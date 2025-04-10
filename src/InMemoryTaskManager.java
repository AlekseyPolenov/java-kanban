import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private long generateId = 0;
    private HashMap<Long, Task> taskMap;
    private HashMap<Long, EpicTask> taskEpicMap;
    private HashMap<Long, SubTask> subTaskMap;
    private HistoryManager historyManager;

    public InMemoryTaskManager() {
        taskMap = new HashMap<>();
        taskEpicMap = new HashMap<>();
        subTaskMap = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }


    public long getGenerateId(){
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
            for (SubTask subTask : epic.subTasks) {
                subTaskMap.remove(subTask.getId());
            }
        }
    }

    @Override
    public void addSubTask(SubTask subTask) {
        subTask.setId(getGenerateId());
        subTaskMap.put(subTask.getId(), subTask);
        EpicTask epic = taskEpicMap.get(subTask.getEpicId());
        if (epic != null) {
            epic.subTasks.add(subTask);
            epic.updateStatus();
        }

    }

    @Override
    public void deleteSubTask() {

        for (EpicTask epic : taskEpicMap.values()) {
            for (SubTask subTask : epic.subTasks) {
                subTaskMap.remove(subTask.getId());
            }
            epic.subTasks.clear();
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
                epic.subTasks.remove(removedSubTask);
                epic.updateStatus();
            }
        }
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public ArrayList<EpicTask> getEpicTasks() {
        return new  ArrayList<>(taskEpicMap.values());
    }

    @Override
    public ArrayList<SubTask> getSubTasks() {
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
    public ArrayList<SubTask> getSubTasks(long idEpicTask) {
        EpicTask epicTask = taskEpicMap.get(idEpicTask);
        return epicTask.subTasks;
    }

    @Override
    public List<Task> getTasksHistory() {
        return historyManager.getTasksHistory();
    }

}
