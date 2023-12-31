package test;

import com.yandex.kanban.exception.DatabaseException;
import com.yandex.kanban.exception.KVClientException;
import com.yandex.kanban.exception.TaskException;
import com.yandex.kanban.server.KVServer;
import com.yandex.kanban.managers.Managers;
import com.yandex.kanban.managers.taskManager.TaskManager;
import com.yandex.kanban.model.Subtask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.TaskStatus;
import com.yandex.kanban.server.HttpTaskServer;
import com.yandex.kanban.util.UtilConstant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTest extends TaskManagerTest<TaskManager> {
    KVServer kvServer;
    HttpTaskServer taskServer;
    TestHttpClient client;
    String path = "http://localhost:8080/tasks/";

    @BeforeEach
    @Override
    void createManager() throws IOException, KVClientException {
        super.createManager();
        kvServer = new KVServer();
        kvServer.start();

        manager = Managers.getDefaultTaskManager();

        taskServer = new HttpTaskServer(manager);
        taskServer.start();

        client = new TestHttpClient();
    }

    @AfterEach
    void stop() {
        taskServer.stop();
        kvServer.stop();
    }

    @Test
    void addTaskFromJson() {
        client.post(path + "task/", UtilConstant.GSON.toJson(task1));

        assertAll(
                () -> assertFalse(manager.getAllTasks().isEmpty()),
                () -> assertEquals(1, manager.getAllTasks().size()),
                () -> assertEquals(task1.getName(), manager.getAllTasks().get(0).getName())
        );
    }

    @Test
    void addEpicTaskFromJson() {
        client.post(path + "task/", UtilConstant.GSON.toJson(epicTask1));

        assertAll(
                () -> assertFalse(manager.getAllTasks().isEmpty()),
                () -> assertEquals(1, manager.getAllTasks().size()),
                () -> assertEquals(epicTask1.getName(), manager.getAllTasks().get(0).getName())
        );
    }

    @Test
    void addSubtasksFromJson() {
        client.post(path + "task/", UtilConstant.GSON.toJson(epicTask2));
        UUID epicTaskId = manager.getAllEpicTask().get(0).getId();
        subtask1 = new Subtask(
                "subtask1",
                "description",
                TaskStatus.NEW,
                epicTaskId
        );
        client.post(path + "task/", UtilConstant.GSON.toJson(subtask1));

        assertAll(
                () -> assertFalse(manager.getAllTasks().isEmpty()),
                () -> assertEquals(1, manager.getAllSubtask().size()),
                () -> assertEquals(subtask1.getName(), manager.getAllTasks().get(1).getName())
        );

    }

    @Test
    void getTask() throws TaskException, DatabaseException, KVClientException {
        manager.createTask(task1);

        Task testTask = UtilConstant.GSON.fromJson(client.get(String.format("%stask/?id=%s", path, task1.getId().toString())), Task.class);

        assertAll(
                () -> assertEquals(task1.getName(), testTask.getName()),
                () -> assertEquals(task1.getDescription(), testTask.getDescription()),
                () -> assertEquals(task1.getStatus(), testTask.getStatus())
        );
    }

    @Test
    void getAllTasks() throws TaskException, DatabaseException, KVClientException {
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
        manager.createTask(subtask1);

        List<Task> testingList = new ArrayList<>();
        testingList.add(task1);
        testingList.add(task2);
        testingList.add(task3);
        testingList.add(epicTask1);
        testingList.add(epicTask2);
        testingList.add(subtask1);

        String answer = client.get(path + "task/");
        String testingString = UtilConstant.GSON.toJson(testingList);

        assertAll(
                () -> assertEquals(answer.length(), testingString.length()),
                () -> assertTrue(answer.contains(task1.getId().toString())),
                () -> assertTrue(answer.contains(task2.getId().toString())),
                () -> assertTrue(answer.contains(task3.getId().toString())),
                () -> assertTrue(answer.contains(epicTask1.getId().toString())),
                () -> assertTrue(answer.contains(epicTask2.getId().toString())),
                () -> assertTrue(answer.contains(subtask1.getId().toString()))
        );
    }

    @Test
    void getAllNormalTasks() throws TaskException, DatabaseException, KVClientException {
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
        manager.createTask(subtask1);

        List<Task> testingList = new ArrayList<>();
        testingList.add(task1);
        testingList.add(task2);
        testingList.add(task3);

        String answer = client.get(path + "normal/");
        String testingString = UtilConstant.GSON.toJson(testingList);

        assertAll(
                () -> assertEquals(answer.length(), testingString.length()),
                () -> assertTrue(answer.contains(task1.getId().toString())),
                () -> assertTrue(answer.contains(task2.getId().toString())),
                () -> assertTrue(answer.contains(task3.getId().toString())),
                () -> assertFalse(answer.contains(epicTask1.getId().toString()))
        );

    }

    @Test
    void getAllEpicTask() throws TaskException, DatabaseException, KVClientException {
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
        manager.createTask(subtask1);

        List<Task> testingList = new ArrayList<>();
        testingList.add(epicTask1);
        testingList.add(epicTask2);


        String answer = client.get(path + "epic/");
        String testingString = UtilConstant.GSON.toJson(testingList);

        assertAll(
                () -> assertEquals(answer.length(), testingString.length()),
                () -> assertTrue(answer.contains(epicTask1.getId().toString())),
                () -> assertTrue(answer.contains(epicTask2.getId().toString())),
                () -> assertFalse(answer.contains(task1.getId().toString()))
        );
    }

    @Test
    void getAllSubtasks() throws TaskException, DatabaseException, KVClientException {
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

        List<Task> testingList = new ArrayList<>();
        testingList.add(subtask1);
        testingList.add(subtask2);

        String answer = client.get(path + "subtask/");
        String testingString = UtilConstant.GSON.toJson(testingList);

        assertAll(
                () -> assertEquals(answer.length(), testingString.length()),
                () -> assertTrue(answer.contains(subtask2.getId().toString())),
                () -> assertTrue(answer.contains(subtask1.getId().toString())),
                () -> assertFalse(answer.contains(task1.getId().toString()))
        );
    }

    @Test
    void getPriorityTask() throws TaskException, DatabaseException, KVClientException {
        manager.createTask(task1);
        manager.createTask(task4);
        manager.createTask(task5);


        List<Task> priorityList = new LinkedList<>();
        priorityList.add(task4);
        priorityList.add(task5);
        priorityList.add(task1);


        String testingString = UtilConstant.GSON.toJson(priorityList);
        String answer = client.get(path);


        assertEquals(testingString,answer);
    }

    @Test
    void getHistory() throws TaskException, DatabaseException, KVClientException {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.createTask(task4);
        manager.createTask(task5);

        client.get(String.format("%stask/?id=%s", path, task1.getId()));
        client.get(String.format("%stask/?id=%s", path, task5.getId()));
        client.get(String.format("%stask/?id=%s", path, task3.getId()));

        List<Task> historyList = new LinkedList<>();
        historyList.add(task1);
        historyList.add(task5);
        historyList.add(task3);

        String testingString = UtilConstant.GSON.toJson(historyList);
        String answer = client.get(path + "history/");

        assertEquals(testingString, answer);
    }

    @Test
    void getSubtasksToEpicTask() throws TaskException, DatabaseException, KVClientException {
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


        String testingString = UtilConstant.GSON.toJson(List.of(subtask1, subtask2));
        String answer = client.get(String.format("%ssubtask/epic/?id=%s", path, epicTask2.getId()));

        assertAll(
                () -> assertEquals(testingString.length(), answer.length()),
                () -> assertTrue(answer.contains(subtask1.getId().toString())),
                () -> assertTrue(answer.contains(subtask2.getId().toString()))
        );
    }

    @Test
    void deleteAllTasks() throws TaskException, DatabaseException, KVClientException {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.createTask(epicTask1);
        manager.createTask(epicTask2);

        client.delete(path + "task/");
        String testResult = "На данный момент база пуста";

        assertEquals(testResult, client.get(path + "task/"));
    }

    @Test
    void deleteAllEpic() throws TaskException, DatabaseException, KVClientException {
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



        String testAnswer = "Все задачи со статусом Эпик удалены";
        String testResult = UtilConstant.GSON.toJson(List.of(task1, task2, task3));

        assertAll(
                () -> assertEquals(testAnswer, client.delete(path + "epic/")),
                () -> assertEquals(testResult.length(), client.get(path + "task/").length()),
                () -> assertTrue(client.get(path + "task/").contains(task1.getId().toString())),
                () -> assertFalse(client.get(path + "task/").contains(epicTask1.getId().toString())),
                () -> assertFalse(client.get(path + "task/").contains(subtask1.getId().toString()))
        );
    }

    @Test
    void deleteAllSubtask() throws TaskException, DatabaseException, KVClientException {
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



        String testAnswer = "Все задачи удалены";

        assertAll(
                () -> assertEquals(testAnswer, client.delete(path + "subtask/")),
                () -> assertEquals(UtilConstant.GSON.toJson(List.of(task1, task2, task3, epicTask1, epicTask2)).length(), client.get(path + "task/").length()),
                () -> assertTrue(client.get(path + "task/").contains(task1.getId().toString())),
                () -> assertTrue(client.get(path + "task/").contains(epicTask1.getId().toString())),
                () -> assertFalse(client.get(path + "task/").contains(subtask1.getId().toString()))
        );
    }
}
