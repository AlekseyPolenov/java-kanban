import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private long generateId = 0;
    private HashMap<Long, Task> taskMap;
    private HashMap<Long, EpicTask> taskEpicMap;
    private HashMap<Long, SubTask> subTaskMap;

    public TaskManager() {
        taskMap = new HashMap<>();
        taskEpicMap = new HashMap<>();
        subTaskMap = new HashMap<>();
    }

    public long getGenerateId(){
        generateId++;
        return generateId;
    }

    public void addTask(Task task) {
        task.setId(getGenerateId());
        taskMap.put(task.getId(), task);
    }

    public void deleteTask() {
        taskMap.clear();
    }

    public void removeTask(long deleteTask) {
       taskMap.remove(deleteTask);
    }

    public void addEpicTask(EpicTask epicTask) {
        epicTask.setId(getGenerateId());
        taskEpicMap.put(epicTask.getId(), epicTask);
    }

    public void deleteEpicTask() {
        taskEpicMap.clear();
        subTaskMap.clear();
    }

    public void removeEpicTask(long deleteEpicTask) {
        EpicTask epic = taskEpicMap.remove(deleteEpicTask);
        if (epic != null) {
            for (SubTask subTask : epic.subTasks) {
                subTaskMap.remove(subTask.getId());
            }
        }
    }

    public void addSubTask(SubTask subTask) {
        subTask.setId(getGenerateId());
        subTaskMap.put(subTask.getId(), subTask);
        EpicTask epic = taskEpicMap.get(subTask.getEpicId());
        epic.updateStatus();
        if (epic != null) {
            epic.subTasks.add(subTask);
        }
    }

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

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(taskMap.values());
    }

    public ArrayList<EpicTask> getEpicTasks() {
        return new  ArrayList<>(taskEpicMap.values());
    }

    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTaskMap.values());
    }

    public Task getTask(long idTask) {
        return taskMap.get(idTask);
    }

    public EpicTask getEpicTask(long idTask) {
        return taskEpicMap.get(idTask);
    }

    public SubTask getSubTask(long idTask) {
        return subTaskMap.get(idTask);
    }

    public ArrayList<SubTask> getSubTasks(long idEpicTask) {
        EpicTask epicTask = taskEpicMap.get(idEpicTask);
        return epicTask.subTasks;
    }

}
