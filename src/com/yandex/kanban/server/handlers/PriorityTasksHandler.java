package com.yandex.kanban.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.yandex.kanban.managers.taskManager.TaskManager;
import com.yandex.kanban.util.UtilConstant;

import java.io.IOException;

public class PriorityTasksHandler extends HandlerTemplate {
    private final TaskManager manager;

    public PriorityTasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().getPath().split("/").length != 2) {
            generateResponse(404, "Данный ресурс недоступен!!", exchange);
        }
        String result;

        switch (exchange.getRequestMethod()) {
            case "GET":
                result = UtilConstant.GSON.toJson(manager.getPriority());
                generateResponse(200, result, exchange);
                return;
            default:
                result = "Данный путь не может принимать запросы по методу " + exchange.getRequestMethod();
                generateResponse(405, result, exchange);
        }

    }
}
