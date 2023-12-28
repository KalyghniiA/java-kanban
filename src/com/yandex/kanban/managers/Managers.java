package com.yandex.kanban.managers;

import com.yandex.kanban.exception.KVClientException;
import com.yandex.kanban.managers.historyManager.HistoryManager;
import com.yandex.kanban.managers.historyManager.InMemoryHistoryManager;
import com.yandex.kanban.managers.taskManager.FileBackedTasksManager;
import com.yandex.kanban.managers.taskManager.HttpTaskManager;
import com.yandex.kanban.managers.taskManager.InMemoryTaskManager;
import com.yandex.kanban.managers.taskManager.TaskManager;

public final class Managers {
    public static TaskManager getDefaultTaskManager() throws KVClientException {
        return new HttpTaskManager("http://localhost:8078");
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getMemoryTaskManager() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getFileTaskManager() {
        return new FileBackedTasksManager();
    }

    public static TaskManager getHttpTaskManager(String path) throws KVClientException {
        return new HttpTaskManager(path);
    }
}
