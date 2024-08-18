package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    protected final List<Task> historyTask = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            historyTask.add(task);
        }
        if (historyTask.size() > 10) {
            historyTask.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyTask);
    }
}
