package managers;

import models.EpicTask;
import models.StatusEnum;
import models.SubTask;
import models.Task;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import exceptions.ManagerSaveException;
import exceptions.ManagerLoadException;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private Path tempFile; // Объявляем переменную на уровне класса

    @Override
    protected FileBackedTaskManager createManager() {
        try {
            tempFile = Files.createTempFile("tasks", ".csv");
            return new FileBackedTaskManager(tempFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        manager = createManager();
    }

    @AfterEach
    void cleanUp() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    void shouldSaveAndLoadTasks() {
        Task task = new Task("Task 1", "Description", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1));
        task.setId(0);
        manager.addTask(task);

        FileBackedTaskManager loadedManager = new FileBackedTaskManager(tempFile);

        assertEquals(1, loadedManager.getTasks().size());
        assertEquals(task, loadedManager.getTask(1));
    }

    @Test
    void shouldSaveAndLoadEpics() {
        EpicTask epic = new EpicTask("Epic 1", "Description", StatusEnum.IN_PROGRESS);
        epic.setId(0);
        manager.addEpicTask(epic);

        FileBackedTaskManager loadedManager = new FileBackedTaskManager(tempFile);

        assertEquals(1, loadedManager.getEpicTasks().size());
        assertEquals(epic, loadedManager.getEpicTask(1));
    }

    @Test
    void shouldSaveAndLoadSubtasks() {
        EpicTask epic = new EpicTask("Epic 1", "Description", StatusEnum.DONE);
        SubTask subTask = new SubTask("SubTask 1", "Description", StatusEnum.DONE,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1), 1);

        epic.setId(1);
        subTask.setId(2);

        manager.addEpicTask(epic);
        manager.addSubTask(subTask);

        FileBackedTaskManager loadedManager = new FileBackedTaskManager(tempFile);

        assertEquals(1, loadedManager.getSubTasks().size());
        assertEquals(subTask, loadedManager.getSubTask(2));
        assertEquals(1, loadedManager.getSubTasks(1).size());
    }

    @Test
    void shouldSaveAndLoadTaskUpdates() {
        Task task = new Task("Task 1", "Description", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1));
        task.setId(1);
        manager.addTask(task);

        task.setStatus(StatusEnum.IN_PROGRESS);
        manager.updateTask(task);

        FileBackedTaskManager loadedManager = new FileBackedTaskManager(tempFile);

        assertEquals(StatusEnum.IN_PROGRESS, loadedManager.getTask(1).getStatus());
    }

    @Test
    void shouldSaveAndLoadTaskDeletions() {
        Task task = new Task("Task 1", "Description", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1));
        task.setId(1);
        manager.addTask(task);
        manager.removeTask(1);

        FileBackedTaskManager loadedManager = new FileBackedTaskManager(tempFile);

        assertTrue(loadedManager.getTasks().isEmpty());
    }

    @Test
    void shouldPreserveTaskOrder() {
        Task task1 = new Task("Task 1", "Description", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1));
        Task task2 = new Task("Task 2", "Description", StatusEnum.NEW,
                LocalDateTime.of(2024, 1, 1, 10, 0), Duration.ofHours(1));
        EpicTask epic = new EpicTask("Epic", "Description", StatusEnum.NEW);

        task1.setId(1);
        task2.setId(2);
        epic.setId(3);

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpicTask(epic);

        FileBackedTaskManager loadedManager = new FileBackedTaskManager(tempFile);

        List<Task> tasks = loadedManager.getTasks();
        assertEquals(1, tasks.get(0).getId());
        assertEquals(2, tasks.get(1).getId());
        assertEquals(3, loadedManager.getEpicTasks().getFirst().getId());
    }

    @Test
    void taskToCsvString() {
        Task task = new Task("Task", "Desc", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1));
        task.setId(1);

        String csv = manager.taskToCsvString(task);
        String expected = "1,Task,Task,NEW,Desc,,2023-01-01T10:00,60";
        assertEquals(expected, csv);
    }

    @Test
    void getType() {
        Task task = new Task("", "", StatusEnum.NEW, null, null);
        EpicTask epic = new EpicTask("", "", StatusEnum.NEW);
        SubTask sub = new SubTask("", "", StatusEnum.NEW, null, null, 1);

        assertEquals("Task", manager.getType(task));
        assertEquals("EpicTask", manager.getType(epic));
        assertEquals("SubTask", manager.getType(sub));
    }

    @Test
    void fromCsvString() {
        String csv = "1,Task,Task1,NEW,Desc,,2023-01-01T10:00,60";
        Task task = manager.fromCsvString(csv);

        assertEquals(1, task.getId());
        assertEquals("Task1", task.getName());
        assertEquals(StatusEnum.NEW, task.getStatus());
        assertEquals(LocalDateTime.of(2023, 1, 1, 10, 0), task.getStartTime());
        assertEquals(Duration.ofHours(1), task.getDuration());
    }

    @Test
    void fileOperationsExceptions() {

        Path readOnlyFile = Paths.get("readonly.csv");
        try {
            if (!Files.exists(readOnlyFile)) {
                Files.createFile(readOnlyFile);
            }
            assertTrue(readOnlyFile.toFile().setReadOnly(), "Не удалось установить read-only");

            FileBackedTaskManager readOnlyManager = new FileBackedTaskManager(readOnlyFile);
            Task task = new Task("Task", "Desc", StatusEnum.NEW, null, null);

            assertThrows(ManagerSaveException.class, () -> readOnlyManager.addTask(task), "исключение при сохранении в read-only файл");

        } catch (IOException e) {
            fail("Ошибка при настройке теста", e);
        } finally {
            try {
                readOnlyFile.toFile().setWritable(true);
                Files.deleteIfExists(readOnlyFile);
            } catch (IOException e) {
            }
        }


        Path nonExistentFile = Paths.get("nonexistent_" + System.currentTimeMillis() + ".csv");
        assertThrows(ManagerLoadException.class, () -> new FileBackedTaskManager(nonExistentFile), "исключение при загрузке из несуществующего файла");
    }
}