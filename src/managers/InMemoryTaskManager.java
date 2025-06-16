package managers;

import models.EpicTask;
import models.SubTask;
import models.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private long generateId = 0;
    protected Map<Long, Task> taskMap;
    protected Map<Long, EpicTask> taskEpicMap;
    protected Map<Long, SubTask> subTaskMap;
    private HistoryManager historyManager;
    private final Set<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(Task::getId)
    );

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
        if (hasTimeOverlap(task)) {
            throw new IllegalStateException("Задача пересекается по времени с существующей");
        }
        task.setId(getGenerateId());
        taskMap.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void deleteTask() {
        taskMap.clear();
    }

    @Override
    public void removeTask(long deleteTask) {
        Task removedTask = taskMap.remove(deleteTask);
        if (removedTask != null) {
            prioritizedTasks.remove(removedTask);
        }
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
            prioritizedTasks.remove(epic);
            for (SubTask subTask : epic.getSubTasks()) {
                subTaskMap.remove(subTask.getId());
                prioritizedTasks.remove(subTask);
            }
        }
        historyManager.removeTaskHistory(deleteEpicTask);
    }

    @Override
    public void addSubTask(SubTask subTask) {
        if (hasTimeOverlap(subTask)) {
            throw new IllegalStateException("Подзадача пересекается по времени с существующей задачей");
        }

        subTask.setId(getGenerateId());
        subTaskMap.put(subTask.getId(), subTask);
        EpicTask epic = taskEpicMap.get(subTask.getEpicId());

        if (epic != null) {
            epic.getSubTasks().add(subTask);
            epic.updateStatus();
            epic.updateTime();
            prioritizedTasks.add(subTask); // Добавляем подзадачу в prioritizedTasks
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
            prioritizedTasks.remove(removedSubTask);

            EpicTask epic = taskEpicMap.get(removedSubTask.getEpicId());
            if (epic != null) {
                epic.getSubTasks().remove(removedSubTask);
                epic.updateStatus();
                epic.updateTime();
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

    private boolean isTimeChanged(Task task, Task updateTask) {
       return !Objects.equals(task.getStartTime(), updateTask.getStartTime()) ||
                !Objects.equals(task.getDuration(), updateTask.getDuration());
    }

    @Override
    public void updateTask(Task updateTask) {
        Task task = taskMap.get(updateTask.getId());
        if (task == null) return;

        if (isTimeChanged(task, updateTask)) {
            if (hasTimeOverlap(updateTask)) {
                prioritizedTasks.add(task);
                throw new IllegalStateException("Обновленная задача пересекается по времени с существующей");
            }
        }

        prioritizedTasks.remove(task);
        task.setName(updateTask.getName());
        task.setDescription(updateTask.getDescription());
        task.setStatus(updateTask.getStatus());
        task.setStartTime(updateTask.getStartTime());
        task.setDuration(updateTask.getDuration());

        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void updateEpicTask(EpicTask updateEpicTask) {
        EpicTask epicTask = taskEpicMap.get(updateEpicTask.getId());
        if (epicTask == null) return;

        prioritizedTasks.remove(epicTask);
        epicTask.setName(updateEpicTask.getName());
        epicTask.setDescription(updateEpicTask.getDescription());
        if (epicTask.getStartTime() != null) {
            prioritizedTasks.add(epicTask);
        }
        epicTask.updateStatus();
    }

    @Override
    public void updateSubTask(SubTask updateSubTask) {
        SubTask subTask = subTaskMap.get(updateSubTask.getId());
        if (subTask == null) return;

        if (isTimeChanged(subTask, updateSubTask)) {
            prioritizedTasks.remove(subTask);

            if (hasTimeOverlap(updateSubTask)) {
                prioritizedTasks.add(subTask);
                throw new IllegalStateException("Обновленная подзадача пересекается по времени с существующей");
            }
        }

        subTask.setName(updateSubTask.getName());
        subTask.setDescription(updateSubTask.getDescription());
        subTask.setStatus(updateSubTask.getStatus());
        subTask.setStartTime(updateSubTask.getStartTime());
        subTask.setDuration(updateSubTask.getDuration());

        if (isTimeChanged(subTask, updateSubTask) && subTask.getStartTime() != null) {
            prioritizedTasks.add(subTask);
        }

        EpicTask epicTask = getEpicTask(subTask.getEpicId());
        if (epicTask != null) {
            epicTask.updateStatus();
            epicTask.updateTime();
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean isTimeOverlap(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null)
            return false;

        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return !(end1.isBefore(start2) || end2.isBefore(start1));
    }

    public boolean hasTimeOverlap(Task newTask) {
        if (newTask.getStartTime() == null) return false;

        return prioritizedTasks.stream()
                .anyMatch(existingTask -> isTimeOverlap(newTask, existingTask));
    }
}
