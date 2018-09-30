package utn.proy2k18.vantrack.viewsModels;

import android.arch.lifecycle.ViewModel;

import java.util.List;

import utn.proy2k18.vantrack.models.Notification;
import utn.proy2k18.vantrack.Test.TestNotifications;

public class NotificationsViewModel extends ViewModel {

    private final List<Notification> notifications = (new TestNotifications()).getTestNotifications();

    public List<Notification> getNotifications() { return this.notifications; }

    public void addNotification(Notification notification) {
        this.notifications.add(notification);
    }
}
