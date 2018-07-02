package mainFunctionality.viewsModels;

import android.arch.lifecycle.ViewModel;

import java.util.List;

import mainFunctionality.notifications.Notification;
import mainFunctionality.notifications.TestNotifications;

public class NotificationsViewModel extends ViewModel {

    private final List<Notification> notifications = (new TestNotifications()).getTestNotifications();

    public List<Notification> getNotifications() { return this.notifications; }

    public void addNotification(Notification notification) {
        this.notifications.add(notification);
    }
}
