package com.yandex.kanban.managers.taskManager;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yandex.kanban.exception.DatabaseException;
import com.yandex.kanban.exception.KVClientException;
import com.yandex.kanban.kvclient.KVTaskClient;
import com.yandex.kanban.model.EpicTask;
import com.yandex.kanban.model.Subtask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.util.UtilConstant;

import java.util.UUID;

public class HttpTaskManager extends FileBackedTasksManager {
    private final String path;
    private final KVTaskClient client;

    public HttpTaskManager() throws DatabaseException {
        throw new DatabaseException("Данный класс не может использоваться без передачи аргументом пути к Базе Данных");
    }

    public HttpTaskManager(String path) throws KVClientException {
        this.path = path;
        client = new KVTaskClient(path);
        this.readFile();//ПРОБЛЕМА!!
    }

    @Override
    protected void save() throws KVClientException {
        client.put("tasks", UtilConstant.GSON.toJson(database.values()));
        client.put("history", UtilConstant.GSON.toJson(getHistory()));
    }

    @Override
    protected void readFile() {
        String dataTasksJson;
        String dataHistoryJson;

        try {
            dataTasksJson = client.load("tasks");
        } catch (KVClientException e) {
            dataTasksJson = null;
        }

        try {
            dataHistoryJson = client.load("history");
        } catch (KVClientException e) {
            dataHistoryJson = null;
        }

        if (dataTasksJson == null) {
            System.out.println("База пуста");
            return;
        }

        JsonArray dataTasks = JsonParser.parseString(dataTasksJson).getAsJsonArray();
        for (JsonElement data: dataTasks) {
            JsonObject taskObj = data.getAsJsonObject();
            Task task = null;
            switch (taskObj.get("type").getAsString()) {
                case "NORMAL":
                    task = UtilConstant.GSON.fromJson(data, Task.class);
                    break;
                case "EPIC_TASK":
                    task = UtilConstant.GSON.fromJson(data, EpicTask.class);
                    break;
                case "SUBTASK":
                    task = UtilConstant.GSON.fromJson(data, Subtask.class);
                    break;
                default:
                    System.out.println("Неизвестный тип задачи");
            }

            if (task != null) {
                database.put(task.getId(), task);
            }
        }

        if (dataHistoryJson != null) {
            JsonArray dataHistory = JsonParser.parseString(dataHistoryJson).getAsJsonArray();
            for (JsonElement elem: dataHistory) {
                JsonObject taskObj = elem.getAsJsonObject();
                try {
                    getTask(UUID.fromString(taskObj.get("id").toString()));
                } catch (DatabaseException | KVClientException e) {
                    System.out.println(e.getMessage());
                }
            }
        }


    }
}
