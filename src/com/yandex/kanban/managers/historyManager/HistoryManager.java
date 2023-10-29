package com.yandex.kanban.managers.historyManager;

import com.yandex.kanban.model.Task;

import java.util.List;
import java.util.UUID;

public interface HistoryManager {
    void add(Task task);
    void remove(UUID id);
    List<Task> getHistory();
    void clear();
}
