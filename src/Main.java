import enums.TaskStatus;
import enums.TaskType;
import managers.FileBackedTaskManager;
import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("task1", "description1", TaskStatus.NEW);
        Task task2 = new Task("task2", "description2", TaskStatus.IN_PROGRESS);
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        Epic epic1 = new Epic("epic1", "description1", TaskStatus.IN_PROGRESS);
        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("subtask1", "description1", TaskStatus.IN_PROGRESS,
                epic1.getId());
        Subtask subtask2 = new Subtask("subtask2", "description2", TaskStatus.IN_PROGRESS,
                epic1.getId());
        Subtask subtask3 = new Subtask("subtask3", "description3", TaskStatus.IN_PROGRESS,
                epic1.getId());
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);

        System.out.println(subtask1.getTaskType());
        System.out.println(epic1.getTaskType());
        System.out.println(subtask1.getTaskType().equals(TaskType.SUBTASK));

        TaskManager taskManager1 = FileBackedTaskManager.loadFromFile(Managers.FILE_CSV);
        Task task3 = new Task("task3", "description3", TaskStatus.NEW);
        taskManager1.addNewTask(task3);

        printAllTasks(taskManager1);
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
