import managers.InMemoryHistoryManager;
import managers.InMemoryTaskManager;
import managers.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManagersTest {

    @Test
    void getDefaultMakeObjectInMemoryTaskManager() {
        Assertions.assertInstanceOf(InMemoryTaskManager.class, Managers.getDefault(), "Объект неверного " +
                "класса");
    }

    @Test
    void getDefaultHistoryMakeObjectInMemoryHistoryManager() {
        Assertions.assertInstanceOf(InMemoryHistoryManager.class, Managers.getDefaultHistory(), "Объект " +
                "неверного класса");
    }
}