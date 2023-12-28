package com.yandex.kanban.managers.taskManager;

import com.yandex.kanban.exception.DatabaseException;
import com.yandex.kanban.exception.KVClientException;
import com.yandex.kanban.exception.TaskException;
import com.yandex.kanban.model.Task;

import java.util.List;
import java.util.UUID;

public interface TaskManager {
    void createTask(Task task) throws DatabaseException, TaskException, KVClientException;

    void createTasks(List<Task> tasks) throws DatabaseException, TaskException, KVClientException;

    List<Task> getAllTasks();

    List<Task> getAllNormalTask();

    List<Task> getAllSubtask();

    List<Task> getAllEpicTask();

    void removeAllTasks() throws DatabaseException, TaskException, KVClientException;

    void removeAllNormalTask() throws KVClientException;

    void removeAllSubtasks() throws KVClientException;

    void removeAllEpicTask() throws KVClientException;

    Task getTask(UUID id) throws DatabaseException, KVClientException;

    void replaceTask(Task task) throws DatabaseException, TaskException, KVClientException;

    void removeTask(UUID id) throws DatabaseException, TaskException, KVClientException;

    List<Task> getSubtasksToEpicTask(UUID id) throws DatabaseException, TaskException, KVClientException;

    List<Task> getHistory();
    List<Task> getPriority();
}
