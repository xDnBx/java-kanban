package managers;

import enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {

    TaskManager taskManager;
    HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void addHistory() {
        Task task = new Task("Купить курицу", "В магазине", TaskStatus.NEW);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void canDeleteRepeatsFromHistory() {
        Task task = new Task("Купить курицу", "В магазине", TaskStatus.NEW);
        taskManager.addNewTask(task);
        final int taskId = task.getId();
        taskManager.getTaskById(taskId);
        taskManager.getTaskById(taskId);
        taskManager.getTaskById(taskId);

        final List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }
}