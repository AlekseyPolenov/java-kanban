import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private InMemoryTaskManager manager;

    @BeforeEach
    public void init() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void addTask() {
        Task task = new Task("", "", StatusEnum.DONE);
        task.setId(0);

        manager.addTask(task);

        assertEquals(true, task.getId() != 0);
        Task expectedTask = manager.getTasks().getFirst();
        assertEquals(expectedTask, task);
    }

    @Test
    void deleteTask() {
        Task task = new Task("", "", StatusEnum.NEW);
        manager.addTask(task);
        assertEquals(1, manager.getTasks().size());
        manager.deleteTask();
        assertEquals(0, manager.getTasks().size());
    }

    @Test
    void removeTask() {
        Task task1 = new Task("1", "1", StatusEnum.NEW);
        Task task2 = new Task("2", "2", StatusEnum.NEW);

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
        SubTask subTask = new SubTask("", "", StatusEnum.DONE, epicTask.getId());
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
        SubTask subTask = new SubTask("", "", StatusEnum.DONE, epicTask.getId());
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
        EpicTask epicTask = new EpicTask("12", "12", StatusEnum.NEW);
        manager.addEpicTask(epicTask);

        SubTask subTask = new SubTask("0", "0", StatusEnum.DONE, epicTask.getId());
        SubTask subTask1 = new SubTask("1", "1", StatusEnum.IN_PROGRESS, epicTask.getId());

        manager.addSubTask(subTask);
        manager.addSubTask(subTask1);
        manager.getSubTask(subTask.getId());

        assertEquals(StatusEnum.IN_PROGRESS, epicTask.getStatus());
        assertEquals(2, epicTask.getSubTasks().size());
        assertEquals(2, manager.getSubTasks().size());

        List<Task> histories = manager.getTasksHistory();
        assertEquals(1, histories.size());
        assertEquals(subTask, histories.getFirst());

        manager.removeSubTask(subTask1.getId());

        assertEquals(1, manager.getSubTasks().size());
        SubTask expectedTask = manager.getSubTasks().getFirst();
        assertEquals(expectedTask, subTask);

        assertEquals(1, epicTask.getSubTasks().size());
        SubTask expectedTaskFromEpic = epicTask.getSubTasks().getFirst();
        assertEquals(expectedTaskFromEpic, subTask);
        assertEquals(StatusEnum.DONE, epicTask.getStatus());

        manager.removeSubTask(subTask.getId());

        assertEquals(0, manager.getTasksHistory().size());
    }

    @Test
    void getTask() {
        Task task = new Task("", "", StatusEnum.DONE);
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
        SubTask subTask = new SubTask("", "", StatusEnum.DONE, epicTask.getId());
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
        SubTask subTask = new SubTask("", "", StatusEnum.NEW, epicTask.getId());
        SubTask subTask1 = new SubTask("", "", StatusEnum.NEW, epicTask.getId());
        manager.addSubTask(subTask);
        manager.addSubTask(subTask1);
        assertEquals(2, manager.getSubTasks().size());
    }

    @Test
    void immutableTaskAfterAddTest() {
        Task task = new Task("Отпуск", "Мальдивы", StatusEnum.DONE);

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
        SubTask subTask = new SubTask("", "", StatusEnum.DONE, 1);

        manager.addSubTask(subTask);
        SubTask actualTask = manager.getSubTask(subTask.getId());

        assertEquals(subTask.getName(), actualTask.getName());
        assertEquals(subTask.getDescription(), actualTask.getDescription());
        assertEquals(subTask.getStatus(), actualTask.getStatus());
        assertEquals(subTask.getEpicId(), actualTask.getEpicId());
    }

    @Test
    void updateTaskTest() {
        Task task = new Task("", "", StatusEnum.NEW);

        manager.addTask(task);

        Task updatedTask = new Task("Поездка", "на поезде", StatusEnum.IN_PROGRESS);
        updatedTask.setId(task.getId());

        manager.updateTask(updatedTask);
        task = manager.getTask(task.getId());

        assertEquals("Поездка", task.getName());
        assertEquals("на поезде", task.getDescription());
        assertEquals(StatusEnum.IN_PROGRESS, task.getStatus());
    }

    @Test
    void updateEpicTaskTest() {
        EpicTask epicTask = new EpicTask("", "", StatusEnum.NEW);

        manager.addEpicTask(epicTask);

        EpicTask updatedEpicTask = new EpicTask("Поездка", "на поезде", StatusEnum.IN_PROGRESS);
        updatedEpicTask.setId(epicTask.getId());

        manager.updateEpicTask(updatedEpicTask);
        epicTask = manager.getEpicTask(epicTask.getId());

        assertEquals("Поездка", epicTask.getName());
        assertEquals("на поезде", epicTask.getDescription());
        assertEquals(StatusEnum.IN_PROGRESS, epicTask.getStatus());
    }

    @Test
    void updateSubTaskTest() {
        SubTask subTask = new SubTask("", "", StatusEnum.NEW, 1);

        manager.addSubTask(subTask);

        SubTask updatedTask = new SubTask("Поездка", "на поезде", StatusEnum.IN_PROGRESS, 2);
        updatedTask.setId(subTask.getId());

        manager.updateSubTask(updatedTask);
        subTask = manager.getSubTask(subTask.getId());

        assertEquals("Поездка", subTask.getName());
        assertEquals("на поезде", subTask.getDescription());
        assertEquals(StatusEnum.IN_PROGRESS, subTask.getStatus());
    }
}