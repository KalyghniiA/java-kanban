package com.yandex.kanban.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import com.yandex.kanban.managers.taskManager.TaskManager;
import com.yandex.kanban.server.handlers.EpicTasksHandler;
import com.yandex.kanban.server.handlers.HistoryHandler;
import com.yandex.kanban.server.handlers.SubtaskHandler;
import com.yandex.kanban.server.handlers.TaskHandler;
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
    private final Gson gson = new GsonBuilder()
           .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
           .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
           .serializeNulls()
           .create();

    public HttpTaskServer(TaskManager manager) {
        this.manager = manager;
    }

    public void start() {
        try {
            server = HttpServer.create();
            server.bind(new InetSocketAddress(PORT), 0);
            server.createContext("/tasks/task/", new TaskHandler(manager, gson));
            server.createContext("/tasks/subtasks/", new SubtaskHandler(manager, gson));
            server.createContext("/tasks/epic/", new EpicTasksHandler(manager, gson));
            server.createContext("/tasks/history/", new HistoryHandler(manager, gson));

            server.start();
        } catch (IOException e) {
            System.out.println("при создании сервера произошла ошибка");
        }

    }

}
