package com.yandex.kanban.server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.kanban.managers.taskManager.TaskManager;
import com.yandex.kanban.util.UtilConstant;

import java.io.IOException;

public class EpicTasksHandler extends HandlerTemplate {
    private final TaskManager manager;


    public EpicTasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String result;
        switch(exchange.getRequestMethod()) {
            case "GET":
                result = UtilConstant.GSON.toJson(manager.getAllEpicTask());
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
