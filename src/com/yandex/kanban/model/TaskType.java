package com.yandex.kanban.model;

public enum TaskType {
    NORMAL("NORMAL"),
    EPIC_TASK("EPIC_TASK"),
    SUBTASK("SUBTASK");

    private final String type;

    TaskType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
