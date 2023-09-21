package com.yandex.kanban.model;

import java.util.UUID;

public class Subtask extends Task {
    UUID epicTaskId;
    public Subtask(String name, String description, TaskStatus status, UUID epicTaskId) {
        super(name, description, status);
        this.epicTaskId = epicTaskId;
        this.type = TaskType.SUBTASK;
    }

    public UUID getEpicTaskId() {
        return epicTaskId;
    }
}
