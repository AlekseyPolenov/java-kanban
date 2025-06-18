package managers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Managers {
    public static TaskManager getDefault() {
        Path path = Paths.get("backup.csv");
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            return new FileBackedTaskManager(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create backup file", e);
        }
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getInMemoryTaskManager() {
        return new InMemoryTaskManager();
    }
}