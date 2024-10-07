package tasks;

import enums.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

class TaskTest {

    @Test
    void twoTasksWithSameIdShouldBeEquals() {
        Task task1 = new Task(1,"task1", "description1", TaskStatus.NEW, Duration.ofMinutes(100),
                LocalDateTime.of(2024, Month.JANUARY, 1, 8, 12));
        Task task2 = new Task(1,"task2", "description2", TaskStatus.IN_PROGRESS, Duration.ofMinutes(50),
                LocalDateTime.of(2024, Month.JANUARY, 4, 9, 44));

        Assertions.assertEquals(task1,task2,"id тасков не равны, попробуйте еще раз");
    }
}