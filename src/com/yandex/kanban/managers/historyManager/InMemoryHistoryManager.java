package com.yandex.kanban.managers.historyManager;

import com.yandex.kanban.exception.TaskException;
import com.yandex.kanban.model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
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

        if (history.contains(task)) {
            history.remove(task);
        } else if (history.size() == 10) {
            history.remove(0);
        }

        history.add(task);
    }

    @Override
    public void add(List<Task> tasks) {
        for (Task task : tasks) {
            add(task);
        }
    }

    @Override
    public void clear() {
        history.clear();
    }

    @Override
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

    @Override
    public void remove(List<Task> tasks) {
        for (Task task : tasks) {
            remove(task);
        }
    }

    @Override
    public void replace(Task oldTask, Task newTask) {
        try {
            if (oldTask == null || newTask == null) {
                throw new TaskException("Нельзя передавать пустое значение");
            }
        } catch (TaskException e) {
            System.out.println(e.getMessage());
        }

        if (!history.contains(oldTask)) return;

        int index = history.indexOf(oldTask);
        if (index == 9) {
            history.remove(oldTask);
            add(newTask);
            return;
        }
        List<Task> nextTasks = new LinkedList<>();
        for (var i = index + 1; i < history.size(); i++) {
            nextTasks.add(history.remove(i));
        }

        history.remove(oldTask);
        history.add(newTask);
        history.addAll(nextTasks);
    }

    @Override
    public List<Task> getHistory() {
        return new LinkedList<>(history);
    }
}
