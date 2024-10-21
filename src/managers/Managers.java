package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;
import server.DurationAdapter;
import server.LocalDateTimeAdapter;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Managers {
    public static final File FILE_CSV = new File("data.csv");

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getDefaultFile() {
        return new FileBackedTaskManager(FILE_CSV);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .setPrettyPrinting()
                .create();
    }
}