import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private long generateId = 0;
    private Map<Long, Task> taskMap;
    private Map<Long, EpicTask> taskEpicMap;
    private Map<Long, SubTask> subTaskMap;
    private HistoryManager historyManager;

    public InMemoryTaskManager() {
        taskMap = new HashMap<>();
        taskEpicMap = new HashMap<>();
        subTaskMap = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }


    private long getGenerateId() {
        generateId++;
        return generateId;
    }

    @Override
    public void addTask(Task task) {
        task.setId(getGenerateId());
        taskMap.put(task.getId(), task);
    }

    @Override
    public void deleteTask() {
        taskMap.clear();
    }

    @Override
    public void removeTask(long deleteTask) {
       taskMap.remove(deleteTask);
       historyManager.removeTaskHistory(deleteTask);
    }

    @Override
    public void addEpicTask(EpicTask epicTask) {
        epicTask.setId(getGenerateId());
        taskEpicMap.put(epicTask.getId(), epicTask);
    }

    @Override
    public void deleteEpicTask() {
        taskEpicMap.clear();
        subTaskMap.clear();
    }

    @Override
    public void removeEpicTask(long deleteEpicTask) {
        EpicTask epic = taskEpicMap.remove(deleteEpicTask);
        if (epic != null) {
            for (SubTask subTask : epic.getSubTasks()) {
                subTaskMap.remove(subTask.getId());
            }
        }

        historyManager.removeTaskHistory(deleteEpicTask);
    }

    @Override
    public void addSubTask(SubTask subTask) {
        subTask.setId(getGenerateId());
        subTaskMap.put(subTask.getId(), subTask);
        EpicTask epic = taskEpicMap.get(subTask.getEpicId());
        if (epic != null) {
            epic.getSubTasks().add(subTask);
            epic.updateStatus();
        }

    }

    @Override
    public void deleteSubTask() {
        for (EpicTask epic : taskEpicMap.values()) {
            for (SubTask subTask : epic.getSubTasks()) {
                subTaskMap.remove(subTask.getId());
            }
            epic.getSubTasks().clear();
            epic.updateStatus();
        }
        subTaskMap.clear();
    }

    @Override
    public void removeSubTask(long deleteSubTask) {
        SubTask removedSubTask = subTaskMap.remove(deleteSubTask);
        if (removedSubTask != null) {
            EpicTask epic = taskEpicMap.get(removedSubTask.getEpicId());
            if (epic != null) {
                epic.getSubTasks().remove(removedSubTask);
                epic.updateStatus();
            }
        }

        historyManager.removeTaskHistory(deleteSubTask);
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public List<EpicTask> getEpicTasks() {
        return new  ArrayList<>(taskEpicMap.values());
    }

    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTaskMap.values());
    }

    @Override
    public Task getTask(long idTask) {
        Task task = taskMap.get(idTask);
        historyManager.addTaskHistory(task);
        return task;
    }

    @Override
    public EpicTask getEpicTask(long idTask) {
        EpicTask task = taskEpicMap.get(idTask);
        historyManager.addTaskHistory(task);
        return task;
    }

    @Override
    public SubTask getSubTask(long idTask) {
        SubTask task = subTaskMap.get(idTask);
        historyManager.addTaskHistory(task);
        return task;
    }

    @Override
    public List<SubTask> getSubTasks(long idEpicTask) {
        EpicTask epicTask = taskEpicMap.get(idEpicTask);
        return epicTask.getSubTasks();
    }

    @Override
    public List<Task> getTasksHistory() {
        return historyManager.getTasksHistory();
    }

    @Override
    public void updateTask(Task updateTask) {
        Task task  = taskMap.get(updateTask.getId());
        if (task == null) return;
        task.setName(updateTask.getName());
        task.setDescription(updateTask.getDescription());
        task.setStatus(updateTask.getStatus());
    }

    @Override
    public void updateEpicTask(EpicTask updateEpicTask) {
        EpicTask epicTask = taskEpicMap.get(updateEpicTask.getId());
        if (epicTask == null) return;
        epicTask.setName(updateEpicTask.getName());
        epicTask.setDescription(updateEpicTask.getDescription());
        epicTask.setStatus(updateEpicTask.getStatus());
        epicTask.updateStatus();
    }

    @Override
    public void updateSubTask(SubTask updateSubTask) {
        SubTask subTask = subTaskMap.get(updateSubTask.getId());
        if (subTask == null) return;
        subTask.setName(updateSubTask.getName());
        subTask.setDescription(updateSubTask.getDescription());
        subTask.setStatus(updateSubTask.getStatus());

        EpicTask epicTask = getEpicTask(subTask.getEpicId());
        if (epicTask == null) return;
        epicTask.updateStatus();
    }
}
