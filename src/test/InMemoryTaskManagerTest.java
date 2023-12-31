package test;

import com.yandex.kanban.exception.KVClientException;
import com.yandex.kanban.managers.Managers;
import com.yandex.kanban.managers.taskManager.TaskManager;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager>{


    @BeforeEach
    @Override
    void createManager() throws IOException, KVClientException {
        super.createManager();
        manager = Managers.getMemoryTaskManager();

    }


}
