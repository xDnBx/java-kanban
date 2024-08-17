import enums.TaskStatus;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryTaskManagerTest {

    TaskManager taskManager;

    @BeforeEach
    void BeforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void CanAddNewTaskAndGetTaskById() {
        Task task = new Task("Купить курицу", "В магазине", TaskStatus.NEW);
        taskManager.addNewTask(task);
        int taskId = task.getId();
        Task anotherTask = taskManager.getTaskById(taskId);

        assertNotNull(taskManager.getTasks(), "Список тасков пуст");
        assertEquals(task, anotherTask, "задачи не совпадают по id");
        assertEquals(task.getName(), anotherTask.getName(), "имя задачи не совпадает");
        assertEquals(task.getDescription(), anotherTask.getDescription(), "описание задачи не совпадает");
        assertEquals(task.getTaskStatus(), anotherTask.getTaskStatus(), "статус задачи не совпадает");
    }

    @Test
    void CanAddNewSubTaskAndGetSubTaskById() {
        Epic epic = new Epic("Купить курицу", "В магазине", TaskStatus.NEW);
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Купить куриные ножки", "В мясном", TaskStatus.IN_PROGRESS,
                epic.getId());
        taskManager.addNewSubtask(subtask);
        int subTaskId = subtask.getId();
        Task anotherSubTask = taskManager.getSubtaskById(subTaskId);

        assertNotNull(taskManager.getSubTasks(), "Список подзадач пуст");
        assertEquals(subtask, anotherSubTask, "подзадачи не совпадают по id");
        assertEquals(subtask.getName(), anotherSubTask.getName(), "имя подзадачи не совпадает");
        assertEquals(subtask.getDescription(), anotherSubTask.getDescription(), "описание подзадачи не " +
                "совпадает");
        assertEquals(subtask.getTaskStatus(), anotherSubTask.getTaskStatus(), "статус подзадачи не совпадает");
    }

    @Test
    void CanAddNewEpicAndGetEpicById() {
        Epic epic = new Epic("Купить курицу", "В магазине", TaskStatus.NEW);
        taskManager.addNewEpic(epic);
        int epicId = epic.getId();
        Task anotherEpic = taskManager.getEpicById(epicId);

        assertNotNull(taskManager.getEpics(), "Список эпиков пуст");
        assertEquals(epic, anotherEpic, "эпики не совпадают по id");
        assertEquals(epic.getName(), anotherEpic.getName(), "имя эпика не совпадает");
        assertEquals(epic.getDescription(), anotherEpic.getDescription(), "описание эпика не совпадает");
        assertEquals(epic.getTaskStatus(), anotherEpic.getTaskStatus(), "статус эпика не совпадает");
    }
}