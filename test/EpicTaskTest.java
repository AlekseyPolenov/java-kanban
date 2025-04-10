import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EpicTaskTest {

    @Test
    void testUpdateStatus() {
        EpicTask epicTask = new EpicTask("", "", StatusEnum.DONE);
        epicTask.updateStatus();
        assertEquals(StatusEnum.NEW, epicTask.getStatus());

        SubTask subTask = new SubTask("", "", StatusEnum.DONE, epicTask.getId());
        epicTask.subTasks.add(subTask);
        epicTask.updateStatus();
        assertEquals(StatusEnum.DONE, epicTask.getStatus());

        subTask.setStatus(StatusEnum.NEW);
        epicTask.updateStatus();
        assertEquals(StatusEnum.NEW, epicTask.getStatus());

        subTask.setStatus(StatusEnum.IN_PROGRESS);
        epicTask.updateStatus();
        assertEquals(StatusEnum.IN_PROGRESS, epicTask.getStatus());
    }
}