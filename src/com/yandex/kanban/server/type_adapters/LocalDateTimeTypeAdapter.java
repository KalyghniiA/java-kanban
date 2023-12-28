package com.yandex.kanban.server.type_adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.yandex.kanban.util.UtilConstant;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        if (localDateTime == null) {
            jsonWriter.value("null");
            return;
        }
        jsonWriter.value(localDateTime.format(UtilConstant.DATE_TIME_FORMATTER));
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        if (value.equals("null")) {
            return null;
        }
        return LocalDateTime.parse(value, UtilConstant.DATE_TIME_FORMATTER);
    }
}
