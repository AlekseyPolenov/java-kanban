package managers;

import models.EpicTask;
import models.SubTask;
import models.Task;

import java.util.List;

public interface TaskManager {

    void addTask(Task task);

    void addEpicTask(EpicTask epicTask);

    void addSubTask(SubTask subTask);

    void deleteTask();

    void deleteEpicTask();

    void deleteSubTask();

    void removeTask(long deleteTask);

    void removeEpicTask(long deleteEpicTask);

    void removeSubTask(long deleteSubTask);

    void updateTask(Task updateTask);

    void updateEpicTask(EpicTask updateEpicTask);

    void updateSubTask(SubTask updateSubTask);

    Task getTask(long idTask);

    EpicTask getEpicTask(long idTask);

    SubTask getSubTask(long idTask);

    List<Task> getTasks();

    List<EpicTask> getEpicTasks();

    List<SubTask> getSubTasks();

    List<SubTask> getSubTasks(long idEpicTask);

    List<Task> getTasksHistory();

    List<Task> getPrioritizedTasks();
}
