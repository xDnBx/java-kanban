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
        if (task != null) {
            int taskId = task.getId();
            remove(taskId);
            historyTask.put(taskId, linkLast(task));
        }
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
        while (current != null) {
            tasks.add(current.data);
            current = current.next;
        }
        return tasks;
    }

    private Node<Task> linkLast(Task task) {
        Node<Task> newNode = new Node<>(task, null, tail);
        if (head == null) {
            head = newNode;
        } else {
            tail.next = newNode;
        }
        tail = newNode;
        return newNode;
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
