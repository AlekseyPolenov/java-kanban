import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {

    private List<SubTask> subTasks;

    public EpicTask(String name, String description, StatusEnum status) {
        super(name, description, status);
        subTasks = new ArrayList<>();
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
