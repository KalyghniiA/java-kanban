package com.yandex.kanban.kvclient;

import com.yandex.kanban.exception.KVClientException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String path;
    private final HttpClient client;
    private String apiToken;

    public KVTaskClient(String path) throws KVClientException {
        this.path = path;
        this.client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(path + KVClientPaths.REGISTER.getResult())).GET().build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                this.apiToken = response.body();
            } else {
                throw new KVClientException("Произошла ошибка при получение ключа авторизации");
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка запуска клиента");
        }
    }

    public void put(String key, String json) throws KVClientException {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(String.format("%s%s/%s?API_TOKEN=%s", path, KVClientPaths.SAVE.getResult(), key, apiToken)))
                .headers("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            switch (response.statusCode()) {
                case 200:
                    break;
                case 400:
                    throw new KVClientException("Произошла ошибка при сохранении задачи, проверьте верность айди или данных задачи");
                case 403:
                    throw new KVClientException("Произошла ошибка авторизации. Проверьте верность ключа авторизации");
                case 405:
                    throw new KVClientException("Произошла ошибка запроса. Требуется метод POST");
                default:
                    throw new KVClientException("Произошла ошибка сервера. Код ошибки: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }

    public String load(String key) throws KVClientException {
        HttpRequest request = HttpRequest
                .newBuilder()
                .GET()
                .uri(URI.create(String.format("%s%s/%s?API_TOKEN=%s", path, KVClientPaths.LOAD.getResult(), key, apiToken)))
                .build();
        String result = null;

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            switch (response.statusCode()) {
                case 200:
                    result = response.body();
                    break;
                case 400:
                    throw new KVClientException("Произошла ошибка при получении задачи, проверьте верность ключа");
                case 403:
                    throw new KVClientException("Произошла ошибка авторизации. Проверьте верность ключа авторизации");
                case 404:
                    throw new KVClientException("Данные по переданному ключу отсутствуют в базе");
                case 405:
                    throw new KVClientException("Произошла ошибка запроса. Требуется метод GET");
                default:
                    throw new KVClientException("Произошла ошибка сервера. Код ошибки: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка отправки запроса");
        }

        return result;
    }
}
