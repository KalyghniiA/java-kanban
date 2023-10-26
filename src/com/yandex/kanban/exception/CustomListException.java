package com.yandex.kanban.exception;

public class CustomListException extends Exception {
    String message;

    public CustomListException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
