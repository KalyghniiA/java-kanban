package test;

import com.yandex.kanban.model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class TestHttpClient {

    private final static String PATH = "http://localhost:8080";
    private final static String TASKS = "tasks";
    private final static String SUBTASK = "subtask";
    private final static String EPIC = "epic";
    private final static String HISTORY = "history";
    private final static String TASK = "task";

    private final HttpClient client;

    public TestHttpClient() {
        client = HttpClient.newHttpClient();
    }

    public String get(String path) {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(path))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String post(String path, String json) {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(path))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String delete(String path) {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(path))
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
