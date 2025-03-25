import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static long generateId = 0;
    private HashMap<Long, Task> taskMap;
    private HashMap<Long, EpicTask> taskEpicMap;
    private HashMap<Long, SubTask> subTaskMap;

    public TaskManager() {
        taskMap = new HashMap<>();
        taskEpicMap = new HashMap<>();
        subTaskMap = new HashMap<>();
    }

    public static long getGenerateId(){
        generateId++;
        return generateId;
    }

    public void addTask(Task task) {
        taskMap.put(task.getId(), task);
    }

    public void deleteTask() {
        taskMap.clear();
    }

    public Task removeTask(long deleteTask) {
       return taskMap.remove(deleteTask);
    }

    public void addEpicTask(EpicTask epicTask) {
        taskEpicMap.put(epicTask.getId(), epicTask);
    }

    public void deleteEpicTask() {
        taskEpicMap.clear();
    }

    public EpicTask removeEpicTask(long deleteEpicTask) {
        return taskEpicMap.remove(deleteEpicTask);
    }

    public void addSubTask(SubTask subTask) {
        subTaskMap.put(subTask.getId(), subTask);
        subTask.parentEpicTask.subTasks.add(subTask);
    }

    public void deleteSubTask() {
        subTaskMap.clear();
    }

    public SubTask removeSubTask(long deleteSubTask) {
        return subTaskMap.remove(deleteSubTask);
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

}
