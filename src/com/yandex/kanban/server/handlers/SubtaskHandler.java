package com.yandex.kanban.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.yandex.kanban.exception.DatabaseException;
import com.yandex.kanban.exception.KVClientException;
import com.yandex.kanban.exception.TaskException;
import com.yandex.kanban.managers.taskManager.TaskManager;
import com.yandex.kanban.util.OptionsPath;
import com.yandex.kanban.util.UtilConstant;

import java.io.IOException;

public class SubtaskHandler extends HandlerTemplate {
    private final TaskManager manager;

    public SubtaskHandler(TaskManager manager) {
        this.manager = manager;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParams = exchange.getRequestURI().getPath().split("/");
        String result;

        switch (exchange.getRequestMethod()) {
            case "GET":
                if (pathParams.length == 3) {
                    result = UtilConstant.GSON.toJson(manager.getAllSubtask());
                    generateResponse(200, result, exchange);
                } else {

                    if (!pathParams[3].equals("epic")) {
                        result = "Данный путь недоступен";
                        generateResponse(405, result, exchange);
                        return;
                    }

                    String query = exchange.getRequestURI().getQuery();
                    if (query == null || query.isBlank()) {
                        result = "Требуется указать id задачи";
                        generateResponse(405, result, exchange);
                        return;
                    }

                    OptionsPath options = new OptionsPath();
                    options.setOptions(query.split("&"));
                    if (options.getId() == null) {
                        result = "Требуется указать id задачи";
                        generateResponse(405, result, exchange);
                    } else {
                        try {
                            result = UtilConstant.GSON.toJson(manager.getSubtasksToEpicTask(options.getId()));
                            generateResponse(200, result, exchange);
                        } catch (TaskException e) {
                            result = e.getMessage();
                            generateResponse(400, result, exchange);
                        } catch (DatabaseException e) {
                            result = e.getMessage();
                            generateResponse(404, result, exchange);
                        } catch (KVClientException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
                return;
            case "DELETE":
                try {
                    result = "Все задачи удалены";
                    manager.removeAllSubtasks();
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
