package tasks;

import enums.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SubtaskTest {

    @Test
    void twoSubTasksWithSameIdShouldBeEquals() {
        Subtask subtask1 = new Subtask("Купить курицу", "В магазине", TaskStatus.NEW, 1);
        Subtask subtask2 = new Subtask("Купить мясо", "В мясном магазине", TaskStatus.IN_PROGRESS, 1);

        Assertions.assertEquals(subtask1,subtask2,"id подзадач не равны, попробуйте еще раз");
    }

}