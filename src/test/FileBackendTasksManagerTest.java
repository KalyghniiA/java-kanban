package test;

import com.yandex.kanban.managers.Managers;
import com.yandex.kanban.managers.taskManager.FileBackedTasksManager;
import com.yandex.kanban.managers.taskManager.TaskManager;
import com.yandex.kanban.model.EpicTask;
import com.yandex.kanban.model.Subtask;
import com.yandex.kanban.model.TaskStatus;
import com.yandex.kanban.util.PathConstant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackendTasksManagerTest extends TaskManagerTest<TaskManager>{


    @BeforeEach
    @Override
    void createManager() {
        super.createManager();
        manager = Managers.getDefaultTaskManager();
    }

    @AfterEach
    void clearDatabase() {
        try(PrintWriter pw = new PrintWriter(PathConstant.RESOURCE + PathConstant.FILE_NAME)) {
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
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

    @Test
    void checkEmptyFile() {
        assertAll(
                () -> assertTrue(manager.getAllTasks().isEmpty()),
                () -> assertTrue(manager.getHistory().isEmpty())
        );
    }

    @Test
    void checkEpic() {
        manager.createTask(epicTask1);

        TaskManager manager2 = new FileBackedTasksManager();

        assertAll(
                () -> assertEquals(manager2.getTask(epicTask1.getId()), epicTask1),
                () -> assertTrue(((EpicTask)manager2.getTask(epicTask1.getId())).getSubtasksId().isEmpty())
        );
    }
}
