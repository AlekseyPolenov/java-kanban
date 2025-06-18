package managers;

import models.Task;

import java.util.List;

public interface HistoryManager {

    List<Task> getTasksHistory();

    void addTaskHistory(Task task);

    void removeTaskHistory(Long id);
}
