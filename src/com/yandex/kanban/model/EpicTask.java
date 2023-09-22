package com.yandex.kanban.model;

import com.yandex.kanban.exception.TaskException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EpicTask extends Task {
    List<UUID> subtasksId;
    public EpicTask(String name, String description, UUID id) {
        super(name, description, TaskStatus.NEW, id);
        this.subtasksId = new ArrayList<>();
        this.type = TaskType.EPIC_TASK;
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
            return;
        } else if (iterator > 0 && iterator < subtasks.size()) {
            this.status = TaskStatus.IN_PROGRESS;
        } else {
            this.status = TaskStatus.NEW;
        }
    }

}
