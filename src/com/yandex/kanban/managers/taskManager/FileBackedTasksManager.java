package com.yandex.kanban.managers.taskManager;

import com.yandex.kanban.exception.DatabaseException;
import com.yandex.kanban.exception.KVClientException;
import com.yandex.kanban.exception.TaskException;
import com.yandex.kanban.model.*;
import com.yandex.kanban.reader.Reader;
import com.yandex.kanban.writer.Writer;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.yandex.kanban.util.PathConstant.*;


public class FileBackedTasksManager extends InMemoryTaskManager {
    public FileBackedTasksManager() {
        this.readFile();
    }

    @Override
    public void createTask(Task task) throws DatabaseException, TaskException, KVClientException {
        super.createTask(task);
        save();
    }

    @Override
    public void createTasks(List<Task> tasks) throws DatabaseException, TaskException, KVClientException {
        super.createTasks(tasks);
        save();
    }

    @Override
    public void replaceTask(Task task) throws DatabaseException, TaskException, KVClientException {
        super.replaceTask(task);
        save();
    }

    @Override
    public void removeTask(UUID id) throws DatabaseException, TaskException, KVClientException {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeAllTasks() throws  KVClientException {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllNormalTask() throws KVClientException {
        super.removeAllNormalTask();
        save();
    }

    @Override
    public void removeAllSubtasks() throws KVClientException {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpicTask() throws KVClientException {
        super.removeAllEpicTask();
        save();
    }

    @Override
    public Task getTask(UUID id) throws DatabaseException, KVClientException {
        Task task = super.getTask(id);
        save();
        return task;
    }

    private void readFile() {
        Map<String, List<String>> dataForFile = Reader.read(RESOURCE + FILE_NAME);
        List<Task> tasks = new ArrayList<>();
        List<String> tasksData = dataForFile.get("tasks");
        List<String> historyData = dataForFile.get("history");

        if (tasksData.isEmpty()) {
            System.out.println("Файл пуст");
            return;
        }

        for (String line: tasksData) {
            String[] data = line.split(",");
            try {
                tasks.add(lineToTask(data));
            } catch (IOException e) {
                System.out.println("Ошибка чтения строки");
            } catch (NullPointerException e) {
                System.out.println("Ошибка данных в строке");
            }
        }

        tasks.stream()
                .filter(task -> task.getType() == TaskType.NORMAL)
                .forEach(task -> database.put(task.getId(), task));
        tasks.stream()
                .filter(task -> task.getType() == TaskType.EPIC_TASK)
                .forEach(task -> database.put(task.getId(), task));
        tasks.stream()
                .filter(task -> task.getType() == TaskType.SUBTASK)
                .peek(task -> database.put(task.getId(), task))
                .forEach(task -> {
                   EpicTask epicTask = (EpicTask) database.get( ((Subtask) task).getEpicTaskId());
                    try {
                        epicTask.addSubtaskId(task.getId());
                        checkStatusToEpicTask(epicTask);
                    } catch (TaskException e) {
                        System.out.println(e.getMessage());
                    }
                });

        try {
            List<UUID> historyId = historyData.stream().map(UUID::fromString).collect(Collectors.toList());
            for (var id: historyId) {
                super.getTask(id);
            }
        } catch (DatabaseException | KVClientException e) {
            System.out.println(e.getMessage());
        }

    }

    private Task lineToTask(String[] line) throws IOException {
        switch (TaskType.valueOf(line[1])) {
            case NORMAL:
                try {
                    return line[5].equals("null")
                            ? new Task(line[2], line[3], TaskStatus.valueOf(line[4]), UUID.fromString(line[0]))
                            : new Task(line[2], line[3], TaskStatus.valueOf(line[4]), UUID.fromString(line[0]), line[5], line[6]);
                } catch (TaskException e) {
                    System.out.println(e.getMessage());
                }
            case EPIC_TASK:
                return new EpicTask(line[2], line[3], UUID.fromString(line[0]));
            case SUBTASK:
                try {
                    return line[5].equals("null")
                            ? new Subtask(line[2], line[3], TaskStatus.valueOf(line[4]), UUID.fromString(line[0]), UUID.fromString(line[7]))
                            : new Subtask(line[2], line[3], TaskStatus.valueOf(line[4]), UUID.fromString(line[0]), UUID.fromString(line[7]), line[5], line[6]);
                } catch (TaskException e) {
                    System.out.println(e.getMessage());
                }
            default:
                throw new IOException();
        }
    }

    protected void save() throws KVClientException {
        try {
            if (!Files.exists(PATH)) {
                Files.createFile(PATH);
            }

            Writer.write(PATH, HEADER, database.values(), history.getHistory());

        } catch (IOException e) {
            System.out.println("Error");
        }
    }
}
