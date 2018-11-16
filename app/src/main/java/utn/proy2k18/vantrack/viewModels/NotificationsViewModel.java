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
    public static final Integer CANCELATION_ID = 6;

    private HashMap<String, List<Notification>> userNotifications = new HashMap<>();

    public List<Notification> getNotifications(String username) {
        if (!userNotifications.containsKey(username)) {
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
}
