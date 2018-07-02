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

        testNotifications.add(new Notification("Viaje modificado!",
                "Su viaje que va de Terminal Obelisco a Echeverria del Lago ha sido modificado."));
        testNotifications.add(new Notification("Viaje confirmado!",
                "Su viaje que va de Terminal Obelisco a Echeverria del Lago ha sido confirmado."));


        return testNotifications;
    }
}
