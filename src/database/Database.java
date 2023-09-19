package database;

import exceptions.DatabaseException;
import exceptions.TaskException;
import task.Task;
import task.epicTask.EpicTask;
import task.subtask.Subtask;

import java.util.*;

public class Database {
    Map<UUID, Task> database;

    public Database() {
        database = new HashMap<>();
    }

    public List<Task> getTasks() {
        return new ArrayList<>(database.values());
    }

    public Task getTask(UUID id) throws DatabaseException {
        if (id == null) {
            throw new DatabaseException("Передано пустое значение");
        }

        if (!database.containsKey(id)) {
            throw new DatabaseException("Данной задачи нет");
        }

        return database.get(id);
    }
    public void addTask(Task task) throws DatabaseException, TaskException {
        if (task == null) {
            throw new DatabaseException("Передан пустой элемент");
        }

        if (database.containsKey(task.getId())) {
            throw new DatabaseException("Данная задача уже существует");
        }



        database.put(task.getId(), task);

        if (task instanceof Subtask) {
            if (!database.containsKey(((Subtask) task).getEpicTaskId())) {
                throw new DatabaseException("Для данной подзадачи нет главной задачи");
            }

            EpicTask epicTask = (EpicTask) database.get(((Subtask) task).getEpicTaskId());
            epicTask.addSubtaskId(task.getId());
            checkStatusToEpicTask(epicTask);
        }
    }

    public void addTasks(List<Task> tasks) throws DatabaseException, TaskException {
        for (Task task: tasks) {
            addTask(task);
        }
    }

    public void removeTask(UUID id) throws DatabaseException, TaskException {
        if (id == null) {
            throw new DatabaseException("Передан пустой элемент");
        }

        if (!database.containsKey(id)) {
            throw new DatabaseException("Данной задачи нет");
        }

        Task task = database.get(id);

        if (task instanceof EpicTask) {
            List<UUID> subtasksId = ((EpicTask) task).getSubtasksId();
            for (UUID subtaskId: subtasksId) {
                removeTask(subtaskId);
            }
        }



        database.remove(id);

        if (task instanceof Subtask) {
            EpicTask epicTask = (EpicTask) database.get(((Subtask) task).getEpicTaskId());
            epicTask.removeSubtaskId(id);
            checkStatusToEpicTask(epicTask);
        }

    }

    public void removeTask(Task task) throws TaskException, DatabaseException {
        removeTask(task.getId());
    }

    public void replaceTask(Task task) throws DatabaseException {
        if (task == null) {
            throw new DatabaseException("Передано пустое значение");
        }

        if (!database.containsKey(task.getId())) {
            throw new DatabaseException("Данной задачи нет");
        }



        database.replace(task.getId(), task);

        if (task instanceof Subtask) {
            EpicTask epicTask = (EpicTask) database.get(((Subtask) task).getEpicTaskId());
            checkStatusToEpicTask(epicTask);
        }
    }

    public void clearDatabase() {
        database.clear();
    }

    private void checkStatusToEpicTask(EpicTask task) {
        List<Subtask> subtasks = new ArrayList<>();
        List<UUID> subtasksId = task.getSubtasksId();
        for (UUID subtaskId: subtasksId) {
            subtasks.add((Subtask) database.get(subtaskId));
        }
        task.checkStatus(subtasks);
    }

}
