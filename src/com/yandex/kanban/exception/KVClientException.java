package com.yandex.kanban.exception;

public class KVClientException extends Exception {
    private final String message;

    public KVClientException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
