package managers;

import models.StatusEnum;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

class InMemoryHistoryManagerTest {


    private InMemoryHistoryManager manager;

    @BeforeEach
    public void init() {
        manager = new InMemoryHistoryManager();
    }

    @Test
    void addTaskHistory() {
        Task task1 = new Task("111", "1", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1));
        task1.setId(1);
        Task task2 = new Task("222", "2", StatusEnum.IN_PROGRESS,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1));
        task2.setId(2);
        Task task3 = new Task("3", "3", StatusEnum.DONE,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1));
        task3.setId(0);

        manager.addTaskHistory(task1);
        manager.addTaskHistory(task2);
        manager.addTaskHistory(task3);

        task1.setName("1_Copy");
        task1.setDescription("1_Copy");
        task1.setStatus(StatusEnum.DONE);

        manager.addTaskHistory(task1);
        manager.addTaskHistory(task1);
        manager.addTaskHistory(task1);

        List<Task> histories = manager.getTasksHistory();

        assertEquals(3, histories.size());
        assertEquals(histories.get(0), task2);
        assertEquals(histories.get(1), task3);
        assertEquals(histories.get(2), task1);
    }

    @Test
    void addTaskHistoryWithNullTask() {
        manager.addTaskHistory(null);

        assertEquals(0, manager.getTasksHistory().size());
    }

    @Test
    void removeTaskHistory() {
        Task task1 = new Task("111", "1", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1));
        task1.setId(0);
        Task task2 = new Task("222", "2", StatusEnum.IN_PROGRESS,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1));
        task2.setId(1);

        manager.addTaskHistory(task1);
        manager.addTaskHistory(task2);

        assertEquals(2, manager.getTasksHistory().size());

        manager.removeTaskHistory(0L);

        List<Task> histories = manager.getTasksHistory();

        assertEquals(1, histories.size());
        assertFalse(histories.contains(task1));
    }

}
