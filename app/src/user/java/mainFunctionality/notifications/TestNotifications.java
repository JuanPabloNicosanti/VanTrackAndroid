package mainFunctionality.notifications;

import java.util.ArrayList;
import java.util.List;

public class TestNotifications {

    private List<Notification> testNotifications;

    public TestNotifications() {
        this.testNotifications = createTestNotifications();
    }

    public List<Notification> getTestNotifications() {
        return testNotifications;
    }

    private List<Notification> createTestNotifications() {
        final List<Notification> testNotifications = new ArrayList<>();

        testNotifications.add(new Notification("Notification 1", "Mensaje 1"));
        testNotifications.add(new Notification("Notification 2", "Mensaje 2"));
        testNotifications.add(new Notification("Notification 3", "Mensaje 3"));

        return testNotifications;
    }
}
