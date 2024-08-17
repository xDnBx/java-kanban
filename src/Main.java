import enums.TaskStatus;
import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Помыть машину", "В четверг нужно помыть машину", TaskStatus.NEW);
        Task task2 = new Task("Приготовить ужин", "Приготовить котлеты с пюрешкой",
                TaskStatus.IN_PROGRESS);

        System.out.println(taskManager.addNewTask(task1));
        System.out.println(taskManager.addNewTask(task2));

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);

        Task task3 = new Task("Сделать работу", "123", TaskStatus.DONE);
        taskManager.updateTask(task3);
        taskManager.getTasks();

        Epic epic1 = new Epic("Сделать работу", "Нужно обзвонить потенциальных клиентов",
                TaskStatus.NEW);
        System.out.println(taskManager.addNewEpic(epic1));
        taskManager.getEpicById(3);

        Subtask subtask1 = new Subtask("Позвонить по направлению Краснодар", "Прикубанский и" +
                " Карасунский округи", TaskStatus.IN_PROGRESS, epic1.getId());
        Subtask subtask2 = new Subtask("Позвонить по направлению Ростов", "Ворошиловский, Октябрьский" +
                " и Советский районы", TaskStatus.IN_PROGRESS, epic1.getId());
        System.out.println(taskManager.addNewSubtask(subtask1));
        System.out.println(taskManager.addNewSubtask(subtask2));

        System.out.println(epic1);
        Epic epic2 = new Epic("Прочитать книгу", "Прочитать книгу за 3 дня", TaskStatus.NEW);
        System.out.println(taskManager.addNewEpic(epic2));
        Subtask subtask3 = new Subtask("Прочитать первые 10 страниц", "Успеть до конца дня прочитать " +
                "10 страниц", TaskStatus.IN_PROGRESS, epic2.getId());

        System.out.println(taskManager.addNewSubtask(subtask3));
        System.out.println(epic2);

        task1.setTaskStatus(TaskStatus.IN_PROGRESS);
        System.out.println(task1);

        subtask3.setTaskStatus(TaskStatus.DONE);
        System.out.println(epic2);

        taskManager.deleteTaskById(1);
        System.out.println(taskManager.getTasks());

        taskManager.deleteEpicTaskById(3);
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubTasks());

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
