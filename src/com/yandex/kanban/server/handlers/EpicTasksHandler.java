package com.yandex.kanban.server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.kanban.managers.taskManager.TaskManager;

import java.io.IOException;

public class EpicTasksHandler extends HandlerTemplate {
    private final TaskManager manager;
    private final Gson gson;

    public EpicTasksHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String result;
        switch(exchange.getRequestMethod()) {
            case "GET":
                result = gson.toJson(manager.getAllEpicTask());
                generateResponse(200, result, exchange);
                return;
            case "DELETE":
                result = "Все задачи со статусом Эпик удалены";
                generateResponse(200, result, exchange);
                return;
            default:
                result = "Данный путь не может принимать запросы по методу " + exchange.getRequestMethod();
                generateResponse(405, result, exchange);
        }
    }
}
