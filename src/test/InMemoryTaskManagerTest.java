package test;

import com.yandex.kanban.managers.Managers;
import com.yandex.kanban.managers.taskManager.TaskManager;
import com.yandex.kanban.model.EpicTask;
import com.yandex.kanban.model.Subtask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
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
    void createManager() {
        manager = Managers.getDefaultTaskManager();

    }

    @Test
    void createTask() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        List<Task> testingTasks = List.of(task1, task2, task3);

        assertAll(
                () -> assertTrue(manager.getAllTasks().containsAll(testingTasks)),
                () -> assertEquals(3, manager.getAllTasks().size()),
                () -> assertFalse(manager.getAllTasks().isEmpty())
        );
    }

    @Test
    void createTasks() {
        List<Task> tasks = List.of(task1, task2, task3);

        List<Task> testingTasks = List.of(task1, task2, task3);

        manager.createTasks(tasks);

        assertAll(
                () -> assertTrue(manager.getAllTasks().containsAll(testingTasks)),
                () -> assertEquals(3, manager.getAllTasks().size()),
                () -> assertFalse(manager.getAllTasks().isEmpty())
        );
    }

    @Test
    void createSubtaskNotEpic() {
        manager.createTask(task1);
        manager.createTask(subtask1);

        subtask1 = new Subtask(
                "subtask1",
                "description",
                TaskStatus.NEW,
                epicTask2.getId()
        );

        List<Task> testingList = List.of(task1,subtask1);

        assertAll(
                () -> assertFalse(manager.getAllTasks().containsAll(testingList)),
                () -> assertNull(manager.getTask(subtask1.getId())),
                () -> assertEquals(List.of(task1), manager.getAllTasks())
        );
    }

    @Test
    void createTasksRepeatTask() {
        manager.createTask(task1);
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);


        List<Task> testingTasks = List.of(task1, task2, task3);

        assertAll(
                () -> assertTrue(manager.getAllTasks().containsAll(testingTasks)),
                () -> assertEquals(3, manager.getAllTasks().size()),
                () -> assertFalse(manager.getAllTasks().isEmpty())
        );
    }

    @Test
    void emptyManager() {
        assertAll(
                () -> assertTrue(manager.getAllTasks().isEmpty()),
                () -> assertNotNull(manager.getAllTasks())
        );
    }

    @Test
    void getTask() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        assertAll(
                () -> assertEquals(manager.getTask(task1.getId()), task1),
                () -> assertEquals(manager.getTask(task2.getId()), task2),
                () -> assertNotNull(manager.getTask(task3.getId())),
                () -> assertNull(manager.getTask(epicTask1.getId()))
        );
    }

    @Test
    void getNormalTasks() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.createTask(epicTask1);
        manager.createTask(epicTask2);

        List<Task> testingList = List.of(task1, task2, task3);

        assertAll(
                () -> assertTrue(manager.getAllNormalTask().containsAll(testingList)),
                () -> assertEquals(3, manager.getAllNormalTask().size()),
                () -> assertNotNull(manager.getAllNormalTask())
        );
    }

    @Test
    void getNormalTasksIsEmptyList() {
        manager.createTask(epicTask1);
        manager.createTask(epicTask2);

        assertAll(
                () -> assertTrue(manager.getAllNormalTask().isEmpty()),
                () -> assertNotNull(manager.getAllNormalTask())
        );
    }

    @Test
    void getEpicTasks() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.createTask(epicTask1);
        manager.createTask(epicTask2);

        List<Task> testingList = List.of(epicTask1, epicTask2);

        assertAll(
                () -> assertTrue(manager.getAllEpicTask().containsAll(testingList)),
                () -> assertEquals(2, manager.getAllEpicTask().size()),
                () -> assertNotNull(manager.getAllEpicTask())
        );
    }

    @Test
    void getEpicTasksIsEmptyList() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        assertAll(
                () -> assertTrue(manager.getAllEpicTask().isEmpty()),
                () -> assertNotNull(manager.getAllEpicTask())
        );
    }

    @Test
    void getSubtasks() {
        manager.createTask(epicTask1);
        manager.createTask(epicTask2);

        //Очередная проблема с созданием id
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

        List<Task> testingList = List.of(subtask1, subtask2);


        assertAll(
                () -> assertTrue(manager.getAllSubtask().containsAll(testingList)),
                () -> assertEquals(2, manager.getAllSubtask().size()),
                () -> assertNotNull(manager.getAllSubtask())
        );
    }

    @Test
    void getSubtasksIsEmptyList() {
        manager.createTask(task1);

        assertAll(
                () -> assertTrue(manager.getAllSubtask().isEmpty()),
                () -> assertNotNull(manager.getAllSubtask())
        );
    }

    @Test
    void removeTask() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.removeTask(task1.getId());

        List<Task> testingList = List.of(task2, task3);

        assertAll(
                () -> assertTrue(manager.getAllTasks().containsAll(testingList)),
                () -> assertEquals(2, manager.getAllTasks().size())
        );
    }

    @Test
    void removeSubtask() {
        manager.createTask(epicTask2);

        //Очередная проблема с созданием id
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



        manager.removeTask(subtask2.getId());

        List<Task> testingListTasks = List.of(epicTask2, subtask1);
        List<UUID> testingListIdSubtask = List.of(subtask1.getId());

        assertAll(
                () -> assertTrue(manager.getAllTasks().containsAll(testingListTasks)),
                () -> assertEquals(testingListIdSubtask, epicTask2.getSubtasksId())
        );
    }

    @Test
    void removeEpicTask() {
        manager.createTask(epicTask1);
        manager.createTask(epicTask2);

        //Очередная проблема с созданием id
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
        manager.removeTask(epicTask2.getId());

        List<Task> testingList = List.of(epicTask1);

        assertAll(
                () -> assertEquals(testingList, manager.getAllTasks()),
                () -> assertTrue(manager.getAllSubtask().isEmpty()),
                () -> assertNull(manager.getTask(subtask1.getId()))
        );
    }

    @Test
    void removeAllTasks() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.removeAllTasks();

        assertAll(
                () -> assertTrue(manager.getAllTasks().isEmpty()),
                () -> assertNull(manager.getTask(task1.getId()))
        );
    }

    @Test
    void removeAllNormalTasks() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.createTask(epicTask1);
        manager.createTask(epicTask2);

        //Очередная проблема с созданием id
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
        manager.removeAllNormalTask();

        List<Task> testingTasks = List.of(epicTask1, epicTask2, subtask1, subtask2);

        assertAll(
                () -> assertTrue(manager.getAllTasks().containsAll(testingTasks)),
                () -> assertEquals(4, manager.getAllTasks().size()),
                () -> assertTrue(manager.getAllNormalTask().isEmpty())
        );
    }

    @Test
    void removeAllSubtasks() {
        manager.createTask(epicTask1);
        manager.createTask(epicTask2);

        //Очередная проблема с созданием id
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
        manager.removeAllSubtasks();

        List<Task> testingList = List.of(epicTask1, epicTask2);

        assertAll(
                () -> assertTrue(manager.getAllTasks().containsAll(testingList)),
                () -> assertEquals(testingList.size(), manager.getAllTasks().size()),
                () -> assertTrue(manager.getAllSubtask().isEmpty()),
                () -> assertTrue(epicTask2.getSubtasksId().isEmpty())
        );
    }

    @Test
    void removeAllEpicTasks() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.createTask(epicTask1);
        manager.createTask(epicTask2);

        //Очередная проблема с созданием id
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
        manager.removeAllEpicTask();

        List<Task> testingList = List.of(task1, task2, task3);

        assertAll(
                () -> assertTrue(testingList.containsAll(manager.getAllTasks())),
                () -> assertEquals(testingList.size(), manager.getAllTasks().size()),
                () -> assertTrue(manager.getAllEpicTask().isEmpty()),
                () -> assertTrue(manager.getAllSubtask().isEmpty())
        );
    }

    @Test
    void getSubtasksToEpicTask() {
        manager.createTask(epicTask1);
        manager.createTask(epicTask2);

        //Очередная проблема с созданием id
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

        List<Task> testingList = List.of(subtask1, subtask2);

        assertAll(
                () -> assertTrue(testingList.containsAll(manager.getSubtasksToEpicTask(epicTask2.getId()))),
                () -> assertEquals(testingList.size(), manager.getSubtasksToEpicTask(epicTask2.getId()).size()),
                () -> assertTrue(manager.getSubtasksToEpicTask(epicTask1.getId()).isEmpty())
        );
    }
}
