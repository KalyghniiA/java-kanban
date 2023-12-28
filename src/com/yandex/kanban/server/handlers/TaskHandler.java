package com.yandex.kanban.server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.kanban.exception.DatabaseException;
import com.yandex.kanban.exception.KVClientException;
import com.yandex.kanban.exception.TaskException;
import com.yandex.kanban.managers.taskManager.TaskManager;
import com.yandex.kanban.model.EpicTask;
import com.yandex.kanban.model.Subtask;
import com.yandex.kanban.model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;

public class TaskHandler extends HandlerTemplate{
    private final TaskManager manager;
    private final Gson gson;

    public TaskHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String params = exchange.getRequestURI().getQuery();
        String result = "Ошибка сервера";
        Options options = null;
        int code = 500;

        if (params != null) {
            options = new Options();
            options.setOptions(params.split("&"));
        }



        switch (exchange.getRequestMethod()) {
            case "GET":
                if (options != null && options.getId() != null) {
                    try {
                        Task task = manager.getTask(options.getId());
                        result = gson.toJson(task);
                        code = 200;
                        break;
                    } catch (DatabaseException | KVClientException e) {
                        result = e.getMessage();
                        code = 406;
                        break;
                    }
                } else {
                    List<Task> tasks = manager.getAllTasks();
                    if (tasks.isEmpty()) {
                        result = "На данный момент база пуста";
                        code = 406;
                    } else {
                        result = gson.toJson(tasks);
                        code = 200;
                    }
                    break;
                }
            case "POST":
                String bodyJson;
                try (InputStream is = exchange.getRequestBody()) {
                    bodyJson = new String(is.readAllBytes(), Charset.defaultCharset());
                    Task newTask;
                    if (bodyJson.contains("NORMAL")) {
                        newTask = gson.fromJson(bodyJson, Task.class);
                    } else if (bodyJson.contains("EPIC")) {
                        newTask = gson.fromJson(bodyJson, EpicTask.class);
                    } else if (bodyJson.contains("SUBTASK")) {
                        newTask = gson.fromJson(bodyJson, Subtask.class);
                    } else {
                        result = "Неизвестный тип задачи";
                        code = 400;
                        break;
                    }

                    try {
                        manager.createTask(newTask);
                        result = String.format("Задача создана и ей присвоен id %s", newTask.getId());
                        code = 200;
                        break;
                    } catch (DatabaseException | TaskException | KVClientException e) {
                        result = e.getMessage();
                        code = 406;
                        break;
                    }
                }

            case "DELETE":
                if (options == null) {
                    try {
                        manager.removeAllTasks();
                        result = "Все задачи удалены";
                        code = 200;
                    } catch (DatabaseException | TaskException | KVClientException e) {
                        result = e.getMessage();
                        code = 406;
                    }
                } else {
                    if (options.getId() != null) {
                        try {
                            manager.removeTask(options.getId());
                            result = String.format("Задача %s удалена", options.getId());
                            code = 200;
                        } catch (DatabaseException | TaskException | KVClientException e) {
                            result = e.getMessage();
                            code = 406;
                        }
                    }
                }
                break;
            default:
                result = "Данный путь не может принимать запросы по методу " + exchange.getRequestMethod();
                code = 405;
        }

        generateResponse(code, result, exchange);
    }

    //Задел на возможное расширение по параметрам
    private static class Options {
        private UUID id;

        public UUID getId() {
            return id;
        }

        public void setOptions(String[] params) {
            for (String option: params) {
                String key = option.substring(0, option.indexOf("="));
                String value = option.substring(option.indexOf("=") + 1);

                switch (key) {
                    case "id":
                        this.id = UUID.fromString(value);
                        break;
                    default:
                        System.out.println("Незарегистрированный параметр:" + key);
                }
            }
        }
    }
}
