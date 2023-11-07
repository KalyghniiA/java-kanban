package com.yandex.kanban.model;

import java.util.UUID;

public class Subtask extends Task {
    UUID epicTaskId;
    public Subtask(String name, String description, TaskStatus status, UUID epicTaskId) {
        super(name, description, status);
        this.epicTaskId = epicTaskId;
        this.type = TaskType.SUBTASK;
        this.id = UUID.randomUUID();
    }

    public Subtask(String name, String description, TaskStatus status, UUID id, UUID epicTaskId) {
        super(name, description, status, id);
        this.epicTaskId = epicTaskId;
        this.type = TaskType.SUBTASK;
    }

    @Override
    public String toLineForFile() {
        return String.format("%s,%s,%s,%s,%s,%s", this.id, this.type, this.name, this.description, this.status, this.epicTaskId);
    }

    public UUID getEpicTaskId() {
        return epicTaskId;
    }
}
