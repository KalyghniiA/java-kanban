package com.yandex.kanban.manager;

import com.yandex.kanban.exception.DatabaseException;
import com.yandex.kanban.exception.TaskException;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.TaskType;
import com.yandex.kanban.model.EpicTask;
import com.yandex.kanban.model.Subtask;

import java.util.*;

public class Manager {

    Map<UUID, Task> database;

    public Manager() {
        database = new HashMap<>();
    }

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

        if (task.getType().equals(TaskType.SUBTASK)) {
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

    public void createTasks(List<Task> tasks) {
        tasks.forEach(this::createTask);
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(database.values());
    }

    public void removeAllTasks() {
        database.clear();
    }

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

        return database.get(id);
    }

    public void replaceTask(Task task) {
        try {
            if (task == null) {
                throw new DatabaseException("Передано пустое значение");
            }

            if (!database.containsKey(task.getId())) {
                throw new DatabaseException("Данной задачи нет");
            }


            database.replace(task.getId(), task);

            if (task.getType().equals(TaskType.SUBTASK)) {
                EpicTask epicTask = (EpicTask) database.get(((Subtask) task).getEpicTaskId());
                checkStatusToEpicTask(epicTask);
            }
        } catch (DatabaseException e) {
            System.out.println(e.getMessage());
        }
    }

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

        Task task = database.get(id);

        if (task.getType().equals(TaskType.EPIC_TASK)) {
            List<UUID> subtasksId = ((EpicTask) task).getSubtasksId();
            for (UUID subtaskId: subtasksId) {
                removeTask(subtaskId);
            }
        }



        database.remove(id);

        if (task.getType().equals(TaskType.SUBTASK)) {
            EpicTask epicTask = (EpicTask) database.get(((Subtask) task).getEpicTaskId());
            try {
                epicTask.removeSubtaskId(id);
                checkStatusToEpicTask(epicTask);
            } catch (TaskException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public List<Task> getSubtasksToEpicTask(UUID id) {
        Task task = getTask(id);
        if (!(task instanceof EpicTask)) {
            System.out.printf("У задачи с переданным id не может быть подзадач. Тип данной задачи: %s%n", task.getClass());
            return null;
        }
        List<Task> subtasks = new ArrayList<>();
        List<UUID> subtasksId = ((EpicTask) task).getSubtasksId();
        for (UUID subtaskId: subtasksId) {
            subtasks.add(getTask(subtaskId));
        }

        return subtasks;
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

/*Возможность хранить задачи всех типов. Для этого вам нужно выбрать подходящую коллекцию.
Методы для каждого из типа задач(Задача/Эпик/Подзадача):
Получение списка всех задач. +
Удаление всех задач. +
Получение по идентификатору. +
Создание. Сам объект должен передаваться в качестве параметра. +
Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра. +
Удаление по идентификатору. +
Дополнительные методы:
Получение списка всех подзадач определённого эпика.
Управление статусами осуществляется по следующему правилу:
Менеджер сам не выбирает статус для задачи. Информация о нём приходит менеджеру вместе с информацией о самой задаче. По этим данным в одних случаях он будет сохранять статус, в других будет рассчитывать.
Для эпиков:+
если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.
если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE.
во всех остальных случаях статус должен быть IN_PROGRESS.*/