import java.util.LinkedHashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private LinkedHashMap<Long, Task> tasksHistory;

    public InMemoryHistoryManager() {
        tasksHistory = new LinkedHashMap<>();
    }

    @Override
    public List<Task> getTasksHistory() {
        return tasksHistory.values().stream().toList();
    }

    @Override
    public void addTaskHistory(Task task) {
        if (task == null) return;

        long taskId = task.getId();

        if (tasksHistory.containsKey(taskId)) {
            removeTaskHistory(taskId);
        }

        tasksHistory.put(taskId, task);
    }

    @Override
    public void removeTaskHistory(Long id) {
        tasksHistory.remove(id);
    }
}
