package com.yandex.kanban.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.yandex.kanban.exception.KVClientException;
import com.yandex.kanban.managers.taskManager.TaskManager;
import com.yandex.kanban.util.UtilConstant;

import java.io.IOException;

public class NormalTaskHandler extends HandlerTemplate {
    TaskManager manager;
    public NormalTaskHandler(TaskManager manager) {
        this.manager = manager;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String result;

        switch (exchange.getRequestMethod()) {
            case "GET":
                result = UtilConstant.GSON.toJson(manager.getAllNormalTask());
                generateResponse(200, result, exchange);
                return;
            case "DELETE":
                try {
                    result = "Все задачи удалены";
                    manager.removeAllNormalTask();
                    generateResponse(200, result, exchange);
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
