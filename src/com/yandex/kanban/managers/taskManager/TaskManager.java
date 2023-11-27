package com.yandex.kanban.managers.taskManager;

import com.yandex.kanban.model.Task;

import java.util.List;
import java.util.UUID;

public interface TaskManager {
    void createTask(Task task);

    void createTasks(List<Task> tasks);

    List<Task> getAllTasks();

    List<Task> getAllNormalTask();

    List<Task> getAllSubtask();

    List<Task> getAllEpicTask();

    void removeAllTasks();

    void removeAllNormalTask();

    void removeAllSubtasks();

    void removeAllEpicTask();

    Task getTask(UUID id);

    void replaceTask(Task task);

    void removeTask(UUID id);

    List<Task> getSubtasksToEpicTask(UUID id);

    List<Task> getHistory();
    List<Task> getPriority();
}
