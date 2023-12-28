package com.yandex.kanban.server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.kanban.managers.taskManager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends HandlerTemplate {
    private final TaskManager manager;
    private final Gson gson;

    public HistoryHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().getPath().split("/").length != 3) {
            generateResponse(404, "Данный ресурс недоступен", exchange);
        }
        String result;
        switch (exchange.getRequestMethod()) {
            case "GET":
               result = gson.toJson(manager.getHistory());
               generateResponse(200, result, exchange);
               return;
            default:
                result = "Данный метод не поддерживается";
                generateResponse(405, result, exchange);
        }
    }
}
