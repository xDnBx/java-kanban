package tasks;

import enums.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

class SubtaskTest {

    @Test
    void twoSubTasksWithSameIdShouldBeEquals() {
        Subtask subtask1 = new Subtask(1,"subtask1", "description1", TaskStatus.NEW,
                Duration.ofMinutes(55), LocalDateTime.of(2024, Month.JANUARY, 3, 10, 45), 1);
        Subtask subtask2 = new Subtask(1,"subtask2", "description2", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(60), LocalDateTime.of(2024, Month.JANUARY, 2, 11, 46), 1);

        Assertions.assertEquals(subtask1,subtask2,"id подзадач не равны, попробуйте еще раз");
    }

}