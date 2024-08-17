import enums.TaskStatus;
import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {

    TaskManager taskManager;

    @BeforeEach
    void BeforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void CanSavePreviousTaskInHistory() {
        Task task1 = new Task("Купить курицу", "В магазине", TaskStatus.NEW);
        taskManager.addNewTask(task1);
        taskManager.getTaskById(task1.getId());
        Task task2 = new Task(task1.getId(), "Купить мясо", "В мясном", TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task2);
        taskManager.getTaskById(task2.getId());
        Task oldTask = taskManager.getHistory().getFirst();

        assertEquals(task1, oldTask, "задачи не идентичны");
    }

    @Test
    void addHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Купить курицу", "В магазине", TaskStatus.NEW);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }
}