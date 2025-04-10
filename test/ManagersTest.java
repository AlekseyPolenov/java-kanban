import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void testGetDefualt(){
        TaskManager taskManager = Managers.getDefault();
        assertEquals(true, taskManager instanceof InMemoryTaskManager );
    }

    @Test
    void testGetDefaultHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertEquals(true, historyManager instanceof InMemoryHistoryManager);
    }
}