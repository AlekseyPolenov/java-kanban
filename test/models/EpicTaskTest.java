package models;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTaskTest {

    @Test
    void testUpdateStatus() {
        EpicTask epicTask = new EpicTask("", "", StatusEnum.DONE);
        epicTask.updateStatus();
        assertEquals(StatusEnum.DONE, epicTask.getStatus());

        SubTask subTask = new SubTask("", "", StatusEnum.DONE,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1), epicTask.getId());
        epicTask.getSubTasks().add(subTask);
        epicTask.updateStatus();
        assertEquals(StatusEnum.DONE, epicTask.getStatus());

        subTask.setStatus(StatusEnum.NEW);
        epicTask.updateStatus();
        assertEquals(StatusEnum.NEW, epicTask.getStatus());

        subTask.setStatus(StatusEnum.IN_PROGRESS);
        epicTask.updateStatus();
        assertEquals(StatusEnum.IN_PROGRESS, epicTask.getStatus());
    }

    @Test
    void getStartTime() {
        EpicTask epic = new EpicTask("Epic", "Desc", StatusEnum.NEW);
        SubTask sub = new SubTask("Sub", "Desc", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1), 1);
        epic.getSubTasks().add(sub);

        assertEquals(sub.getStartTime(), epic.getStartTime());
    }

    @Test
    void getDuration() {
        EpicTask epic = new EpicTask("Epic", "Desc", StatusEnum.NEW);
        SubTask sub1 = new SubTask("Sub1", "Desc", StatusEnum.NEW,
                null, Duration.ofHours(2), 1);
        SubTask sub2 = new SubTask("Sub2", "Desc", StatusEnum.NEW,
                null, Duration.ofHours(3), 1);
        epic.getSubTasks().add(sub1);
        epic.getSubTasks().add(sub2);

        assertEquals(Duration.ofHours(5), epic.getDuration());
    }

    @Test
    void getEndTime() {
        EpicTask epic = new EpicTask("Epic", "Desc", StatusEnum.NEW);
        SubTask sub = new SubTask("Sub", "Desc", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1), 1);
        epic.getSubTasks().add(sub);

        assertEquals(sub.getEndTime(), epic.getEndTime());
    }

    @Test
    void updateTime() {
        EpicTask epic = new EpicTask("Epic", "Desc", StatusEnum.NEW);
        SubTask sub1 = new SubTask("Sub1", "Desc", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1), 1);
        SubTask sub2 = new SubTask("Sub2", "Desc", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 11, 0), Duration.ofHours(2), 1);

        epic.getSubTasks().add(sub1);
        epic.getSubTasks().add(sub2);

        epic.updateTime();

        assertEquals(sub1.getStartTime(), epic.getStartTime());
        assertEquals(Duration.ofHours(3), epic.getDuration());
        assertEquals(sub2.getEndTime(), epic.getEndTime());
    }

    @Test
    void epicStatusAllNew() {
        EpicTask epic = new EpicTask("Epic", "Desc", StatusEnum.NEW);
        SubTask sub1 = new SubTask("Sub1", "Desc", StatusEnum.NEW, null, null, 1);
        SubTask sub2 = new SubTask("Sub2", "Desc", StatusEnum.NEW, null, null, 1);

        epic.getSubTasks().add(sub1);
        epic.getSubTasks().add(sub2);
        epic.updateStatus();

        assertEquals(StatusEnum.NEW, epic.getStatus());
    }

    @Test
    void epicStatusAllDone() {
        EpicTask epic = new EpicTask("Epic", "Desc", StatusEnum.NEW);
        SubTask sub1 = new SubTask("Sub1", "Desc", StatusEnum.DONE, null, null, 1);
        SubTask sub2 = new SubTask("Sub2", "Desc", StatusEnum.DONE, null, null, 1);

        epic.getSubTasks().add(sub1);
        epic.getSubTasks().add(sub2);
        epic.updateStatus();

        assertEquals(StatusEnum.DONE, epic.getStatus());
    }

    @Test
    void epicStatusMixed() {
        EpicTask epic = new EpicTask("Epic", "Desc", StatusEnum.NEW);
        SubTask sub1 = new SubTask("Sub1", "Desc", StatusEnum.NEW, null, null, 1);
        SubTask sub2 = new SubTask("Sub2", "Desc", StatusEnum.DONE, null, null, 1);

        epic.getSubTasks().add(sub1);
        epic.getSubTasks().add(sub2);
        epic.updateStatus();

        assertEquals(StatusEnum.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void epicStatusInProgress() {
        EpicTask epic = new EpicTask("Epic", "Desc", StatusEnum.NEW);
        SubTask sub1 = new SubTask("Sub1", "Desc", StatusEnum.IN_PROGRESS, null, null, 1);

        epic.getSubTasks().add(sub1);
        epic.updateStatus();

        assertEquals(StatusEnum.IN_PROGRESS, epic.getStatus());
    }
}