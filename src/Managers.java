import java.nio.file.Paths;

public class Managers {
    public static TaskManager getDefault() {
        return new FileBackedTaskManager(Paths.get("backup.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
