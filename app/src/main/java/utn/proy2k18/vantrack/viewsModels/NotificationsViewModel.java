package utn.proy2k18.vantrack.viewsModels;

import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import utn.proy2k18.vantrack.models.Notification;


public class NotificationsViewModel extends ViewModel {

    private HashMap<Integer, String> notificationDescriptions;
    private String username = "lucas.lopez@gmail.com";

    private final List<Notification> notifications = getTestNotifications();

    public List<Notification> getNotifications() { return this.notifications; }

    public void addNotification(Notification notification) {
        this.notifications.add(notification);
    }

    private HashMap<Integer, String> getNotificationDescriptions() {
        if (notificationDescriptions == null) {
            notificationDescriptions = new HashMap<>();
            notificationDescriptions.put(1, "Cambio en el origen del viaje");
            notificationDescriptions.put(2, "Cambio en el destino del viaje");
            notificationDescriptions.put(3, "Cambio en el recorrido del viaje");
            notificationDescriptions.put(4, "Cambio en el horario de salida del viaje");
            notificationDescriptions.put(5, "Cambio en la fecha de su viaje");
            notificationDescriptions.put(6, "Cancelación del viaje");
        }
        return notificationDescriptions;
    }

    public String getNotificationDescriptionMessageById(Integer notificationMessageId) {
        return getNotificationDescriptions().get(notificationMessageId);
    }

    private List<Notification> getTestNotifications() {
        final List<Notification> testNotifications = new ArrayList<>();

        testNotifications.add(new Notification(1, username, 1,
                getNotificationDescriptionMessageById(1)));
        testNotifications.add(new Notification(2, username, 2,
                getNotificationDescriptionMessageById(2)));

        return testNotifications;
    }
}
