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

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void canSavePreviousTaskInHistory() {
        Task task1 = new Task("Купить курицу", "В магазине", TaskStatus.NEW);
        taskManager.addNewTask(task1);
        taskManager.getTaskById(task1.getId());
        Task task2 = new Task(task1.getId(), "Купить мясо", "В мясном", TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task2);
        taskManager.getTaskById(task2.getId());
        Task oldTask = taskManager.getHistory().getFirst();

        assertEquals(task1, oldTask, "id задач не идентичны");
        assertEquals(task1.getName(), oldTask.getName(), "Имена задач не идентичны");
        assertEquals(task1.getDescription(), oldTask.getDescription(), "Описания задач не идентичны");
        assertEquals(task1.getTaskStatus(), oldTask.getTaskStatus(), "Статусы задач не идентичны");
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

    @Test
    void addHistoryMoreThen10() {
        Task task = new Task("Купить курицу", "В магазине", TaskStatus.NEW);
        for (int i = 0; i < 11; i++) {
            taskManager.addNewTask(task);
        }
        List<Task> tasks = taskManager.getTasks();
        for (Task task1 : tasks) {
            taskManager.getTaskById(task1.getId());
        }
        List<Task> history = taskManager.getHistory();

        assertEquals(10, history.size(), "История имеет больше 10 элементов");
    }
}