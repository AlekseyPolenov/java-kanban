import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() {
        manager = new FileBackedTaskManager();
    }

    @AfterEach
    void cleanUp() throws IOException {
        Files.deleteIfExists(Paths.get("backup.csv"));
    }

    @Test
    void shouldSaveAndLoadTasks() {
        Task task = new Task("Task 1", "Description", StatusEnum.NEW);
        task.setId(0);
        manager.addTask(task);

        FileBackedTaskManager loadedManager = new FileBackedTaskManager();

        assertEquals(1, loadedManager.getTasks().size());
        assertEquals(task, loadedManager.getTask(1));
    }

    @Test
    void shouldSaveAndLoadEpics() {
        EpicTask epic = new EpicTask("Epic 1", "Description", StatusEnum.IN_PROGRESS);
        epic.setId(0);
        manager.addEpicTask(epic);

        FileBackedTaskManager loadedManager = new FileBackedTaskManager();

        assertEquals(1, loadedManager.getEpicTasks().size());
        assertEquals(epic, loadedManager.getEpicTask(1));
    }

    @Test
    void shouldSaveAndLoadSubtasks() {
        EpicTask epic = new EpicTask("Epic 1", "Description", StatusEnum.DONE);
        SubTask subTask = new SubTask("SubTask 1", "Description", StatusEnum.DONE, 1);
        epic.setId(1);
        subTask.setId(2);

        manager.addEpicTask(epic);
        manager.addSubTask(subTask);

        FileBackedTaskManager loadedManager = new FileBackedTaskManager();

        assertEquals(1, loadedManager.getSubTasks().size());
        assertEquals(subTask, loadedManager.getSubTask(2));
        assertEquals(1, loadedManager.getSubTasks(1).size());
    }

    @Test
    void shouldSaveAndLoadTaskUpdates() {
        Task task = new Task("Task 1", "Description", StatusEnum.NEW);
        task.setId(1);
        manager.addTask(task);

        task.setStatus(StatusEnum.IN_PROGRESS);
        manager.updateTask(task);

        FileBackedTaskManager loadedManager = new FileBackedTaskManager();

        assertEquals(StatusEnum.IN_PROGRESS, loadedManager.getTask(1).getStatus());
    }

    @Test
    void shouldSaveAndLoadTaskDeletions() {
        Task task = new Task("Task 1", "Description", StatusEnum.NEW);
        task.setId(1);
        manager.addTask(task);
        manager.removeTask(1);

        FileBackedTaskManager loadedManager = new FileBackedTaskManager();

        assertTrue(loadedManager.getTasks().isEmpty());
    }

    @Test
    void shouldPreserveTaskOrder() {
        Task task1 = new Task( "Task 1", "Description", StatusEnum.NEW);
        Task task2 = new Task("Task 2", "Description", StatusEnum.NEW);
        EpicTask epic = new EpicTask("Epic", "Description", StatusEnum.NEW);

        task1.setId(1);
        task2.setId(2);
        epic.setId(3);

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpicTask(epic);

        FileBackedTaskManager loadedManager = new FileBackedTaskManager();

        List<Task> tasks = loadedManager.getTasks();
        assertEquals(1, tasks.get(0).getId());
        assertEquals(2, tasks.get(1).getId());
        assertEquals(3, loadedManager.getEpicTasks().getFirst().getId());
    }
}