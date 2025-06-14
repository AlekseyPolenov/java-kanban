import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EpicTask extends Task {

    private List<SubTask> subTasks;

    public EpicTask(String name, String description, StatusEnum status) {
        super(name, description, status, null, null);
        subTasks = new ArrayList<>();
    }

    public void updateTime() {
        if (subTasks.isEmpty()) {
            setStartTime(null);
            setDuration(null);
            return;
        }

        LocalDateTime earliest = subTasks.stream()
                .map(SubTask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        Duration totalDuration = subTasks.stream()
                .map(SubTask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        setStartTime(earliest);
        setDuration(totalDuration);
    }


    @Override
    public LocalDateTime getStartTime() {
        if (subTasks.isEmpty()) return null;
        return subTasks.stream()
                .map(SubTask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public Duration getDuration() {
        if (subTasks.isEmpty()) return null;
        return subTasks.stream()
                .map(SubTask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
    }

    @Override
    public LocalDateTime getEndTime() {
        if (subTasks.isEmpty()) return null;
        return subTasks.stream()
                .map(SubTask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    public void updateStatus() {
        StatusEnum updatedStatus = StatusEnum.IN_PROGRESS;
        if (subTasks.isEmpty()) return;

        if (hasAllSubTasksStatus(StatusEnum.DONE)) updatedStatus =  StatusEnum.DONE;

        if (hasAllSubTasksStatus(StatusEnum.NEW)) updatedStatus = StatusEnum.NEW;
        super.setStatus(updatedStatus);
    }

    private boolean hasAllSubTasksStatus(StatusEnum statusEnum) {
        for (SubTask subTask : subTasks) {
            if (subTask.getStatus() != statusEnum) return false;
        }

        return true;
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

}
