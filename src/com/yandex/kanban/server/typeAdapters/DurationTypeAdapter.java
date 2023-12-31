package com.yandex.kanban.server.typeAdapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationTypeAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        String result;
        if (duration == null) {
            result = "null";
        } else {
            if (duration.toHours() == 0) {
                result = String.format("%s", duration.toMinutes());
            } else {
                result = String.format("%s:%s", duration.toHours(), duration.toMinutes() - (duration.toHours() * 60));
            }
        }

        jsonWriter.value(result);
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        if (value.equals("null")) {
            return null;
        } else if (value.contains(":")) {
            return Duration
                    .ofHours(Integer.parseInt(value.substring(0, value.indexOf(":"))))
                    .plusMinutes(Integer.parseInt(value.substring(value.indexOf(":") + 1)));
        } else {
            return Duration.ofMinutes(Integer.parseInt(value));
        }

    }
}
