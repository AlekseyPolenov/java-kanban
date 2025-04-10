import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        Task task = new Task("", "", StatusEnum.NEW);
        Task task1 = new Task("", "", StatusEnum.NEW);
        manager.addTask(task);
        manager.addTask(task1);
        assertEquals(2, manager.getTasks().size());
        manager.removeTask(task.getId());
        assertEquals(1, manager.getTasks().size());
        Task expectedTask = manager.getTasks().getFirst();
        assertEquals(expectedTask, task1);
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
        EpicTask epicTask = new EpicTask("", "", StatusEnum.NEW);
        EpicTask epicTask1 = new EpicTask("", "", StatusEnum.NEW);
        manager.addEpicTask(epicTask);
        manager.addEpicTask(epicTask1);
        assertEquals(2, manager.getEpicTasks().size());
        manager.removeEpicTask(epicTask.getId());
        assertEquals(1, manager.getEpicTasks().size());
        EpicTask expectedTask = manager.getEpicTasks().getFirst();
        assertEquals(expectedTask, epicTask1);
    }

    @Test
    void addSubTask() {
        EpicTask epicTask = new EpicTask("", "", StatusEnum.NEW);
        manager.addEpicTask(epicTask);
        SubTask subTask = new SubTask("", "", StatusEnum.DONE, epicTask.getId());
        subTask.setId(0);
        manager.addSubTask(subTask);
        assertEquals(StatusEnum.DONE, epicTask.getStatus());
        assertEquals(1, epicTask.subTasks.size());
        assertEquals(subTask, epicTask.subTasks.getFirst());
    }

    @Test
    void deleteSubTask() {
        EpicTask epicTask = new EpicTask("", "", StatusEnum.NEW);
        manager.addEpicTask(epicTask);
        SubTask subTask = new SubTask("", "", StatusEnum.DONE, epicTask.getId());
        subTask.setId(0);
        manager.addSubTask(subTask);
        assertEquals(1, epicTask.subTasks.size());
        manager.deleteSubTask();
        assertEquals(0, manager.getSubTasks().size());
        assertEquals(0, epicTask.subTasks.size());
        assertEquals(StatusEnum.NEW, epicTask.getStatus());
    }

    @Test
    void removeSubTask() {
        EpicTask epicTask = new EpicTask("", "", StatusEnum.NEW);
        manager.addEpicTask(epicTask);

        SubTask subTask = new SubTask("", "", StatusEnum.DONE, epicTask.getId());
        SubTask subTask1 = new SubTask("", "", StatusEnum.IN_PROGRESS, epicTask.getId());

        manager.addSubTask(subTask);
        manager.addSubTask(subTask1);

        assertEquals(StatusEnum.IN_PROGRESS, epicTask.getStatus());
        assertEquals(2, epicTask.subTasks.size());
        assertEquals(2, manager.getSubTasks().size());

        manager.removeSubTask(subTask1.getId());

        assertEquals(1, manager.getSubTasks().size());
        SubTask expectedTask = manager.getSubTasks().getFirst();
        assertEquals(expectedTask, subTask);

        assertEquals(1, epicTask.subTasks.size());
        SubTask expectedTaskFromEpic = epicTask.subTasks.getFirst();
        assertEquals(expectedTaskFromEpic, subTask);
        assertEquals(StatusEnum.DONE, epicTask.getStatus());
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
        assertEquals(epicTask.subTasks, actualTask.subTasks);
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
}