package com.yandex.kanban.managers;

import com.yandex.kanban.managers.historyManager.HistoryManager;
import com.yandex.kanban.managers.historyManager.InMemoryHistoryManager;
import com.yandex.kanban.managers.taskManager.FileBackedTasksManager;
import com.yandex.kanban.managers.taskManager.InMemoryTaskManager;
import com.yandex.kanban.managers.taskManager.TaskManager;

public final class Managers {
    public static TaskManager getDefaultTaskManager() {
        return new FileBackedTasksManager();
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getMemoryTaskManager() {
        return new InMemoryTaskManager();
    }
}
