package com.yandex.kanban.util;

import java.util.UUID;
//Задел на возможное расширение по параметрам
public class OptionsPath {
    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setOptions(String[] params) {
        for (String option: params) {
            String key = option.substring(0, option.indexOf("="));
            String value = option.substring(option.indexOf("=") + 1);

            switch (key) {
                case "id":
                    this.id = UUID.fromString(value);
                    break;
                default:
                    System.out.println("Незарегистрированный параметр:" + key);
            }
        }
    }
}
