package com.yandex.kanban.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.yandex.kanban.managers.taskManager.TaskManager;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.util.UtilConstant;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends HandlerTemplate {
    private final TaskManager manager;
    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().getPath().split("/").length != 3) {
            generateResponse(404, "Данный ресурс недоступен", exchange);
        }
        String result;
        if (exchange.getRequestMethod().equals("GET")) {
            List<Task> history = manager.getHistory();
            result = history.isEmpty() ? "История пуста" : UtilConstant.GSON.toJson(history);
            generateResponse(200, result, exchange);
        } else {
            result = "Данный метод не поддерживается";
            generateResponse(405, result, exchange);
        }
    }
}
