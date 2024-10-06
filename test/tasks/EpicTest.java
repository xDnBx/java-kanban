package tasks;

import enums.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EpicTest {

    @Test
    void twoEpicsWithSameIdShouldBeEquals() {
        Epic epic1 = new Epic(1, "Купить курицу", "В магазине");
        Epic epic2 = new Epic(1, "Купить мясо", "В мясном магазине");

        Assertions.assertEquals(epic1, epic2,"id эпиков не равны, попробуйте еще раз");
    }
}