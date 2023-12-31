package com.yandex.kanban.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yandex.kanban.server.typeAdapters.DurationTypeAdapter;
import com.yandex.kanban.server.typeAdapters.LocalDateTimeTypeAdapter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UtilConstant {
    public static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
    public static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    public static Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .serializeNulls()
            .create();
}
