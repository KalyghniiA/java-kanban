package manager;

import database.Database;
import exceptions.DatabaseException;
import exceptions.TaskException;
import task.Task;
import task.epicTask.EpicTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Manager {
    Database database;

    public Manager() {
        database = new Database();
    }

    public void createTask(Task task) {
        try {
            database.addTask(task);
        } catch (DatabaseException | TaskException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Task> getAllTasks() {
        return database.getTasks();
    }

    public void removeAllTasks() {
        database.clearDatabase();
    }

    public Task getTask(UUID id) {
        try {
            return database.getTask(id);
        } catch (DatabaseException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void replaceTask(Task task) {
        try {
            database.replaceTask(task);
        } catch (DatabaseException e) {
            System.out.println(e.getMessage());
        }
    }

    public void removeTask(UUID id) {
        try {
            database.removeTask(id);
        } catch (DatabaseException | TaskException e) {
            System.out.println(e.getMessage());
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
            try {
                subtasks.add(database.getTask(subtaskId));
            } catch (DatabaseException e) {
                System.out.println(e.getMessage());
            }
        }

        return subtasks;
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