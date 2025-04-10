public class SubTask extends Task {
    private long epicId;

    public SubTask(String name, String description, StatusEnum status, long epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public long getEpicId() {
        return epicId;
    }
}
