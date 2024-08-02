import managers.TaskManager;
import enums.TaskStatus;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Помыть машину", "В четверг нужно помыть машину", TaskStatus.NEW);
        Task task2 = new Task("Приготовить ужин", "Приготовить котлеты с пюрешкой",
                TaskStatus.IN_PROGRESS);

        System.out.println(taskManager.addNewTask(task1));
        System.out.println(taskManager.addNewTask(task2));

        Epic epic1 = new Epic("Сделать работу", "Нужно обзвонить потенциальных клиентов",
                TaskStatus.NEW);
        System.out.println(taskManager.addNewEpic(epic1));

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
        taskManager.updateEpicTaskStatus(epic2);
        System.out.println(epic2);

        taskManager.deleteTaskById(1);
        System.out.println(taskManager.getTasks());

        taskManager.deleteEpicTaskById(3);
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubTasks());
    }
}
