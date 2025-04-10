import java.util.ArrayList;
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

    Task getTask(long idTask);

    EpicTask getEpicTask(long idTask);

    SubTask getSubTask(long idTask);

    ArrayList<Task> getTasks();

    ArrayList<EpicTask> getEpicTasks();

    ArrayList<SubTask> getSubTasks();

    ArrayList<SubTask> getSubTasks(long idEpicTask);

    List<Task> getTasksHistory();
}
