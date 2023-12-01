package test;

import com.yandex.kanban.managers.Managers;
import com.yandex.kanban.managers.taskManager.TaskManager;
import com.yandex.kanban.model.EpicTask;
import com.yandex.kanban.model.Subtask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InHistoryTaskManagerTest {
    TaskManager manager;
    Task task1 = new Task(
            "task1",
            "description",
            TaskStatus.NEW
    );
    Task task2 = new Task(
            "Task2",
            "description",
            TaskStatus.IN_PROGRESS
    );
    Task task3 = new Task(
            "Task3",
            "description",
            TaskStatus.DONE
    );
    EpicTask epicTask1 = new EpicTask(
            "epicTask1",
            "description"
    );
    EpicTask epicTask2 = new EpicTask(
            "epicTask2",
            "description"
    );
    Subtask subtask1;
    Subtask subtask2;

    @BeforeEach
    void createManagers() {
        manager = Managers.getDefaultTaskManager();
        manager.removeAllTasks();
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.createTask(epicTask1);
        manager.createTask(epicTask2);

        subtask1 = new Subtask(
                "subtask1",
                "description",
                TaskStatus.NEW,
                epicTask2.getId()
        );
        subtask2  = new Subtask(
                "subtask2",
                "description",
                TaskStatus.IN_PROGRESS,
                epicTask2.getId()
        );

        manager.createTask(subtask1);
        manager.createTask(subtask2);

    }

    @Test
    void getHistoryOneTask() {
        manager.getTask(task1.getId());

        List<Task> testingList = List.of(task1);

        assertEquals(testingList, manager.getHistory());
    }

    @Test
    void getHistoryThreeTask() {
        manager.getTask(task1.getId());
        manager.getTask(task2.getId());
        manager.getTask(task3.getId());

        List<Task> testingList = new LinkedList<>();
        testingList.add(task1);
        testingList.add(task2);
        testingList.add(task3);

        assertAll(
                () -> assertTrue(manager.getHistory().containsAll(testingList)),
                () -> assertEquals(manager.getHistory().size(), testingList.size()),
                () -> assertEquals(manager.getHistory().get(0), task1),
                () -> assertEquals(manager.getHistory().get(2), task3)
        );
    }

    @Test
    void getHistoryRepeatTask() {
        manager.getTask(task1.getId());
        manager.getTask(task2.getId());
        manager.getTask(task3.getId());
        manager.getTask(task1.getId());

        List<Task> testingList = new LinkedList<>();
        testingList.add(task2);
        testingList.add(task3);
        testingList.add(task1);

        assertAll(
                () -> assertTrue(testingList.containsAll(manager.getHistory())),
                () -> assertEquals(testingList.size(), manager.getHistory().size()),
                () -> assertEquals(task2, manager.getHistory().get(0)),
                () -> assertEquals(task1, manager.getHistory().get(2))
        );
    }

    @Test
    void getHistoryRemoveTask() {
        manager.getTask(task1.getId());
        manager.getTask(task2.getId());
        manager.removeTask(task1.getId());

        List<Task> testingList = new LinkedList<>();
        testingList.add(task2);

        assertAll(
                () -> assertTrue(testingList.containsAll(manager.getHistory())),
                () -> assertEquals(testingList.size(), manager.getHistory().size())
        );
    }

    @Test
    void getHistoryRemoveTask2() {
        manager.getTask(task1.getId());
        manager.getTask(task2.getId());
        manager.getTask(task3.getId());
        manager.getTask(epicTask1.getId());
        manager.getTask(epicTask2.getId());
        manager.getTask(subtask1.getId());
        manager.getTask(subtask2.getId());

        List<Task> testingList = new LinkedList<>();
        testingList.add(task2);
        testingList.add(task3);
        testingList.add(epicTask1);
        testingList.add(epicTask2);
        testingList.add(subtask1);
        testingList.add(subtask2);

        manager.removeTask(task1.getId());

        assertAll(
                () -> assertTrue(testingList.containsAll(manager.getHistory())),
                () -> assertEquals(testingList.size(), manager.getHistory().size()),
                () -> assertEquals(task2, manager.getHistory().get(0)),
                () -> assertEquals(subtask2, manager.getHistory().get(5))
        );
    }

    @Test
    void getHistoryRemoveNormalTasks() {
        manager.getTask(task1.getId());
        manager.getTask(task2.getId());
        manager.getTask(task3.getId());
        manager.getTask(epicTask1.getId());
        manager.getTask(epicTask2.getId());
        manager.getTask(subtask1.getId());
        manager.getTask(subtask2.getId());

        List<Task> testingList = new LinkedList<>();
        testingList.add(epicTask1);
        testingList.add(epicTask2);
        testingList.add(subtask1);
        testingList.add(subtask2);

        manager.removeAllNormalTask();

        assertAll(
                () -> assertTrue(testingList.containsAll(manager.getHistory())),
                () -> assertEquals(testingList.size(), manager.getHistory().size()),
                () -> assertEquals(epicTask1, manager.getHistory().get(0)),
                () -> assertEquals(subtask2, manager.getHistory().get(3))
        );

    }

    @Test
    void getHistoryRemoveSubtasks() {
        manager.getTask(task1.getId());
        manager.getTask(task2.getId());
        manager.getTask(task3.getId());
        manager.getTask(epicTask1.getId());
        manager.getTask(epicTask2.getId());
        manager.getTask(subtask1.getId());
        manager.getTask(subtask2.getId());

        List<Task> testingList = new LinkedList<>();
        testingList.add(task1);
        testingList.add(task2);
        testingList.add(task3);
        testingList.add(epicTask1);
        testingList.add(epicTask2);

        manager.removeAllSubtasks();

        assertAll(
                () -> assertTrue(testingList.containsAll(manager.getHistory())),
                () -> assertEquals(testingList.size(), manager.getHistory().size()),
                () -> assertEquals(task1, manager.getHistory().get(0)),
                () -> assertEquals(epicTask2, manager.getHistory().get(4))
        );
    }

    @Test
    void getHistoryRemoveEpicTasks() {
        manager.getTask(task1.getId());
        manager.getTask(task2.getId());
        manager.getTask(task3.getId());
        manager.getTask(epicTask1.getId());
        manager.getTask(epicTask2.getId());
        manager.getTask(subtask1.getId());
        manager.getTask(subtask2.getId());

        List<Task> testingList = new LinkedList<>();
        testingList.add(task1);
        testingList.add(task2);
        testingList.add(task3);

        manager.removeAllEpicTask();

        assertAll(
                () -> assertTrue(testingList.containsAll(manager.getHistory())),
                () -> assertEquals(testingList.size(), manager.getHistory().size()),
                () -> assertEquals(task1, manager.getHistory().get(0)),
                () -> assertEquals(task3, manager.getHistory().get(2))
        );
    }

    @Test
    void getHistoryRemoveAllTasks() {
        manager.getTask(task1.getId());
        manager.getTask(task2.getId());
        manager.getTask(task3.getId());
        manager.getTask(epicTask1.getId());
        manager.getTask(epicTask2.getId());
        manager.getTask(subtask1.getId());
        manager.getTask(subtask2.getId());

        manager.removeAllTasks();
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void getHistoryRemoveAllTasksAndGetTask() {
        manager.getTask(task1.getId());
        manager.getTask(task2.getId());
        manager.getTask(task3.getId());
        manager.removeAllTasks();
        manager.createTask(task1);
        manager.getTask(task1.getId());

        assertAll(
                () -> assertTrue(manager.getHistory().contains(task1)),
                () -> assertEquals(1, manager.getHistory().size())
        );
    }

    @Test
    void getEmptyHistory() {
        List<Task> history = manager.getHistory();
        assertTrue(history.isEmpty());
    }
}
