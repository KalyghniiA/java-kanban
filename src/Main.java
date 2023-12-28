import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yandex.kanban.exception.DatabaseException;
import com.yandex.kanban.exception.KVClientException;
import com.yandex.kanban.exception.TaskException;
import com.yandex.kanban.kvclient.KVTaskClient;
import com.yandex.kanban.kvserver.KVServer;
import com.yandex.kanban.managers.Managers;
import com.yandex.kanban.managers.taskManager.TaskManager;
import com.yandex.kanban.model.EpicTask;
import com.yandex.kanban.model.Subtask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.TaskStatus;
import com.yandex.kanban.server.HttpTaskServer;
import com.yandex.kanban.server.type_adapters.DurationTypeAdapter;
import com.yandex.kanban.server.type_adapters.LocalDateTimeTypeAdapter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws TaskException, DatabaseException, IOException, KVClientException {
        KVServer kvServer = new KVServer();
        kvServer.start();

        TaskManager manager = Managers.getDefaultTaskManager();
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

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.createTask(epicTask1);

         Subtask subtask1 = new Subtask(
                "subtask1",
                "description",
                TaskStatus.NEW,
                epicTask1.getId()
        );
        manager.createTask(subtask1);



        HttpTaskServer server = new HttpTaskServer(manager);
        server.start();

    }
}

