package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    protected final List<Task> historyTask = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (historyTask.size() >= 10) {
            historyTask.removeFirst();
        }
        historyTask.add(task);
    }

    @Override
    public List<Task> getHistory() {
        if (historyTask.isEmpty()) {
            return null;
        }
        return new ArrayList<>(historyTask);
    }
}
