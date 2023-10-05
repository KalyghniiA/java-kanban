package com.yandex.kanban.managers.historyManager;

import com.yandex.kanban.model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    void add(List<Task> tasks);

    void clear();

    void remove(Task task);

    void remove(List<Task> tasks);

    void replace(Task oldTask, Task task);

    List<Task> getHistory();
}
