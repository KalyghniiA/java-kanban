package com.yandex.kanban.managers.historyManager;

import com.yandex.kanban.exception.TaskException;
import com.yandex.kanban.model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final static int MAX_SIZE = 10;
    private final List<Task> history = new LinkedList<>();
    @Override
    public void add(Task task) {
        try {
            if (task == null) {
                throw new TaskException("Нельзя передавать пустое значение");
            }
        } catch (TaskException e) {
            System.out.println(e.getMessage());
            return;
        }

        if (history.size() == MAX_SIZE) {
            history.remove(0);
        }

        history.add(task);
    }

    public void add(List<Task> tasks) {
        for (Task task : tasks) {
            add(task);
        }
    }

    public void clear() {
        history.clear();
    }

    public void remove(Task task) {
        try {
            if (task == null) {
                throw new TaskException("Нельзя передавать пустое значение");
            }
        } catch (TaskException e) {
            System.out.println(e.getMessage());
            return;
        }

        history.remove(task);
    }

    public void remove(List<Task> tasks) {
        for (Task task : tasks) {
            remove(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(history);
    }
}
