import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {


    private InMemoryHistoryManager manager;

    @BeforeEach
    public void init() {
        manager = new InMemoryHistoryManager();
    }

    @Test
    void addTaskHistory() {
        Task task1 = new Task("", "", StatusEnum.NEW);
        task1.setId(0);
        assertEquals(0, manager.getTasksHistory().size());

        manager.addTaskHistory(task1);

        assertEquals(1, manager.getTasksHistory().size());

        for (int i = 1; i < 10; i++) {
            Task task = new Task("", "", StatusEnum.NEW);
            task.setId(i);
            manager.addTaskHistory(task);
        }

        assertEquals(10, manager.getTasksHistory().size());

        Task task11 = new Task("", "", StatusEnum.NEW);
        task11.setId(11);

        manager.addTaskHistory(task11);

        assertEquals(10, manager.getTasksHistory().size());
        List<Task> tasksHistory = manager.getTasksHistory();
        assertFalse(tasksHistory.contains(task1));
        assertTrue(tasksHistory.contains(task11));
    }
}
