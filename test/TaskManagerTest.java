import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    protected abstract T createManager();

    @BeforeEach
    void testSetUp() {
        manager = createManager();
    }

    @Test
    void testAddAndGetTask() {
        Task task = new Task("Task", "Desc", StatusEnum.NEW,
                LocalDateTime.now(), Duration.ofHours(1));
        manager.addTask(task);
        assertEquals(task, manager.getTask(task.getId()));
    }

    @Test
    void testAddAndGetEpic() {
        EpicTask epic = new EpicTask("Epic", "Desc", StatusEnum.NEW);
        manager.addEpicTask(epic);
        assertEquals(epic, manager.getEpicTask(epic.getId()));
    }

    @Test
    void testAddAndGetSubTask() {
        EpicTask epic = new EpicTask("Epic", "Desc", StatusEnum.NEW);
        manager.addEpicTask(epic);
        SubTask subTask = new SubTask("Sub", "Desc", StatusEnum.NEW,
                LocalDateTime.now(), Duration.ofHours(1), epic.getId());
        manager.addSubTask(subTask);
        assertEquals(subTask, manager.getSubTask(subTask.getId()));
    }
}