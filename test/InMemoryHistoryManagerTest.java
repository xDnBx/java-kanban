import enums.TaskStatus;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Test;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {

    TaskManager taskManager = Managers.getDefault();

    @Test
    void CanSavePreviousTaskInHistory() {
        Task task1 = new Task("Купить курицу", "В магазине", TaskStatus.NEW);
        taskManager.addNewTask(task1);
        taskManager.getTaskById(task1.getId());
        Task task2 = new Task(1, "Купить мясо", "В мясном", TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task2);
        taskManager.getTaskById(task2.getId());
        Task oldTask = taskManager.getHistory().getFirst();

        assertEquals(task1, oldTask, "задачи не идентичны");
    }
}