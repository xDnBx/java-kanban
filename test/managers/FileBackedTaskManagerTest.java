package managers;

import enums.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest {
    protected File tempFile;
    TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        try {
            tempFile = File.createTempFile("data1", ".csv");
            taskManager = new FileBackedTaskManager(tempFile);
            tempFile.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldSaveTasksToFile() {
        Task task1 = new Task("task1", "description1", TaskStatus.NEW, Duration.ofMinutes(100),
                LocalDateTime.of(2024, Month.JANUARY, 1, 8, 12));
        Task task2 = new Task("task2", "description2", TaskStatus.IN_PROGRESS, Duration.ofMinutes(50),
                LocalDateTime.of(2024, Month.JANUARY, 4, 9, 44));
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        Epic epic1 = new Epic("epic1", "description1");
        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("subtask1", "description1", TaskStatus.NEW,
                Duration.ofMinutes(55), LocalDateTime.of(2024, Month.JANUARY, 3, 10, 45), epic1.getId());
        Subtask subtask2 = new Subtask("subtask2", "description2", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(60), LocalDateTime.of(2024, Month.JANUARY, 2, 11, 46), epic1.getId());
        Subtask subtask3 = new Subtask("subtask3", "description3", TaskStatus.DONE,
                Duration.ofMinutes(80), LocalDateTime.of(2024, Month.JANUARY, 5, 12, 47), epic1.getId());
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);

        String tasks = FileBackedTaskManager.HEADER +
                ";1,TASK,task1,NEW,description1,100,2024-01-01T08:12,,;" +
                "2,TASK,task2,IN_PROGRESS,description2,50,2024-01-04T09:44,,;" +
                "3,EPIC,epic1,IN_PROGRESS,description1,195,2024-01-02T11:46,2024-01-05T14:07;" +
                "4,SUBTASK,subtask1,NEW,description1,55,2024-01-03T10:45,,3;" +
                "5,SUBTASK,subtask2,IN_PROGRESS,description2,60,2024-01-02T11:46,,3;" +
                "6,SUBTASK,subtask3,DONE,description3,80,2024-01-05T12:47,,3";
        List<String> listTasks = new ArrayList<>(List.of(tasks.split(";")));

        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(tempFile))) {
            while (br.ready()) {
                lines.add(br.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertArrayEquals(listTasks.toArray(), lines.toArray(), "Листы не равны");
    }

    @Test
    void shouldLoadTasksFromFile() {
        Task task1 = new Task("task1", "description1", TaskStatus.NEW, Duration.ofMinutes(100),
                LocalDateTime.of(2024, Month.JANUARY, 1, 8, 12));
        Task task2 = new Task("task2", "description2", TaskStatus.IN_PROGRESS, Duration.ofMinutes(50),
                LocalDateTime.of(2024, Month.JANUARY, 4, 9, 44));
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        Epic epic1 = new Epic("epic1", "description1");
        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("subtask1", "description1", TaskStatus.NEW,
                Duration.ofMinutes(55), LocalDateTime.of(2024, Month.JANUARY, 3, 10, 45), epic1.getId());
        Subtask subtask2 = new Subtask("subtask2", "description2", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(60), LocalDateTime.of(2024, Month.JANUARY, 2, 11, 46), epic1.getId());
        Subtask subtask3 = new Subtask("subtask3", "description3", TaskStatus.DONE,
                Duration.ofMinutes(80), LocalDateTime.of(2024, Month.JANUARY, 5, 12, 47), epic1.getId());
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);

        TaskManager taskManager1 = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(taskManager.getTaskById(1).getId(), taskManager1.getTaskById(1).getId(),
                "id задач не равны");
        assertEquals(taskManager.getTaskById(1).getName(), taskManager1.getTaskById(1).getName(),
                "Имена задач не равны");
        assertEquals(taskManager.getTaskById(1).getDescription(), taskManager1.getTaskById(1).getDescription(),
                "Описания задач не равны");
        assertEquals(taskManager.getTaskById(1).getTaskStatus(), taskManager1.getTaskById(1).getTaskStatus(),
                "Статусы задач не равны");
        assertEquals(taskManager.getTaskById(1).getDuration(), taskManager1.getTaskById(1).getDuration(),
                "Продолжительности задач не равны");
        assertEquals(taskManager.getTaskById(1).getStartTime(), taskManager1.getTaskById(1).getStartTime(),
                "Время начала задач не равны");
        assertEquals(taskManager.getEpicById(3).getId(), taskManager1.getEpicById(3).getId(),
                "id эпиков не равны");
        assertEquals(taskManager.getEpicById(3).getName(), taskManager1.getEpicById(3).getName(),
                "Имена эпиков не равны");
        assertEquals(taskManager.getEpicById(3).getDescription(), taskManager1.getEpicById(3).getDescription(),
                "Описания эпиков не равны");
        assertEquals(taskManager.getEpicById(3).getTaskStatus(), taskManager1.getEpicById(3).getTaskStatus(),
                "Статусы эпиков не равны");
        assertEquals(taskManager.getEpicById(3).getDuration(), taskManager1.getEpicById(3).getDuration(),
                "Продолжительности эпиков не равны");
        assertEquals(taskManager.getEpicById(3).getStartTime(), taskManager1.getEpicById(3).getStartTime(),
                "Время начала эпиков не равны");
        assertEquals(taskManager.getEpicById(3).getEndTime(), taskManager1.getEpicById(3).getEndTime(),
                "Время окончания эпиков не равны");
        assertArrayEquals(taskManager.getEpicById(3).getSubtaskEpic().toArray(),
                taskManager1.getEpicById(3).getSubtaskEpic().toArray(), "Списки подзадач эпиков не равны");
        assertEquals(taskManager.getSubtaskById(4).getId(),
                taskManager1.getSubtaskById(4).getId(), "id подзадач не равны");
        assertEquals(taskManager.getSubtaskById(4).getName(),
                taskManager1.getSubtaskById(4).getName(), "Имена подзадач не равны");
        assertEquals(taskManager.getSubtaskById(4).getDescription(),
                taskManager1.getSubtaskById(4).getDescription(), "Описания подзадач не равны");
        assertEquals(taskManager.getSubtaskById(4).getTaskStatus(),
                taskManager1.getSubtaskById(4).getTaskStatus(), "Статусы подзадач не равны");
        assertEquals(taskManager.getSubtaskById(4).getDuration(),
                taskManager1.getSubtaskById(4).getDuration(),"Продолжительности подзадач не равны");
        assertEquals(taskManager.getSubtaskById(4).getStartTime(),
                taskManager1.getSubtaskById(4).getStartTime(),"Время начала подзадач не равны");
        assertEquals(taskManager.getSubtaskById(4).getEpicTaskId(),
                taskManager1.getSubtaskById(4).getEpicTaskId(), "Эпики подзадач не равны");

    }

    @Test
    void shouldLoadEmptyFile() {
        TaskManager taskManager1 = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(taskManager1.getTasks().isEmpty(), "Файл не пустой");
        assertTrue(taskManager1.getEpics().isEmpty(), "Файл не пустой");
        assertTrue(taskManager1.getSubTasks().isEmpty(), "Файл не пустой");
    }
}