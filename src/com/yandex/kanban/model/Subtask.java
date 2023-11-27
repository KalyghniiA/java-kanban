package com.yandex.kanban.model;

import com.yandex.kanban.exception.TaskException;

import java.util.UUID;

public class Subtask extends Task {
    UUID epicTaskId;
    public Subtask(String name, String description, TaskStatus status, UUID epicTaskId) {
        super(name, description, status);
        this.epicTaskId = epicTaskId;
        this.type = TaskType.SUBTASK;
    }

    public Subtask(String name, String description, TaskStatus status, UUID epicTaskId, String startTime, String duration) throws TaskException {
        super(name, description, status, startTime, duration);
        this.epicTaskId = epicTaskId;
        this.type = TaskType.SUBTASK;
    }

    public Subtask(String name, String description, TaskStatus status, UUID id, UUID epicTaskId) {
        super(name, description, status);
        this.epicTaskId = epicTaskId;
        this.type = TaskType.SUBTASK;
        this.id = id;
    }

    public Subtask(String name, String description, TaskStatus status, UUID id, UUID epicTaskId, String startTime, String duration) throws TaskException{
        this(name, description, status, epicTaskId, startTime, duration);
        this.id = id;
    }

    @Override
    public String toLineForFile() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s", this.id, this.type, this.name, this.description, this.status,getStartTime(), getDuration(), this.epicTaskId);
    }

    public UUID getEpicTaskId() {
        return epicTaskId;
    }
}
