public class SubTask extends Task {
    EpicTask parentEpicTask;

    public SubTask(String name, String description, StatusEnum status, EpicTask parentEpicTask) {
        super(name, description, status);
        this.parentEpicTask = parentEpicTask;
    }

}
