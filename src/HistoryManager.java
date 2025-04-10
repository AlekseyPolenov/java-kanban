import java.util.List;

public interface HistoryManager {

    List<Task> getTasksHistory();

    void addTaskHistory(Task task);
}
