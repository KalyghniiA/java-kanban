package test;

import com.yandex.kanban.exception.DatabaseException;
import com.yandex.kanban.exception.KVClientException;
import com.yandex.kanban.exception.TaskException;
import com.yandex.kanban.server.KVServer;
import com.yandex.kanban.managers.Managers;
import com.yandex.kanban.managers.taskManager.TaskManager;
import com.yandex.kanban.model.EpicTask;
import com.yandex.kanban.model.Subtask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.TaskStatus;
import com.yandex.kanban.server.HttpTaskServer;
import com.yandex.kanban.util.UtilConstant;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class InHistoryTaskManagerTest {
    TaskManager manager;
    KVServer kvServer;
    HttpTaskServer server;
    TestHttpClient testClient = new TestHttpClient();
    String path = "http://localhost:8080/tasks/";
    String pathHistory = path + "history";
    String pathTask = path + "task/";

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
    void createManagers() throws KVClientException, TaskException, DatabaseException, IOException {
        kvServer = new KVServer();
        kvServer.start();


        manager = Managers.getDefaultTaskManager();
        server = new HttpTaskServer(manager);
        server.start();

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
    @AfterEach
    void stopServer() {
        server.stop();
        kvServer.stop();

    }

    @Test
    void getHistoryOneTask() {
        testClient.get(String.format("%s?id=%s", pathTask, task1.getId().toString()));

        String testingList = UtilConstant.GSON.toJson(List.of(task1));

        assertEquals(testingList, testClient.get(pathHistory));

    }

    @Test
    void getHistoryThreeTask() {
        testClient.get(String.format("%s?id=%s", pathTask, task1.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, task2.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, task3.getId().toString()));

        List<Task> testingList = new LinkedList<>();
        testingList.add(task1);
        testingList.add(task2);
        testingList.add(task3);

        assertEquals(testClient.get(pathHistory), UtilConstant.GSON.toJson(testingList));
    }

    @Test
    void getHistoryRepeatTask() {
        testClient.get(String.format("%s?id=%s", pathTask, task1.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, task2.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, task3.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, task1.getId().toString()));

        List<Task> testingList = new LinkedList<>();
        testingList.add(task2);
        testingList.add(task3);
        testingList.add(task1);

        assertEquals(testClient.get(pathHistory), UtilConstant.GSON.toJson(testingList));
    }

    @Test
    void getHistoryRemoveTask() {
        testClient.get(String.format("%s?id=%s", pathTask, task1.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, task2.getId().toString()));
        testClient.delete(String.format("%s?id=%s", pathTask, task1.getId().toString()));

        List<Task> testingList = new LinkedList<>();
        testingList.add(task2);

        assertEquals(testClient.get(pathHistory), UtilConstant.GSON.toJson(testingList));
    }

    @Test
    void getHistoryRemoveTask2() {
        testClient.get(String.format("%s?id=%s", pathTask, task1.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, task2.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, task3.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, epicTask1.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, epicTask2.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, subtask1.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, subtask2.getId().toString()));

        List<Task> testingList = new LinkedList<>();
        testingList.add(task2);
        testingList.add(task3);
        testingList.add(epicTask1);
        testingList.add(epicTask2);
        testingList.add(subtask1);
        testingList.add(subtask2);

        testClient.delete(String.format("%s?id=%s", pathTask, task1.getId().toString()));

        assertEquals(testClient.get(pathHistory), UtilConstant.GSON.toJson(testingList));
    }

    @Test
    void getHistoryRemoveNormalTasks() {
        testClient.get(String.format("%s?id=%s", pathTask, task1.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, task2.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, task3.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, epicTask1.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, epicTask2.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, subtask1.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, subtask2.getId().toString()));

        List<Task> testingList = new LinkedList<>();
        testingList.add(epicTask1);
        testingList.add(epicTask2);
        testingList.add(subtask1);
        testingList.add(subtask2);

        testClient.delete(path + "normal/");



        assertEquals(UtilConstant.GSON.toJson(testingList), testClient.get(pathHistory));
    }

    @Test
    void getHistoryRemoveSubtasks() {
        testClient.get(String.format("%s?id=%s", pathTask, task1.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, task2.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, task3.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, epicTask1.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, epicTask2.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, subtask1.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, subtask2.getId().toString()));

        List<Task> testingList = new LinkedList<>();
        testingList.add(task1);
        testingList.add(task2);
        testingList.add(task3);
        testingList.add(epicTask1);
        testingList.add(epicTask2);

        testClient.delete(path + "subtask/");

        assertEquals(UtilConstant.GSON.toJson(testingList), testClient.get(pathHistory));
    }

    @Test
    void getHistoryRemoveEpicTasks() {
        testClient.get(String.format("%s?id=%s", pathTask, task1.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, task2.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, task3.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, epicTask1.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, epicTask2.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, subtask1.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, subtask2.getId().toString()));

        List<Task> testingList = new LinkedList<>();
        testingList.add(task1);
        testingList.add(task2);
        testingList.add(task3);

        testClient.delete(path + "epic/");



        assertEquals(UtilConstant.GSON.toJson(testingList), testClient.get(pathHistory));
    }

    @Test
    void getHistoryRemoveAllTasks() {
        testClient.get(String.format("%s?id=%s", pathTask, task1.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, task2.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, task3.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, epicTask1.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, epicTask2.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, subtask1.getId().toString()));
        testClient.get(String.format("%s?id=%s", pathTask, subtask2.getId().toString()));

        testClient.delete(pathTask);

        assertEquals("История пуста", testClient.get(pathHistory));
    }

    @Test
    void getEmptyHistory() {
        assertEquals("История пуста", testClient.get(pathHistory));
    }


}
