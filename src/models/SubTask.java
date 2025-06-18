package models;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private long epicId;

    public SubTask(String name, String description, StatusEnum status,
                   LocalDateTime startTime, Duration duration, long epicId) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public long getEpicId() {
        return epicId;
    }
}
