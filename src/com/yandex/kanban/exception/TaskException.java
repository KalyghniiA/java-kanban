package com.yandex.kanban.exception;

public class TaskException extends Exception{
    String message;

    public TaskException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
