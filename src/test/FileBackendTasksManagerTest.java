package test;

import com.yandex.kanban.managers.taskManager.FileBackedTasksManager;
import com.yandex.kanban.managers.taskManager.TaskManager;
import com.yandex.kanban.model.EpicTask;
import com.yandex.kanban.model.Subtask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class FileBackendTasksManagerTest {
    TaskManager manager;
    Task task1;
    Task task2;
    Task task3;
    EpicTask epicTask1;
    EpicTask epicTask2;
    Subtask subtask1;
    Subtask subtask2;

    @BeforeEach
    void createManager() {
        manager = new FileBackedTasksManager();
        task1 = new Task(
                "task1",
                "description",
                TaskStatus.NEW
        );
        task2 = new Task(
                "Task2",
                "description",
                TaskStatus.IN_PROGRESS
        );
        task3 = new Task(
                "Task3",
                "description",
                TaskStatus.DONE
        );
        epicTask1 = new EpicTask(
                "epicTask1",
                "description"
        );
        epicTask2 = new EpicTask(
                "epicTask2",
                "description"
        );
    }

    @Test
    void createTask() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.createTask(epicTask2);
        subtask1 = new Subtask(
                "subtask1",
                "description",
                TaskStatus.NEW,
                epicTask2.getId()
        );
        manager.createTask(subtask1);
        subtask2  = new Subtask(
                "subtask2",
                "description",
                TaskStatus.IN_PROGRESS,
                epicTask2.getId()
        );
        manager.createTask(subtask2);

        TaskManager manager2 = new FileBackedTasksManager();

        assertAll(
                () -> assertEquals(manager.getAllTasks().size(), manager2.getAllTasks().size()),
                () -> assertEquals(manager.getHistory().size(), manager2.getHistory().size()),
                () -> assertEquals(manager.getTask(epicTask2.getId()).getStatus(), manager2.getTask(epicTask2.getId()).getStatus())
        );
    }

    @Test
    void clear() {
        manager.removeAllTasks();

        TaskManager manager2 = new FileBackedTasksManager();

        assertAll(
                () -> assertTrue(manager2.getAllTasks().isEmpty()),
                () -> assertTrue(manager2.getHistory().isEmpty())
        );
    }

    @Test
    void historyTest() {
        manager.createTask(task1);
        manager.getTask(task1.getId());

        TaskManager manager2 = new FileBackedTasksManager();

        assertEquals(manager.getHistory().size(), manager2.getHistory().size());

    }
}
