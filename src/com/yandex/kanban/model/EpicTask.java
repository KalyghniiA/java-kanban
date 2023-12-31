package com.yandex.kanban.model;

import com.yandex.kanban.exception.TaskException;
import com.yandex.kanban.util.UtilConstant;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class EpicTask extends Task {
    List<UUID> subtasksId = new ArrayList<>();
    LocalDateTime endTime;
    public EpicTask(String name, String description) {
        super(name, description, TaskStatus.NEW);
        this.type = TaskType.EPIC_TASK;
    }

    public EpicTask(String name, String description, UUID id) {
        super(name, description, TaskStatus.NEW);
        this.type = TaskType.EPIC_TASK;
        this.id = id;
    }

    public List<UUID> getSubtasksId() {
        return new ArrayList<>(subtasksId);
    }

    public void addSubtaskId(UUID id) throws TaskException {
        if (id == null) {
            throw new TaskException("Передано пустое значение");
        }

        if (subtasksId.contains(id)) {
            throw new TaskException("Данная Задача уже есть");
        }

        subtasksId.add(id);
    }

    public void removeSubtaskId(UUID id) throws TaskException {
        if (id == null) {
            throw new TaskException("Передано пустое значение");
        }

        if (!subtasksId.contains(id)) {
            throw new TaskException("Данной подзадачи нет");
        }

        subtasksId.remove(id);
    }

    public void checkStatus(List<Subtask> subtasks) {
        try {
            if (subtasks == null) {
                throw new TaskException("Передано пустое значение");
            }
        } catch (TaskException e) {
            System.out.println(e.getMessage());
            return;
        }

        if (subtasks.isEmpty()) {
            this.status = TaskStatus.NEW;
            return;
        }

        checkTime(subtasks);

        int iterator = 0;
        for (Subtask task: subtasks) {
            if (task.getStatus().equals(TaskStatus.IN_PROGRESS)) {
                this.status = TaskStatus.IN_PROGRESS;
                return;
            } else if (task.getStatus().equals(TaskStatus.DONE)) {
                iterator++;
            }
        }

        if (iterator == subtasks.size()) {
            this.status = TaskStatus.DONE;
        } else if (iterator > 0 && iterator < subtasks.size()) {
            this.status = TaskStatus.IN_PROGRESS;
        } else {
            this.status = TaskStatus.NEW;
        }
    }

    private void checkTime(List<Subtask> subtasks) {
        int iterator = 0;
        for (Subtask task: subtasks) {
            if (task.getStartTime() == null || task.getDuration() == null) {
                iterator++;
                continue;
            }

            LocalDateTime startTimeSubtask = LocalDateTime.parse(task.getStartTime(), UtilConstant.DATE_TIME_FORMATTER);
            LocalDateTime endTimeSubtask = LocalDateTime.parse(task.getEndTime(), UtilConstant.DATE_TIME_FORMATTER);

            if (this.startTime == null && this.endTime == null) {
                this.startTime = startTimeSubtask;
                this.endTime = endTimeSubtask;
            }

            if (this.startTime != null && this.startTime.isAfter(startTimeSubtask)) {
                this.startTime = startTimeSubtask;
            }

            if (this.endTime != null && this.endTime.isBefore(endTimeSubtask)) {
                this.endTime = endTimeSubtask;
            }
        }

        if (this.startTime != null && this.endTime != null && iterator != subtasks.size()) {
            this.duration = Duration.between(this.startTime, this.endTime);
        } else if (this.startTime != null && this.endTime != null && iterator == subtasks.size()) {
            this.startTime = null;
            this.endTime = null;
            this.duration = null;
        }
    }
}
