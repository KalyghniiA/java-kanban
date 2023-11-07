package com.yandex.kanban.reader;

import java.io.*;
import java.util.*;

public class Reader {
    public static Map<String, List<String>> read (String path) {

        Map<String, List<String>> result = new HashMap<>();
        result.put("tasks", new ArrayList<>());
        result.put("history", new LinkedList<>());

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine();
            while (br.ready()) {
                String line = br.readLine();
                if (!line.isBlank()) {
                    result.get("tasks").add(line);
                } else {
                    String[] history = br.readLine().split(",");
                    for (String id: history) {
                        result.get("history").add(id);
                    }
                }

            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла");
        }
        return result;
    }
}
