package utn.proy2k18.vantrack.viewModels;

import android.arch.lifecycle.ViewModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import utn.proy2k18.vantrack.models.Notification;
import utn.proy2k18.vantrack.utils.BackendMapper;
import utn.proy2k18.vantrack.utils.QueryBuilder;


public class NotificationsViewModel extends ViewModel {

    private static final QueryBuilder queryBuilder = new QueryBuilder();
    private static final BackendMapper backendMapper = BackendMapper.getInstance();
    private static final String HTTP_GET = "GET";
    private static final String HTTP_PATCH = "PATCH";
    public static final Integer CANCELATION_ID = 5;
    public static final Integer CONFIRMATION_ID = 6;
    private static NotificationsViewModel viewModel;

    private HashMap<String, List<Notification>> userNotifications = new HashMap<>();

    public static NotificationsViewModel getInstance() {
        if (viewModel == null) {
            viewModel = new NotificationsViewModel();
        }
        return viewModel;
    }

    public List<Notification> getNotifications(String username, Boolean forceFetching) {
        if (!userNotifications.containsKey(username) || forceFetching) {
            HashMap<String, String> params = new HashMap<>();
            params.put("username", username);
            String url = queryBuilder.getNotificationsUrl(params);
            userNotifications.put(username, backendMapper.mapListFromBackend(Notification.class,
                    url, HTTP_GET));
        }
        sortNotificationsByDate(username);
        return userNotifications.get(username);
    }

    private void sortNotificationsByDate(String username) {
        Collections.sort(userNotifications.get(username), new Comparator<Notification>() {
            @Override
            public int compare(Notification notification1, Notification notification2) {
                return notification2.getDateTime().compareTo(notification1.getDateTime());
            }
        });
    }

    public Notification getNotificationAtPosition(String username, int position) {
        return userNotifications.get(username).get(position);
    }

    public void readNotification(Notification notification) {
        HashMap<String, String> params = new HashMap<>();
        params.put("notification_id", String.valueOf(notification.getNotificationId()));
        String url = queryBuilder.readNotification(params);
        String result = backendMapper.getFromBackend(url, HTTP_PATCH);
        notification.setSeen(true);
    }
}
