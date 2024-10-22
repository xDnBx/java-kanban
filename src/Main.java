import enums.TaskStatus;
import managers.FileBackedTaskManager;
import managers.Managers;
import managers.interfaces.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

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

        TaskManager taskManager1 = FileBackedTaskManager.loadFromFile(Managers.FILE_CSV);
        Task task3 = new Task("task3", "description3", TaskStatus.NEW, Duration.ofMinutes(90),
                LocalDateTime.of(2024, Month.JANUARY, 6, 13, 48));
        taskManager1.addNewTask(task3);
        System.out.println(taskManager1.getPrioritizedTasks());

        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasksFromEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
