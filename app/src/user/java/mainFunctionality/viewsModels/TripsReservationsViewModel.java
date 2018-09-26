package mainFunctionality.viewsModels;

import android.arch.lifecycle.ViewModel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import utn.proy2k18.vantrack.connector.HttpConnector;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.models.Reservation;
import utn.proy2k18.vantrack.utils.JacksonSerializer;
import utn.proy2k18.vantrack.utils.QueryBuilder;

import static com.google.android.gms.common.util.ArrayUtils.newArrayList;


public class TripsReservationsViewModel extends ViewModel {
    private QueryBuilder queryBuilder = new QueryBuilder();
    private static final ObjectMapper objectMapper = JacksonSerializer.getObjectMapper();
    private static final String HTTP_GET = "GET";
    private List<Reservation> reservations = null;

//    public void addReservationForTrip(Trip trip) {
//        reservations.add(new Reservation(trip, new DateTime()));
//    }

    public List<Reservation> getReservations(String username) {
        if (reservations == null) {
            HashMap<String, String> data = new HashMap<>();
            data.put("username", username);
            String url = queryBuilder.getReservationsQuery(data);
            reservations = getReservationsFromBack(url);
        }
        return reservations;
    }

    private List<Reservation> getReservationsFromBack(String url){
        final HttpConnector HTTP_CONNECTOR = HttpConnector.getInstance();
        try{
            String result = HTTP_CONNECTOR.execute(url, HTTP_GET).get();
            TypeReference listType = new TypeReference<List<Reservation>>(){};
            return objectMapper.readValue(result, listType);
        } catch (ExecutionException ee){
            ee.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return newArrayList();
    }

    public Reservation getReservationById(int res_id) {
        Reservation reservation = null;
        for (Reservation res: reservations) {
            if (res.get_id() == res_id) {
                reservation = res;
                break;
            }
        }
        return reservation;
    }

    public void deleteReservation(int res_id) {
        for (Reservation res: reservations) {
            if (res.get_id() == res_id) {
                reservations.remove(res);
                break;
            }
        }
    }

    public Reservation getReservationAtPosition(int position) {
        return reservations.get(position);
    }

    public boolean isTripBooked(Trip trip) {
        boolean isBooked = false;
        for (Reservation reservation: reservations) {
            if (reservation.getBookedTrip().equals(trip)) {
                isBooked = true;
                break;
            }
        }

        return isBooked;
    }
}
