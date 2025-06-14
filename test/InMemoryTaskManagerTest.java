import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }

    @BeforeEach
    public void init() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void addTask() {
        Task task = new Task("", "", StatusEnum.DONE,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1));
        task.setId(0);

        manager.addTask(task);

        assertEquals(true, task.getId() != 0);
        Task expectedTask = manager.getTasks().getFirst();
        assertEquals(expectedTask, task);
    }

    @Test
    void deleteTask() {
        Task task = new Task("", "", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1));
        manager.addTask(task);
        assertEquals(1, manager.getTasks().size());
        manager.deleteTask();
        assertEquals(0, manager.getTasks().size());
    }

    @Test
    void removeTask() {
        Task task1 = new Task("1", "1", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1));
        Task task2 = new Task("2", "2", StatusEnum.NEW,
                LocalDateTime.of(2024, 1, 1, 10, 0), Duration.ofHours(1));

        manager.addTask(task1);
        manager.addTask(task2);
        manager.getTask(task1.getId());

        assertEquals(2, manager.getTasks().size());

        List<Task> histories = manager.getTasksHistory();
        assertEquals(1, histories.size());
        assertEquals(task1, histories.getFirst());

        manager.removeTask(task1.getId());

        assertEquals(1, manager.getTasks().size());
        Task expectedTask = manager.getTasks().getFirst();
        assertEquals(expectedTask, task2);

        assertEquals(0, manager.getTasksHistory().size());
    }

    @Test
    void addEpicTask() {
        EpicTask epicTask = new EpicTask("", "", StatusEnum.DONE);
        epicTask.setId(0);

        manager.addEpicTask(epicTask);

        assertEquals(true, epicTask.getId() != 0);
        EpicTask expectedTask = manager.getEpicTasks().getFirst();
        assertEquals(expectedTask, epicTask);
    }

    @Test
    void deleteEpicTask() {
        EpicTask epicTask = new EpicTask("", "", StatusEnum.NEW);
        manager.addEpicTask(epicTask);
        assertEquals(1, manager.getEpicTasks().size());
        manager.deleteEpicTask();
        assertEquals(0, manager.getEpicTasks().size());
    }

    @Test
    void removeEpicTask() {
        EpicTask epicTask = new EpicTask("0", "0", StatusEnum.NEW);
        EpicTask epicTask1 = new EpicTask("1", "1", StatusEnum.NEW);

        manager.addEpicTask(epicTask);
        manager.addEpicTask(epicTask1);
        manager.getEpicTask(epicTask.getId());

        assertEquals(2, manager.getEpicTasks().size());

        List<Task> histories = manager.getTasksHistory();
        assertEquals(1, histories.size());
        assertEquals(epicTask, histories.getFirst());

        manager.removeEpicTask(epicTask.getId());

        assertEquals(1, manager.getEpicTasks().size());
        EpicTask expectedTask = manager.getEpicTasks().getFirst();
        assertEquals(expectedTask, epicTask1);

        assertEquals(0, manager.getTasksHistory().size());
    }

    @Test
    void addSubTask() {
        EpicTask epicTask = new EpicTask("", "", StatusEnum.NEW);
        manager.addEpicTask(epicTask);
        SubTask subTask = new SubTask("", "", StatusEnum.DONE,
                LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofHours(1), epicTask.getId());

        subTask.setId(0);
        manager.addSubTask(subTask);
        assertEquals(StatusEnum.DONE, epicTask.getStatus());
        assertEquals(1, epicTask.getSubTasks().size());
        assertEquals(subTask, epicTask.getSubTasks().getFirst());
    }

    @Test
    void deleteSubTask() {
        EpicTask epicTask = new EpicTask("", "", StatusEnum.NEW);
        manager.addEpicTask(epicTask);
        SubTask subTask = new SubTask("", "", StatusEnum.DONE,
                LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofHours(1), epicTask.getId());

        subTask.setId(0);
        manager.addSubTask(subTask);
        assertEquals(1, epicTask.getSubTasks().size());
        manager.deleteSubTask();
        assertEquals(0, manager.getSubTasks().size());
        assertEquals(0, epicTask.getSubTasks().size());
        assertEquals(StatusEnum.DONE, epicTask.getStatus());
    }

    @Test
    void removeSubTask() {
        EpicTask epic = new EpicTask("Epic", "Description", StatusEnum.NEW);
        manager.addEpicTask(epic);

        SubTask subTask1 = new SubTask("Sub1", "Desc1", StatusEnum.DONE,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1), epic.getId());
        SubTask subTask2 = new SubTask("Sub2", "Desc2", StatusEnum.IN_PROGRESS,
                LocalDateTime.of(2023, 1, 1, 12, 0), Duration.ofHours(1), epic.getId());

        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

        // Проверка добавления
        assertEquals(2, manager.getSubTasks().size());
        assertEquals(2, manager.getPrioritizedTasks().size());

        // Удаление первой подзадачи
        manager.removeSubTask(subTask1.getId());

        // Проверка удаления
        assertEquals(1, manager.getSubTasks().size());
        assertEquals(2, manager.getPrioritizedTasks().size());
        assertNull(manager.getSubTask(subTask1.getId()));

        // Проверка содержимого prioritizedTasks
        List<Task> prioritized = manager.getPrioritizedTasks();
        assertTrue(prioritized.contains(subTask2));
        assertTrue(prioritized.contains(epic));

        // Удаление последней подзадачи
        manager.removeSubTask(subTask2.getId());
        assertEquals(0, manager.getSubTasks().size());
        assertEquals(0, manager.getPrioritizedTasks().size());
    }

    @Test
    void getTask() {
        Task task = new Task("", "", StatusEnum.DONE,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1));
        manager.addTask(task);

        assertEquals(0, manager.getTasksHistory().size());

        Task actualTask = manager.getTask(task.getId());

        assertEquals(task, actualTask);
        assertEquals(1, manager.getTasksHistory().size());
        Task actualFromHistory = manager.getTasksHistory().getFirst();
        assertEquals(task, actualFromHistory);

        List<Task> histories = manager.getTasksHistory();
        assertEquals(1, histories.size());
        assertEquals(task, histories.getFirst());
    }

    @Test
    void getEpicTask() {
        EpicTask epicTask = new EpicTask("", "", StatusEnum.DONE);
        manager.addEpicTask(epicTask);
        assertEquals(0, manager.getTasksHistory().size());

        EpicTask actualTask = manager.getEpicTask(epicTask.getId());

        assertEquals(epicTask, actualTask);
        assertEquals(1, manager.getTasksHistory().size());
        Task actualFromHistory = manager.getTasksHistory().getFirst();
        assertEquals(epicTask, actualFromHistory);

        List<Task> histories = manager.getTasksHistory();
        assertEquals(1, histories.size());
        assertEquals(epicTask, histories.getFirst());
    }

    @Test
    void getSubTask() {
        EpicTask epicTask = new EpicTask("", "", StatusEnum.DONE);
        manager.addEpicTask(epicTask);
        SubTask subTask = new SubTask("", "", StatusEnum.DONE,
                LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofHours(1), epicTask.getId());

        manager.addSubTask(subTask);
        assertEquals(0, manager.getTasksHistory().size());

        SubTask actualTask = manager.getSubTask(subTask.getId());

        assertEquals(subTask, actualTask);
        assertEquals(1, manager.getTasksHistory().size());
        Task actualFromHistory = manager.getTasksHistory().getFirst();
        assertEquals(subTask, actualFromHistory);

        List<Task> histories = manager.getTasksHistory();
        assertEquals(1, histories.size());
        assertEquals(subTask, histories.getFirst());
    }

    @Test
    void getSubTasks() {
        EpicTask epicTask = new EpicTask("", "", StatusEnum.NEW);
        manager.addEpicTask(epicTask);

        SubTask subTask = new SubTask("", "", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofHours(1), epicTask.getId());

        SubTask subTask1 = new SubTask("", "", StatusEnum.NEW,
                LocalDateTime.of(2023, 2, 1, 10, 0),
                Duration.ofHours(1), epicTask.getId());

        manager.addSubTask(subTask);
        manager.addSubTask(subTask1);
        assertEquals(2, manager.getSubTasks().size());
    }

    @Test
    void immutableTaskAfterAddTest() {
        Task task = new Task("Отпуск", "Мальдивы", StatusEnum.DONE,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1));

        manager.addTask(task);
        Task actualTask = manager.getTask(task.getId());

        assertEquals(task.getName(), actualTask.getName());
        assertEquals(task.getDescription(), actualTask.getDescription());
        assertEquals(task.getStatus(), actualTask.getStatus());
    }

    @Test
    void immutableEpicTaskAfterAddTest() {
        EpicTask epicTask = new EpicTask("", "", StatusEnum.DONE);

        manager.addEpicTask(epicTask);
        EpicTask actualTask = manager.getEpicTask(epicTask.getId());

        assertEquals(epicTask.getName(), actualTask.getName());
        assertEquals(epicTask.getDescription(), actualTask.getDescription());
        assertEquals(epicTask.getStatus(), actualTask.getStatus());
        assertEquals(epicTask.getSubTasks(), actualTask.getSubTasks());
    }

    @Test
    void immutableSubTaskAfterAddTest() {
        SubTask subTask = new SubTask("", "", StatusEnum.DONE,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1),
                1);

        manager.addSubTask(subTask);
        SubTask actualTask = manager.getSubTask(subTask.getId());

        assertEquals(subTask.getName(), actualTask.getName());
        assertEquals(subTask.getDescription(), actualTask.getDescription());
        assertEquals(subTask.getStatus(), actualTask.getStatus());
        assertEquals(subTask.getEpicId(), actualTask.getEpicId());
    }

    @Test
    void updateTaskTest() {
        Task task = new Task("", "", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1));

        manager.addTask(task);

        Task updatedTask = new Task("Поездка", "на поезде", StatusEnum.IN_PROGRESS,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1));
        updatedTask.setId(task.getId());

        manager.updateTask(updatedTask);
        task = manager.getTask(task.getId());

        assertEquals("Поездка", task.getName());
        assertEquals("на поезде", task.getDescription());
        assertEquals(StatusEnum.IN_PROGRESS, task.getStatus());
    }

    @Test
    void updateEpicTaskTest() {
        EpicTask originalEpic = new EpicTask("Original", "Original description", StatusEnum.NEW);
        manager.addEpicTask(originalEpic);
        long epicId = originalEpic.getId();

        SubTask subTask1 = new SubTask("SubTask 1", "Desc 1", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1), epicId);
        SubTask subTask2 = new SubTask("SubTask 2", "Desc 2", StatusEnum.DONE,
                LocalDateTime.of(2023, 1, 1, 12, 0), Duration.ofHours(2), epicId);

        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

        assertEquals(StatusEnum.IN_PROGRESS, originalEpic.getStatus());
        assertNotNull(originalEpic.getStartTime());
        assertNotNull(originalEpic.getEndTime());

        EpicTask updatedEpic = new EpicTask("Updated", "Updated description", StatusEnum.DONE);
        updatedEpic.setId(epicId);

        manager.updateEpicTask(updatedEpic);

        EpicTask retrievedEpic = manager.getEpicTask(epicId);

        assertEquals("Updated", retrievedEpic.getName());
        assertEquals("Updated description", retrievedEpic.getDescription());

        assertEquals(StatusEnum.IN_PROGRESS, retrievedEpic.getStatus());

        assertEquals(2, retrievedEpic.getSubTasks().size());

        assertNotNull(retrievedEpic.getStartTime());
        assertNotNull(retrievedEpic.getEndTime());

        assertTrue(manager.getPrioritizedTasks().contains(retrievedEpic));
    }

    @Test
    void updateSubTaskTest() {
        SubTask subTask = new SubTask("", "", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1),1);

        manager.addSubTask(subTask);

        SubTask updatedTask = new SubTask("Поездка", "на поезде", StatusEnum.IN_PROGRESS,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1), 2);
        updatedTask.setId(subTask.getId());

        manager.updateSubTask(updatedTask);
        subTask = manager.getSubTask(subTask.getId());

        assertEquals("Поездка", subTask.getName());
        assertEquals("на поезде", subTask.getDescription());
        assertEquals(StatusEnum.IN_PROGRESS, subTask.getStatus());
    }

    @Test
    void isTimeOverlap() {
        LocalDateTime baseTime = LocalDateTime.of(2023, 1, 1, 10, 0);

        Task task1 = new Task("Task1", "Desc", StatusEnum.NEW,
                baseTime, Duration.ofHours(1));
        Task task2 = new Task("Task2", "Desc", StatusEnum.NEW,
                baseTime.plusHours(2), Duration.ofHours(1));

        assertFalse(manager.isTimeOverlap(task1, task2));

        Task task3 = new Task("Task3", "Desc", StatusEnum.NEW,
                baseTime.plusMinutes(30), Duration.ofHours(1));

        assertTrue(manager.isTimeOverlap(task1, task3));
    }

    @Test
    void hasTimeOverlap() {
        Task task1 = new Task("Task1", "Desc", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1));
        manager.addTask(task1);

        Task task2 = new Task("Task2", "Desc", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 30), Duration.ofHours(1));

        assertTrue(manager.hasTimeOverlap(task2));
    }

    @Test
    void getPrioritizedTasks() {
        Task task1 = new Task("Task1", "Desc", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 12, 0), Duration.ofHours(1));
        Task task2 = new Task("Task2", "Desc", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1));

        manager.addTask(task1);
        manager.addTask(task2);

        List<Task> prioritized = manager.getPrioritizedTasks();

        assertEquals(2, prioritized.size());
        assertEquals(task2, prioritized.get(0));
        assertEquals(task1, prioritized.get(1));
    }
}