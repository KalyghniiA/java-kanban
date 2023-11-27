package com.yandex.kanban.model;

import com.yandex.kanban.exception.TaskException;
import com.yandex.kanban.util.DateRegEx;
import com.yandex.kanban.util.UtilConstant;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.UUID;

public class Task {
    protected String name;
    protected String description;
    protected UUID id;
    protected TaskType type;
    protected TaskStatus status;
    LocalDateTime startTime = null;
    protected Duration duration = null;

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        type = TaskType.NORMAL;
        this.status = status;
    }

    public Task(String name, String description, TaskStatus status, UUID id) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = TaskType.NORMAL;
        this.id = id;
    }

    public Task(String name, String description, TaskStatus status, String startTime, String duration) throws TaskException {
        this(name, description, status);
        if (!DateRegEx.DATE_TIME_REGEX.matcher(startTime).matches()) {
            throw new TaskException("Дата начала выполнения задачи передана в неверном формате. Верный формат: ЧЧ:мм дд:ММ:гггг");
        }
        if (!DateRegEx.TIME_MINUTES_REGEX.matcher(duration).matches() && !DateRegEx.TIME_HOURS_AND_MINUTES_REGEX.matcher(duration).matches()) {
            throw new TaskException("Время выполнения задачи передано в неверном формате. Верный формат: ЧЧ:мм(на задачу дается от 1 часа и более) или мм(на задачу дается менее 1 часа)");
        }


        try {
            this.startTime = LocalDateTime.parse(startTime, UtilConstant.DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("Дата начала выполнения задачи передана в неверном формате. Верный формат: ЧЧ:мм дд:ММ:гггг");
            this.startTime = null;
            this.duration = null;
            System.out.println("Задача создана без указания времени!");
            return;
        }
        try {
            if (duration.contains(":")) {
                int hours = Integer.parseInt(duration.substring(0, duration.indexOf(":")));
                int minutes = Integer.parseInt(duration.substring(duration.indexOf(":") + 1));
                this.duration = Duration.between(this.startTime, this.startTime.plusHours(hours).plusMinutes(minutes));
            } else {
                int minutes = Integer.parseInt(duration);
                this.duration = Duration.between(this.startTime, this.startTime.plusMinutes(minutes));
            }
        } catch (DateTimeParseException e) {
            System.out.println("Время выполнения задачи передано в неверном формате. Верный формат: ЧЧ:мм(на задачу дается от 1 часа и более) или мм(на задачу дается менее 1 часа)");
            System.out.println("Задача создана без указания времени!");
            this.startTime = null;
            this.duration = null;
        } catch (NumberFormatException e) {
            System.out.printf("Не удалось корректно обработать длительность выполнение задачи. Возможно передано некорректное значение. Переданное значение: %s%n", duration);
            System.out.println("Задача создана без указания времени!");
            this.startTime = null;
            this.duration = null;
        }
    }

    public Task(String name, String description, TaskStatus status, UUID id, String startTime, String duration) throws TaskException {
        this(name, description, status, startTime, duration);
        this.id = id;
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

    public void setId(UUID id) {
        this.id = id;
    }

    public String getStartTime() {
        if (this.startTime == null) {
            return null;
        } else {
            return this.startTime.format(UtilConstant.DATE_TIME_FORMATTER);
        }
    }

    public void setStartTime(String startTime) throws TaskException {
        if (!DateRegEx.DATE_TIME_REGEX.matcher(startTime).matches()) {
            throw new TaskException("Дата начала выполнения задачи передана в неверном формате. Верный формат: ЧЧ:мм дд:ММ:гггг");
        }

        try {
            this.startTime = LocalDateTime.parse(startTime, UtilConstant.DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("Дата начала выполнения задачи передана в неверном формате. Верный формат: ЧЧ:мм дд:ММ:гггг");
        }
    }

    public String getDuration() {
        if (this.duration == null) {
            return null;
        } else {
            int allMinutes = (int) this.duration.toMinutes();
            int minutes = allMinutes % 60;
            int hours = (allMinutes - minutes) / 60;
            return String.format("%s:%s",hours, minutes);
        }
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getEndTime() {
        if (this.startTime == null || this.duration == null) {
            System.out.println("У данной задачи нет данных по времени");
            return null;
        }

        LocalDateTime endTime = this.startTime.plusMinutes(duration.toMinutes());
        return endTime.format(UtilConstant.DATE_TIME_FORMATTER);
    }

    public String toLineForFile() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,null", this.id, this.type, this.name, this.description, this.status, getStartTime(), getDuration());
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
