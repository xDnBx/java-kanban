package managers;

import java.io.File;

public class Managers {
    public static final File FILE_CSV = new File("data.csv");

    public static TaskManager getDefault() {
        return new FileBackedTaskManager(FILE_CSV);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
