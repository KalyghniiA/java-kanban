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
    }
}

