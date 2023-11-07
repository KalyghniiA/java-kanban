package com.yandex.kanban.writer;

import com.yandex.kanban.model.Task;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;



public class Writer {
    public static void write(Path path, String header, Collection<Task> info, Collection<Task> history) {
        try (FileWriter fw = new FileWriter(path.toFile())) {
            fw.write(header);
            for (Task line: info) {
                fw.write("\n");
                fw.write(line.toLineForFile());
            }

            if (!history.isEmpty()) {
                fw.write("\n\n");
                fw.write(history.stream().map(task -> task .getId().toString()).collect(Collectors.joining(",")));
            }
        } catch (IOException e) {
            System.out.println("Ошибка при записи файла");
        }
    }
}
