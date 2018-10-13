package utn.proy2k18.vantrack.viewsModels;

import android.arch.lifecycle.ViewModel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
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

    private HashMap<Integer, String> notificationDescriptions;

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
}
