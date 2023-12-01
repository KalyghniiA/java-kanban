package test;

import com.yandex.kanban.managers.Managers;
import com.yandex.kanban.managers.taskManager.TaskManager;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager>{


    @BeforeEach
    @Override
    void createManager() {
        super.createManager();
        manager = Managers.getMemoryTaskManager();

    }


}
