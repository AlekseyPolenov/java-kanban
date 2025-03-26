import java.util.ArrayList;

public class EpicTask extends Task {
    ArrayList<SubTask> subTasks;

    public EpicTask(String name, String description, StatusEnum status) {
        super(name, description, status);
        subTasks = new ArrayList<>();
    }

    public void setStatus(StatusEnum status) {
    }

    public void updateStatus() {
        StatusEnum updatedStatus = StatusEnum.IN_PROGRESS;
        if (subTasks.isEmpty()) updatedStatus = StatusEnum.NEW;

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

}
