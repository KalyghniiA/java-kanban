package com.yandex.kanban.managers.taskManager;

import com.yandex.kanban.exception.DatabaseException;
import com.yandex.kanban.exception.TaskException;
import com.yandex.kanban.managers.Managers;
import com.yandex.kanban.managers.historyManager.HistoryManager;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.TaskType;
import com.yandex.kanban.model.EpicTask;
import com.yandex.kanban.model.Subtask;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager{

    protected final Map<UUID, Task> database;
    protected final HistoryManager history;

    public InMemoryTaskManager() {
        database = new HashMap<>();
        history = Managers.getDefaultHistoryManager();
    }

    @Override
    public void createTask(Task task) {
        try {
            if (task == null) {
                throw new DatabaseException("Передан пустой элемент");
            }

            if (database.containsKey(task.getId())) {
                throw new DatabaseException("Данная задача уже существует");
            }
        } catch (DatabaseException e) {
            System.out.println(e.getMessage());
            return;
        }

        database.put(task.getId(), task);

        if (task.getType() == TaskType.SUBTASK) {
            try {
                if (!database.containsKey(((Subtask) task).getEpicTaskId())) {
                    throw new DatabaseException("Для данной подзадачи нет главной задачи");
                }
            } catch (DatabaseException e) {
                System.out.println(e.getMessage());
                return;
            }

            EpicTask epicTask = (EpicTask) database.get(((Subtask) task).getEpicTaskId());
            try {
                epicTask.addSubtaskId(task.getId());
                checkStatusToEpicTask(epicTask);
            } catch (TaskException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public void createTasks(List<Task> tasks) {
        tasks.forEach(this::createTask);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(database.values());
    }

    @Override
    public List<Task> getAllNormalTask() {
        return database.values()
                .stream()
                .filter(task -> task.getType() == TaskType.NORMAL)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getAllSubtask() {
        return database.values()
                .stream()
                .filter(task -> task.getType() == TaskType.SUBTASK)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getAllEpicTask() {
        return database.values()
                .stream()
                .filter(task -> task.getType() == TaskType.EPIC_TASK)
                .collect(Collectors.toList());
    }

    @Override
    public void removeAllTasks() {
        database.clear();
        history.clear();
    }

    @Override
    public void removeAllNormalTask() {
        List<Task> tasks = database.values()
                .stream()
                .filter(task -> task.getType() == TaskType.NORMAL)
                .collect(Collectors.toList());
        tasks.forEach(task -> this.removeTask(task.getId()));
    }

    @Override
    public void removeAllSubtasks() {
        List<Task> tasks = database.values()
                .stream()
                .filter(task -> task.getType() == TaskType.SUBTASK)
                .collect(Collectors.toList());
        tasks.forEach(task -> removeTask(task.getId()));
    }

    @Override
    public void removeAllEpicTask() {
        List<Task> tasks = database.values()
                .stream()
                .filter(task -> task.getType() == TaskType.EPIC_TASK)
                .collect(Collectors.toList());
        tasks.forEach(task -> this.removeTask(task.getId()));
    }

    @Override
    public Task getTask(UUID id) {
        try {
            if (id == null) {
                throw new DatabaseException("Передано пустое значение");
            }

            if (!database.containsKey(id)) {
                throw new DatabaseException("Данной задачи нет");
            }
        } catch (DatabaseException e) {
            System.out.println(e.getMessage());
            return null;
        }
        Task task = database.get(id);

        history.add(task);

        return task;
    }

    @Override
    public void replaceTask(Task task) {
        try {
            if (task == null) {
                throw new DatabaseException("Передано пустое значение");
            }

            if (!database.containsKey(task.getId())) {
                throw new DatabaseException("Данной задачи нет");
            }

            database.replace(task.getId(), task);

            if (task.getType() == TaskType.SUBTASK) {
                EpicTask epicTask = (EpicTask) database.get(((Subtask) task).getEpicTaskId());
                checkStatusToEpicTask(epicTask);
            }
        } catch (DatabaseException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void removeTask(UUID id) {
        try {
            if (id == null) {
                throw new DatabaseException("Передан пустой элемент");
            }

            if (!database.containsKey(id)) {
                throw new DatabaseException("Данной задачи нет");
            }
        } catch (DatabaseException e) {
            System.out.println(e.getMessage());
        }

        Task task = database.remove(id);
        history.remove(task.getId());

        if (task.getType() == TaskType.EPIC_TASK) {
            List<UUID> subtasksId = ((EpicTask) task).getSubtasksId();
            for (UUID subtaskId : subtasksId) {
                database.remove(subtaskId);
                history.remove(subtaskId);
            }
        }


        if (task.getType() == TaskType.SUBTASK) {
            EpicTask epicTask = (EpicTask) database.get(((Subtask) task).getEpicTaskId());
            try {
                epicTask.removeSubtaskId(id);
                checkStatusToEpicTask(epicTask);
            } catch (TaskException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public List<Task> getSubtasksToEpicTask(UUID id) {
        try {
            if (id == null) {
                throw new TaskException("Передано пустое значение");
            }
            if (!database.containsKey(id)) {
                throw new TaskException("Данной задачи нет в базе");
            }
        } catch (TaskException e) {
            System.out.println(e.getMessage());
            return null;
        }

        Task task = database.get(id);
        if (!(task.getType() == TaskType.EPIC_TASK)) {
            System.out.printf("У задачи с переданным id не может быть подзадач. Тип данной задачи: %s%n", task.getClass());
            return null;
        }
        List<Task> subtasks = new ArrayList<>();
        List<UUID> subtasksId = ((EpicTask) task).getSubtasksId();
        for (UUID subtaskId : subtasksId) {
            subtasks.add(getTask(subtaskId));
        }

        return subtasks;
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    private void checkStatusToEpicTask(EpicTask task) {
        List<Subtask> subtasks = new ArrayList<>();
        List<UUID> subtasksId = task.getSubtasksId();
        for (UUID subtaskId : subtasksId) {
            if (database.containsKey(subtaskId)) {
                subtasks.add((Subtask) database.get(subtaskId));
            }
        }
        task.checkStatus(subtasks);
    }

}

