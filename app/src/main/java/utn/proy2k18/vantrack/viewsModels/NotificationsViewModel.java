package utn.proy2k18.vantrack.viewsModels;

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
    private static final String HTTP_PUT = "PUT";

    public static final Integer ORIGIN_MODIF_ID = 1;
    public static final Integer DESTINATION_MODIF_ID = 2;
    public static final Integer STOPS_MODIF_ID = 3;
    public static final Integer TIME_MODIF_ID = 4;
    public static final Integer DATE_MODIF_ID = 5;
    public static final Integer CANCELATION_ID = 6;

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

    public void createNotification(Notification notification) {
        final HttpConnector HTTP_CONNECTOR = HttpConnector.getInstance();
        String url = queryBuilder.getNotificationsUrl();

        try {
            String payload = objectMapper.writeValueAsString(notification);
            String result = HTTP_CONNECTOR.execute(url, HTTP_PUT, payload).get();
        } catch (ExecutionException ee) {
            ee.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void addNotification(Notification notification) {
        this.notifications.add(notification);
    }

    public String getMessage(Integer messageId) {
        return getNotificationDescription().get(messageId);
    }

    private HashMap<Integer, String> getNotificationDescription() {
        if (notificationDescriptions == null) {
            notificationDescriptions = new HashMap<>();
            notificationDescriptions.put(ORIGIN_MODIF_ID, "Cambio en el origen del viaje.");
            notificationDescriptions.put(DESTINATION_MODIF_ID, "Cambio en el destino del viaje.");
            notificationDescriptions.put(STOPS_MODIF_ID, "Cambio en el recorrido del viaje.");
            notificationDescriptions.put(TIME_MODIF_ID, "Cambio en el horario del viaje.");
            notificationDescriptions.put(DATE_MODIF_ID, "Cambio en la fecha del viaje.");
            notificationDescriptions.put(CANCELATION_ID, "Cancelación del viaje.");
        }
        return notificationDescriptions;
    }
}
