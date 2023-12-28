package com.yandex.kanban.server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.kanban.exception.KVClientException;
import com.yandex.kanban.managers.taskManager.TaskManager;

import java.io.IOException;

public class SubtaskHandler extends HandlerTemplate {
    private final TaskManager manager;
    private final Gson gson;

    public SubtaskHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String result;

        switch (exchange.getRequestMethod()) {
            case "GET":
                result = gson.toJson(manager.getAllSubtask());
                generateResponse(200, result, exchange);
                return;
            case "DELETE":
                try {
                    result = "Все задачи удалены";
                    manager.removeAllSubtasks();
                    generateResponse(204, result, exchange);
                    return;
                } catch (KVClientException e) {
                    generateResponse(500, e.getMessage(), exchange);
                    break;
                }
            default:
                result = "Данный путь не может принимать запросы по методу " + exchange.getRequestMethod();
                generateResponse(405, result, exchange);
        }
    }

}
