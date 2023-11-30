package test;

import com.yandex.kanban.exception.TaskException;
import com.yandex.kanban.managers.Managers;
import com.yandex.kanban.managers.taskManager.InMemoryTaskManager;
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

class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager>{


    @BeforeEach
    @Override
    void createManager() {
        super.createManager();
        manager = Managers.getMemoryTaskManager();

    }


}
