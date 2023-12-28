package com.yandex.kanban.exception;

public class HandlerServerException extends Exception {
    String message = "Требуется переопределить метод";

    public HandlerServerException() {
    }

    public HandlerServerException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
