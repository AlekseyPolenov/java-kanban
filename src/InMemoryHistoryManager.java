import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> tasksHistory;

    public InMemoryHistoryManager() {
        tasksHistory = new ArrayList<>();
    }

    @Override
    public List<Task> getTasksHistory() {
        return tasksHistory;
    }

    @Override
    public void addTaskHistory(Task task) {
        tasksHistory.add(task);
        if (tasksHistory.size() > 10) {
            tasksHistory.removeFirst();
        }
    }
}
