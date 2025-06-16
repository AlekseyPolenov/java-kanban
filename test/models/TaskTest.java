package models;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void testEquals() {
        Task movingTask = new Task("Переезд", "В хрущевку", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1));

        Task vacationTask = new Task("Отпуск", "В Казахстан", StatusEnum.DONE,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1));

        movingTask.setId(1);
        vacationTask.setId(2);

        assertEquals(false, movingTask.equals(vacationTask));

        movingTask.setId(0);
        vacationTask.setId(0);

        assertEquals(true, movingTask.equals(vacationTask));
    }

    @Test
    void testHashCode() {
        Task task = new Task("", "", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1));
        task.setId(1);

        assertEquals(Objects.hashCode(1), task.hashCode());
    }

    @Test
    void getStartTime() {
        LocalDateTime time = LocalDateTime.now();
        Task task = new Task("Task", "Desc", StatusEnum.NEW, time, Duration.ofHours(1));
        assertEquals(time, task.getStartTime());
    }

    @Test
    void getDuration() {
        Duration duration = Duration.ofHours(2);
        Task task = new Task("Task", "Desc", StatusEnum.NEW, null, duration);
        assertEquals(duration, task.getDuration());
    }

    @Test
    void getEndTime() {
        LocalDateTime time = LocalDateTime.of(2023, 1, 1, 10, 0);
        Task task = new Task("Task", "Desc", StatusEnum.NEW, time, Duration.ofHours(1));
        assertEquals(time.plusHours(1), task.getEndTime());
    }
}