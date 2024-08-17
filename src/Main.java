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

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        Task task3 = new Task("Сделать работу", "123", TaskStatus.DONE);
        taskManager.updateTask(task3);

        Epic epic1 = new Epic("Сделать работу", "Нужно обзвонить потенциальных клиентов",
                TaskStatus.NEW);
        taskManager.addNewEpic(epic1);

        Subtask subtask1 = new Subtask("Позвонить по направлению Краснодар", "Прикубанский и" +
                " Карасунский округи", TaskStatus.IN_PROGRESS, epic1.getId());
        Subtask subtask2 = new Subtask("Позвонить по направлению Ростов", "Ворошиловский, Октябрьский" +
                " и Советский районы", TaskStatus.IN_PROGRESS, epic1.getId());
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        Epic epic2 = new Epic("Прочитать книгу", "Прочитать книгу за 3 дня", TaskStatus.NEW);
        taskManager.addNewEpic(epic2);

        Subtask subtask3 = new Subtask("Прочитать первые 10 страниц", "Успеть до конца дня прочитать " +
                "10 страниц", TaskStatus.IN_PROGRESS, epic2.getId());

        taskManager.addNewSubtask(subtask3);
        task1.setTaskStatus(TaskStatus.IN_PROGRESS);
        subtask3.setTaskStatus(TaskStatus.DONE);
        taskManager.deleteTaskById(1);
        taskManager.deleteEpicTaskById(3);

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
