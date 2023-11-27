package com.yandex.kanban.managers.taskManager;

import com.yandex.kanban.exception.DatabaseException;
import com.yandex.kanban.exception.TaskException;
import com.yandex.kanban.managers.Managers;
import com.yandex.kanban.managers.historyManager.HistoryManager;
import com.yandex.kanban.model.*;
import com.yandex.kanban.util.UtilConstant;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager{

    protected final Map<UUID, Task> database;
    protected final HistoryManager history;
    protected final Set<Task> priorityTasks = new TreeSet<>((o1, o2) -> {
        if (o1.getStartTime() == null && o2.getStartTime() == null) return o1.getId().compareTo(o2.getId());
        if (o1.getStartTime() == null) return 1;
        if (o2.getStartTime() == null) return -1;

        LocalDateTime startTimeO1 = LocalDateTime.parse(o1.getStartTime(), UtilConstant.DATE_TIME_FORMATTER);
        LocalDateTime startTimeO2 = LocalDateTime.parse(o2.getStartTime(), UtilConstant.DATE_TIME_FORMATTER);

        return startTimeO1.compareTo(startTimeO2);
    });

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
            if (intersectionCheck(task)) {
                throw new TaskException("Время выполнения данной задачи пересекается с действующими задачами");
            };
        } catch (DatabaseException | TaskException e) {
            System.out.println(e.getMessage());
            return;
        }

        task.setId(UUID.randomUUID());
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
        checkPriority();
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
        checkPriority();
    }

    @Override
    public void removeAllNormalTask() {
        List<Task> tasks = database.values()
                .stream()
                .filter(task -> task.getType() == TaskType.NORMAL)
                .collect(Collectors.toList());
        tasks.forEach(task -> this.removeTask(task.getId()));
        checkPriority();
    }

    @Override
    public void removeAllSubtasks() {
        List<Task> tasks = database.values()
                .stream()
                .filter(task -> task.getType() == TaskType.SUBTASK)
                .collect(Collectors.toList());
        tasks.forEach(task -> removeTask(task.getId()));
        checkPriority();
    }

    @Override
    public void removeAllEpicTask() {
        List<Task> tasks = database.values()
                .stream()
                .filter(task -> task.getType() == TaskType.EPIC_TASK)
                .collect(Collectors.toList());
        tasks.forEach(task -> this.removeTask(task.getId()));
        checkPriority();
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

            if (intersectionCheck(task)) {
                throw new TaskException("Данная задача пресекается по времени с действующими задачами");
            }

            database.replace(task.getId(), task);

            if (task.getType() == TaskType.SUBTASK) {
                EpicTask epicTask = (EpicTask) database.get(((Subtask) task).getEpicTaskId());
                checkStatusToEpicTask(epicTask);
            }
            checkPriority();
        } catch (DatabaseException | TaskException e) {
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
        checkPriority();
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

    @Override
    public List<Task> getPriority() {
        return List.copyOf(priorityTasks);
    }

    protected void checkStatusToEpicTask(EpicTask task) {
        try {
            if (task == null) {
                throw new TaskException("Передано пустое значение");
            }
        } catch (TaskException e) {
            System.out.println(e.getMessage());
            return;
        }

        List<Subtask> subtasks = new ArrayList<>();
        List<UUID> subtasksId = task.getSubtasksId();
        for (UUID subtaskId : subtasksId) {
            if (database.containsKey(subtaskId)) {
                subtasks.add((Subtask) database.get(subtaskId));
            }
        }
        task.checkStatus(subtasks);
    }

    protected void checkPriority() {
        this.priorityTasks.clear();
        for(Task taskData: this.database.values()) {
            if (taskData.getType() == TaskType.EPIC_TASK || taskData.getStatus() == TaskStatus.DONE) continue;
            this.priorityTasks.add(taskData);
        }
    }

    protected boolean intersectionCheck(Task task) {
       if (task.getStartTime() == null || task.getEndTime() == null) return false;

       LocalDateTime startTimeTask = LocalDateTime.parse(task.getStartTime(), UtilConstant.DATE_TIME_FORMATTER);
       LocalDateTime endTimeTask = LocalDateTime.parse(task.getEndTime(), UtilConstant.DATE_TIME_FORMATTER);

       List<Task> tasks = priorityTasks.stream().collect(Collectors.toList());
       int left = 0;
       int right = tasks.size() - 1;

       while (left <= right) {
           int mid = (left + right) / 2;
           Task elem = tasks.get(mid);

           if (elem.getStartTime() == null) {
               right = mid - 1;
               continue;
           }

           LocalDateTime currentStart = LocalDateTime.parse(elem.getStartTime(), UtilConstant.DATE_TIME_FORMATTER);
           LocalDateTime currentEnd = LocalDateTime.parse(elem.getEndTime(), UtilConstant.DATE_TIME_FORMATTER);

           int compare = currentStart.compareTo(startTimeTask);

           if (compare == 0) {
               return true;
           } else if (compare < 0) {
               if (currentEnd.isAfter(startTimeTask)) {
                   return true;
               }
               left = mid + 1;
           } else {
               if (endTimeTask.isAfter(currentStart)) {
                   return true;
               }
               right = mid - 1;
           }

       }
       return false;
    }

}

