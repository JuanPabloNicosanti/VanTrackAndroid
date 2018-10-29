package utn.proy2k18.vantrack.viewModels;

import android.arch.lifecycle.ViewModel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import utn.proy2k18.vantrack.connector.HttpConnector;
import utn.proy2k18.vantrack.models.Notification;
import utn.proy2k18.vantrack.utils.JacksonSerializer;
import utn.proy2k18.vantrack.utils.QueryBuilder;


public class NotificationsViewModel extends ViewModel {

    private QueryBuilder queryBuilder = new QueryBuilder();
    private static final ObjectMapper objectMapper = JacksonSerializer.getObjectMapper();
    private static final String HTTP_GET = "GET";
    public static final Integer CANCELATION_ID = 6;

    private List<Notification> notifications;

    public List<Notification> getNotifications(String username) {
        if (this.notifications == null) {
            final HttpConnector HTTP_CONNECTOR = HttpConnector.getInstance();
            HashMap<String, String> params = new HashMap<>();
            params.put("username", username);
            String url = queryBuilder.getNotificationsUrl(params);

            try{
                String result = HTTP_CONNECTOR.execute(url, HTTP_GET).get();
                TypeReference listNotifType = new TypeReference<List<Notification>>(){};
                this.notifications = objectMapper.readValue(result, listNotifType);
            } catch (ExecutionException ee){
                ee.printStackTrace();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return this.notifications;
    }

    private void addNotification(Notification notification) {
        this.notifications.add(notification);
    }

    public Notification getNotificationAtPosition(int position) {
        return notifications.get(position);
    }
}
