package managers;

import tasks.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(int id);

    List<Task> getHistory();

    void removeNode(Node<Task> node);

    Node<Task> linkLast(Task task);

    List<Task> getTasks();
}
