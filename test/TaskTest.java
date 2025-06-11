import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void testEquals() {
        Task movingTask = new Task("Переезд", "В хрущевку", StatusEnum.NEW);
        Task vacationTask = new Task("Отпуск", "В Казахстан", StatusEnum.DONE);

        movingTask.setId(1);
        vacationTask.setId(2);

        assertEquals(false, movingTask.equals(vacationTask));

        movingTask.setId(0);
        vacationTask.setId(0);

        assertEquals(true, movingTask.equals(vacationTask));
    }

    @Test
    void testHashCode() {
        Task task = new Task("", "", StatusEnum.NEW);
        task.setId(1);

        assertEquals(Objects.hashCode(1), task.hashCode());
    }
}