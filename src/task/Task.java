package task;

import java.util.Objects;
import java.util.UUID;

public class Task {
    protected String name;
    protected String description;
    protected final UUID id;
    protected TaskType type;
    protected TaskStatus status;

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        id = UUID.randomUUID();
        type = TaskType.NORMAL;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public UUID getId() {
        return id;
    }

    public TaskType getType() {
        return type;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) && Objects.equals(description, task.description) && Objects.equals(id, task.id) && type == task.type && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, type, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", type=" + type +
                ", status=" + status +
                '}';
    }
}
