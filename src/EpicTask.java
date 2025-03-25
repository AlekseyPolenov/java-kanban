import java.util.ArrayList;

public class EpicTask extends Task {
    ArrayList<SubTask> subTasks;

    public EpicTask(String name, String description, StatusEnum status) {
        super(name, description, status);
        subTasks = new ArrayList<>();
    }

    @Override
    public void setStatus(StatusEnum status) {
    }

    @Override
    public StatusEnum getStatus() {
        if (subTasks.isEmpty()) return StatusEnum.NEW;

        if (hasAllSubTasksStatus(StatusEnum.DONE)) return StatusEnum.DONE;

        if (hasAllSubTasksStatus(StatusEnum.NEW)) return StatusEnum.NEW;

        return StatusEnum.IN_PROGRESS;
    }

    private boolean hasAllSubTasksStatus(StatusEnum statusEnum) {
        for (SubTask subTask : subTasks) {
            if (subTask.getStatus() != statusEnum) return false;
        }

        return true;
    }

}
