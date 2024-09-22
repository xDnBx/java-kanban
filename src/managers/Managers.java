package managers;

import java.io.File;

public class Managers {
    public static final File fileCsv = new File("data.csv");

    public static TaskManager getDefault() {
        return new FileBackedTaskManager(fileCsv);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
