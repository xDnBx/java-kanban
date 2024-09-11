package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    protected final Map<Integer, Node<Task>> historyTask = new HashMap<>();
    protected Node<Task> head;
    protected Node<Task> tail;

    @Override
    public void add(Task task) {
        int taskId = task.getId();
        remove(taskId);
        historyTask.put(taskId, linkLast(task));
    }

    @Override
    public void remove(int id) {
        if (historyTask.containsKey(id)) {
            removeNode(historyTask.get(id));
            historyTask.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> current = head;
        for (int i = 0; i < historyTask.size(); i++) {
            tasks.add(current.data);
            current = current.next;
        }
        return tasks;
    }

    private Node<Task> linkLast(Task task) {
        Node<Task> updNode = new Node<>(task);
        if (head == null) {
            head = updNode;
        } else {
            tail.next = updNode;
            updNode.prev = tail;
        }
        tail = updNode;
        return updNode;
    }

    private void removeNode(Node<Task> node) {
        if (node == null) {
            return;
        }
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }
}
