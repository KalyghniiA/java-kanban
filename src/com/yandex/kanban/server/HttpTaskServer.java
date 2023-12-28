package com.yandex.kanban.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import com.yandex.kanban.managers.taskManager.TaskManager;
import com.yandex.kanban.server.handlers.*;
import com.yandex.kanban.server.type_adapters.DurationTypeAdapter;
import com.yandex.kanban.server.type_adapters.LocalDateTimeTypeAdapter;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer server;
    private final TaskManager manager;

    public HttpTaskServer(TaskManager manager) {
        this.manager = manager;
    }

    public void start() {
        try {
            server = HttpServer.create();
            server.bind(new InetSocketAddress(PORT), 0);
            server.createContext("/tasks/", new PriorityTasksHandler(manager));
            server.createContext("/tasks/task/", new TaskHandler(manager));
            server.createContext("/tasks/subtasks/", new SubtaskHandler(manager));
            server.createContext("/tasks/epic/", new EpicTasksHandler(manager));
            server.createContext("/tasks/history", new HistoryHandler(manager));


            server.start();
        } catch (IOException e) {
            System.out.println("при создании сервера произошла ошибка");
        }

    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер остановлен");
    }

}
