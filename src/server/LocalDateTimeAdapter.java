package server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    public void write(JsonWriter writer, LocalDateTime localDateTime) throws IOException {
        writer.value(localDateTime.format(timeFormatter));
    }

    @Override
    public LocalDateTime read(JsonReader reader) throws IOException {
        return LocalDateTime.parse(reader.nextString(), timeFormatter);
    }
}
