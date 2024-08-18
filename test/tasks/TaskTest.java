package tasks;

import enums.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    void twoTasksWithSameIdShouldBeEquals() {
        Task task1 = new Task(1, "Купить курицу", "В магазине", TaskStatus.NEW);
        Task task2 = new Task(1, "Купить мясо", "В мясном магазине", TaskStatus.IN_PROGRESS);

        Assertions.assertEquals(task1,task2,"id тасков не равны, попробуйте еще раз");
    }
}