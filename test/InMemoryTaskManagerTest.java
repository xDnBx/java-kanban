import enums.TaskStatus;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        final int taskId = task.getId();
        final Task savedTask = taskManager.getTaskById(taskId);
        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Список тасков пуст");
        assertEquals(task, savedTask, "задачи не совпадают по id");
        assertEquals(task.getName(), savedTask.getName(), "имя задачи не совпадает");
        assertEquals(task.getDescription(), savedTask.getDescription(), "описание задачи не совпадает");
        assertEquals(task.getTaskStatus(), savedTask.getTaskStatus(), "статус задачи не совпадает");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void CanAddNewSubTaskAndGetSubTaskById() {
        Epic epic = new Epic("Купить курицу", "В магазине", TaskStatus.NEW);
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Купить куриные ножки", "В мясном", TaskStatus.IN_PROGRESS,
                epic.getId());
        taskManager.addNewSubtask(subtask);
        final int subTaskId = subtask.getId();
        final Subtask savedSubTask = taskManager.getSubtaskById(subTaskId);
        final List<Subtask> subTasks = taskManager.getSubTasks();

        assertNotNull(subTasks, "Список подзадач пуст");
        assertEquals(subtask, savedSubTask, "подзадачи не совпадают по id");
        assertEquals(subtask.getName(), savedSubTask.getName(), "имя подзадачи не совпадает");
        assertEquals(subtask.getDescription(), savedSubTask.getDescription(), "описание подзадачи не " +
                "совпадает");
        assertEquals(subtask.getTaskStatus(), savedSubTask.getTaskStatus(), "статус подзадачи не совпадает");
        assertEquals(1, subTasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subTasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void CanAddNewEpicAndGetEpicById() {
        Epic epic = new Epic("Купить курицу", "В магазине", TaskStatus.NEW);
        taskManager.addNewEpic(epic);
        final int epicId = epic.getId();
        final Epic savedEpic = taskManager.getEpicById(epicId);
        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(taskManager.getEpics(), "Список эпиков пуст");
        assertEquals(epic, savedEpic, "эпики не совпадают по id");
        assertEquals(epic.getName(), savedEpic.getName(), "имя эпика не совпадает");
        assertEquals(epic.getDescription(), savedEpic.getDescription(), "описание эпика не совпадает");
        assertEquals(epic.getTaskStatus(), savedEpic.getTaskStatus(), "статус эпика не совпадает");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void clearAllTasks() {
        Task task1 = new Task(1, "Купить курицу", "В магазине", TaskStatus.NEW);
        taskManager.addNewTask(task1);
        Task task2 = new Task(1, "Купить мясо", "В мясном магазине", TaskStatus.IN_PROGRESS);
        taskManager.addNewTask(task2);
        taskManager.clearTasks();

        assertTrue(taskManager.getTasks().isEmpty(), "Список тасков не пуст");
    }

    @Test
    void clearAllSubTasks() {
        Epic epic = new Epic("Купить курицу", "В магазине", TaskStatus.NEW);
        taskManager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("Купить куриные ножки", "В мясном", TaskStatus.IN_PROGRESS,
                epic.getId());
        taskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Купить куриные крылья", "В мясном", TaskStatus.IN_PROGRESS,
                epic.getId());
        taskManager.addNewSubtask(subtask2);
        taskManager.clearSubtasks();

        assertTrue(taskManager.getSubTasks().isEmpty(), "Список подзадач не пуст");
    }

    @Test
    void clearAllEpics() {
        Epic epic1 = new Epic("Купить курицу", "В магазине", TaskStatus.NEW);
        taskManager.addNewEpic(epic1);
        Epic epic2 = new Epic("Купить древесный уголь", "В гипермаркете", TaskStatus.IN_PROGRESS);
        taskManager.addNewEpic(epic2);
        taskManager.clearEpicTasks();

        assertTrue(taskManager.getEpics().isEmpty(), "Список эпиков не пуст");
    }

    @Test
    void ShouldUpdateSubTask() {
        Epic epic = new Epic("Купить курицу", "В магазине", TaskStatus.NEW);
        taskManager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("Купить куриные ножки", "В мясном", TaskStatus.IN_PROGRESS,
                epic.getId());
        taskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask(subtask1.getId(),"Купить куриные крылья", "В мясном",
                TaskStatus.IN_PROGRESS, epic.getId());
        taskManager.updateSubtask(subtask2);

        assertEquals(subtask1, subtask2, "Подзадачи не равны");
    }

    @Test
    void ShouldUpdateEpic() {
        Epic epic1 = new Epic("Купить курицу", "В магазине", TaskStatus.NEW);
        taskManager.addNewEpic(epic1);
        Epic epic2 = new Epic(epic1.getId(), "Купить древесный уголь", "В гипермаркете",
                TaskStatus.IN_PROGRESS);
        taskManager.updateEpicTask(epic2);

        assertEquals(epic1, epic2, "Эпики не равны");
    }

    @Test
    void ShouldDeleteTask() {
        Task task1 = new Task("Купить курицу", "В магазине", TaskStatus.NEW);
        taskManager.addNewTask(task1);
        Task task2 = new Task("Купить мясо", "В мясном магазине", TaskStatus.IN_PROGRESS);
        taskManager.addNewTask(task2);
        taskManager.deleteTaskById(task1.getId());
        final List<Task> tasks = taskManager.getTasks();

        assertEquals(1, tasks.size(), "Задача не удалена");
    }

    @Test
    void ShouldDeleteSubTask() {
        Epic epic = new Epic("Купить курицу", "В магазине", TaskStatus.NEW);
        taskManager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("Купить куриные ножки", "В мясном", TaskStatus.IN_PROGRESS,
                epic.getId());
        taskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Купить куриные крылья", "В мясном", TaskStatus.IN_PROGRESS,
                epic.getId());
        taskManager.addNewSubtask(subtask2);
        taskManager.deleteSubtaskById(subtask1.getId());
        final List<Subtask> subtasks = taskManager.getSubTasks();

        assertEquals(1, subtasks.size(), "Подзадача не удалена");
    }

    @Test
    void ShouldDeleteEpic() {
        Epic epic1 = new Epic("Купить курицу", "В магазине", TaskStatus.NEW);
        taskManager.addNewEpic(epic1);
        Epic epic2 = new Epic( "Купить древесный уголь", "В гипермаркете", TaskStatus.IN_PROGRESS);
        taskManager.addNewEpic(epic2);
        taskManager.deleteEpicTaskById(epic1.getId());
        final List<Epic> epics = taskManager.getEpics();

        assertEquals(1, epics.size(), "Эпик не удален");
    }
}