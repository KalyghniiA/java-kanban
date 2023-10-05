package com.yandex.kanban.managers;

import com.yandex.kanban.managers.historyManager.HistoryManager;
import com.yandex.kanban.managers.historyManager.InMemoryHistoryManager;
import com.yandex.kanban.managers.taskManager.InMemoryTaskManager;
import com.yandex.kanban.managers.taskManager.TaskManager;

public final class Managers {
    public static TaskManager getDefaultTaskManager() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getMemoryTaskManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }
}
