package managers;

import enums.TaskStatus;
import exceptions.TaskTimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTaskManagerTest {

    TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void canAddNewTaskAndGetTaskById() {
        Task task = new Task("Купить курицу", "В магазине", TaskStatus.NEW, Duration.ofMinutes(100),
                LocalDateTime.of(2024, Month.JANUARY, 1, 8, 12));
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
    void canAddNewSubTaskAndGetSubTaskById() {
        Epic epic = new Epic("Купить курицу", "В магазине");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Купить куриные ножки", "В мясном", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(55), LocalDateTime.of(2024, Month.JANUARY, 3, 10, 45), epic.getId());
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
    void canAddNewEpicAndGetEpicById() {
        Epic epic = new Epic("Купить курицу", "В магазине");
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
        Task task1 = new Task(1, "Купить курицу", "В магазине", TaskStatus.NEW, Duration.ofMinutes(100),
                LocalDateTime.of(2024, Month.JANUARY, 1, 8, 12));
        taskManager.addNewTask(task1);
        Task task2 = new Task(1, "Купить мясо", "В мясном магазине", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(50), LocalDateTime.of(2024, Month.JANUARY, 4, 9, 44));
        taskManager.addNewTask(task2);
        taskManager.clearTasks();

        assertTrue(taskManager.getTasks().isEmpty(), "Список тасков не пуст");
    }

    @Test
    void clearAllSubTasks() {
        Epic epic = new Epic("Купить курицу", "В магазине");
        taskManager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("Купить куриные ножки", "В мясном", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(55), LocalDateTime.of(2024, Month.JANUARY, 3, 10, 45), epic.getId());
        taskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Купить куриные крылья", "В мясном", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(60), LocalDateTime.of(2024, Month.JANUARY, 2, 11, 46), epic.getId());
        taskManager.addNewSubtask(subtask2);
        taskManager.clearSubtasks();

        assertTrue(taskManager.getSubTasks().isEmpty(), "Список подзадач не пуст");
    }

    @Test
    void clearAllEpics() {
        Epic epic1 = new Epic("Купить курицу", "В магазине");
        taskManager.addNewEpic(epic1);
        Epic epic2 = new Epic("Купить древесный уголь", "В гипермаркете");
        taskManager.addNewEpic(epic2);
        taskManager.clearEpicTasks();

        assertTrue(taskManager.getEpics().isEmpty(), "Список эпиков не пуст");
    }

    @Test
    void shouldUpdateSubTask() {
        Epic epic = new Epic("Купить курицу", "В магазине");
        taskManager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("Купить куриные ножки", "В мясном", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(55), LocalDateTime.of(2024, Month.JANUARY, 3, 10, 45), epic.getId());
        taskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask(subtask1.getId(), "Купить куриные крылья", "В мясном",
                TaskStatus.IN_PROGRESS, Duration.ofMinutes(55), LocalDateTime.of(2024, Month.JANUARY, 3, 10, 45),
                epic.getId());
        taskManager.updateSubtask(subtask2);

        assertEquals(subtask1, subtask2, "Подзадачи не равны");
    }

    @Test
    void shouldUpdateEpic() {
        Epic epic1 = new Epic("Купить курицу", "В магазине");
        taskManager.addNewEpic(epic1);
        Epic epic2 = new Epic(epic1.getId(), "Купить древесный уголь", "В гипермаркете");
        taskManager.updateEpicTask(epic2);

        assertEquals(epic1, epic2, "Эпики не равны");
    }

    @Test
    void shouldDeleteTask() {
        Task task1 = new Task(1, "Купить курицу", "В магазине", TaskStatus.NEW, Duration.ofMinutes(100),
                LocalDateTime.of(2024, Month.JANUARY, 1, 8, 12));
        taskManager.addNewTask(task1);
        Task task2 = new Task(1, "Купить мясо", "В мясном магазине", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(50), LocalDateTime.of(2024, Month.JANUARY, 4, 9, 44));
        taskManager.addNewTask(task2);
        taskManager.deleteTaskById(task1.getId());
        final List<Task> tasks = taskManager.getTasks();

        assertEquals(1, tasks.size(), "Задача не удалена");
    }

    @Test
    void shouldDeleteSubTask() {
        Epic epic = new Epic("Купить курицу", "В магазине");
        taskManager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("Купить куриные ножки", "В мясном", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(55), LocalDateTime.of(2024, Month.JANUARY, 3, 10, 45), epic.getId());
        taskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask(subtask1.getId(), "Купить куриные крылья", "В мясном",
                TaskStatus.IN_PROGRESS, Duration.ofMinutes(55), LocalDateTime.of(2024, Month.JANUARY, 4, 10, 45),
                epic.getId());
        taskManager.addNewSubtask(subtask2);
        taskManager.deleteSubtaskById(subtask1.getId());
        final List<Subtask> subtasks = taskManager.getSubTasks();

        assertEquals(1, subtasks.size(), "Подзадача не удалена");
    }

    @Test
    void shouldDeleteEpic() {
        Epic epic1 = new Epic("Купить курицу", "В магазине");
        taskManager.addNewEpic(epic1);
        Epic epic2 = new Epic("Купить древесный уголь", "В гипермаркете");
        taskManager.addNewEpic(epic2);
        taskManager.deleteEpicTaskById(epic1.getId());
        final List<Epic> epics = taskManager.getEpics();

        assertEquals(1, epics.size(), "Эпик не удален");
    }

    @Test
    void shouldDeleteSubTaskAndNotSaveOldId() {
        Epic epic = new Epic("Купить курицу", "В магазине");
        taskManager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("Купить куриные ножки", "В мясном", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(55), LocalDateTime.of(2024, Month.JANUARY, 3, 10, 45), epic.getId());
        taskManager.addNewSubtask(subtask1);
        final int subTaskId = subtask1.getId();
        taskManager.deleteSubtaskById(subTaskId);
        Subtask delSubTask = taskManager.getSubtaskById(subTaskId);

        assertNull(delSubTask, "Подзадача не удалена");
    }

    @Test
    void shouldDeleteSubTaskAndEpicNotContains() {
        Epic epic = new Epic("Купить курицу", "В магазине");
        taskManager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("Купить куриные ножки", "В мясном", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(55), LocalDateTime.of(2024, Month.JANUARY, 3, 10, 45), epic.getId());
        taskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask(subtask1.getId(), "Купить куриные крылья", "В мясном",
                TaskStatus.IN_PROGRESS, Duration.ofMinutes(55), LocalDateTime.of(2024, Month.JANUARY, 4, 10, 45),
                epic.getId());
        taskManager.addNewSubtask(subtask2);
        final int subTaskId = subtask1.getId();
        taskManager.deleteSubtaskById(subTaskId);
        final List<Subtask> subtasks = epic.getSubtaskEpic();

        assertFalse(subtasks.contains(subtask1), "Подзадача не удалена");
    }

    @Test
    void taskShouldMatchWhenUseSetters() {
        Task task1 = new Task(1, "Купить курицу", "В магазине", TaskStatus.NEW, Duration.ofMinutes(100),
                LocalDateTime.of(2024, Month.JANUARY, 1, 8, 12));
        taskManager.addNewTask(task1);
        task1.setName("Купить мясо");
        task1.setDescription("В мясном магазине");
        task1.setTaskStatus(TaskStatus.DONE);
        taskManager.updateTask(task1);
        Task task2 = taskManager.getTaskById(task1.getId());

        assertEquals("Купить мясо", task2.getName(), "Имена не совпадают");
        assertEquals("В мясном магазине", task2.getDescription(), "Описания не совпадают");
        assertEquals(TaskStatus.DONE, task2.getTaskStatus(), "Статусы не совпадают");
    }

    @Test
    void epicShouldHaveStatusNewWhenSubtasksHaveStatusNew() {
        Epic epic1 = new Epic("epic1", "description1");
        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("subtask1", "description1", TaskStatus.NEW,
                Duration.ofMinutes(55), LocalDateTime.of(2024, Month.JANUARY, 3, 10, 45), epic1.getId());
        Subtask subtask2 = new Subtask("subtask2", "description2", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2024, Month.JANUARY, 2, 11, 46), epic1.getId());
        Subtask subtask3 = new Subtask("subtask3", "description3", TaskStatus.NEW,
                Duration.ofMinutes(80), LocalDateTime.of(2024, Month.JANUARY, 5, 12, 47), epic1.getId());
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);

        assertEquals(TaskStatus.NEW, epic1.getTaskStatus());
    }

    @Test
    void epicShouldHaveStatusDoneWhenSubtasksHaveStatusDone() {
        Epic epic1 = new Epic("epic1", "description1");
        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("subtask1", "description1", TaskStatus.DONE,
                Duration.ofMinutes(55), LocalDateTime.of(2024, Month.JANUARY, 3, 10, 45), epic1.getId());
        Subtask subtask2 = new Subtask("subtask2", "description2", TaskStatus.DONE,
                Duration.ofMinutes(60), LocalDateTime.of(2024, Month.JANUARY, 2, 11, 46), epic1.getId());
        Subtask subtask3 = new Subtask("subtask3", "description3", TaskStatus.DONE,
                Duration.ofMinutes(80), LocalDateTime.of(2024, Month.JANUARY, 5, 12, 47), epic1.getId());
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);

        assertEquals(TaskStatus.DONE, epic1.getTaskStatus());
    }

    @Test
    void epicShouldHaveStatusInProgressWhenSubtasksHaveStatusNewAndDone() {
        Epic epic1 = new Epic("epic1", "description1");
        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("subtask1", "description1", TaskStatus.NEW,
                Duration.ofMinutes(55), LocalDateTime.of(2024, Month.JANUARY, 3, 10, 45), epic1.getId());
        Subtask subtask2 = new Subtask("subtask2", "description2", TaskStatus.DONE,
                Duration.ofMinutes(60), LocalDateTime.of(2024, Month.JANUARY, 2, 11, 46), epic1.getId());
        Subtask subtask3 = new Subtask("subtask3", "description3", TaskStatus.DONE,
                Duration.ofMinutes(80), LocalDateTime.of(2024, Month.JANUARY, 5, 12, 47), epic1.getId());
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);

        assertEquals(TaskStatus.IN_PROGRESS, epic1.getTaskStatus());
    }

    @Test
    void epicShouldHaveStatusInProgressWhenSubtasksHaveStatusInProgress() {
        Epic epic1 = new Epic("epic1", "description1");
        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("subtask1", "description1", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(55), LocalDateTime.of(2024, Month.JANUARY, 3, 10, 45), epic1.getId());
        Subtask subtask2 = new Subtask("subtask2", "description2", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(60), LocalDateTime.of(2024, Month.JANUARY, 2, 11, 46), epic1.getId());
        Subtask subtask3 = new Subtask("subtask3", "description3", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(80), LocalDateTime.of(2024, Month.JANUARY, 5, 12, 47), epic1.getId());
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);

        assertEquals(TaskStatus.IN_PROGRESS, epic1.getTaskStatus());
    }

    @Test
    void shouldThrowAndNotThrowExceptionWhenTaskTimeIsCrossing() {
        Task task1 = new Task("task1", "description1");
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.ofHours(1));
        taskManager.addNewTask(task1);
        Task task2 = new Task("task2", "description2");
        task2.setStartTime(LocalDateTime.now().plusMinutes(20));
        task2.setDuration(Duration.ofMinutes(30));
        Task task3 = new Task("task3", "description3");
        task3.setStartTime(LocalDateTime.now().minusMinutes(20));
        task3.setDuration(Duration.ofMinutes(40));
        Task task4 = new Task("task4", "description4");
        task4.setStartTime(LocalDateTime.now().plusMinutes(40));
        task4.setDuration(Duration.ofHours(1));
        Task task5 = new Task("task5", "description5");
        task5.setStartTime(LocalDateTime.now().plusMinutes(70));
        task5.setDuration(Duration.ofMinutes(40));

        assertThrows(TaskTimeException.class, () -> taskManager.addNewTask(task2));
        assertThrows(TaskTimeException.class, () -> taskManager.addNewTask(task3));
        assertThrows(TaskTimeException.class, () -> taskManager.addNewTask(task4));
        assertDoesNotThrow(() -> taskManager.addNewTask(task5), "Исключение выкидывается");
    }
}