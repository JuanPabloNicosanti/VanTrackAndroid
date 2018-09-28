package mainFunctionality.viewsModels;

import com.fasterxml.jackson.core.JsonProcessingException;
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


public class TripsReservationsViewModel {
    private QueryBuilder queryBuilder = new QueryBuilder();
    private static final ObjectMapper objectMapper = JacksonSerializer.getObjectMapper();
    private static final String HTTP_GET = "GET";
    private static final String HTTP_PATCH = "PATCH";
    private List<Reservation> reservations = null;
    private static TripsReservationsViewModel viewModel;

//    public void addReservationForTrip(Trip trip) {
//        reservations.add(new Reservation(trip, new DateTime()));
//    }
    public TripsReservationsViewModel() { }

    public static TripsReservationsViewModel getInstance() {
        if (viewModel == null) {
            viewModel = new TripsReservationsViewModel();
        }
        return viewModel;
    }

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

    public String modifyReservationHopOnStop(int reservationId, int stopId) {
        HashMap<String, Integer> payload = new HashMap<>();
        payload.put("reservation_id", reservationId);
        payload.put("stop_id", stopId);

        final HttpConnector HTTP_CONNECTOR = new HttpConnector();
        try{
            String jsonResults = objectMapper.writeValueAsString(payload);
            String url = queryBuilder.getModifyReservationUrl();
            return HTTP_CONNECTOR.execute(url, HTTP_PATCH, jsonResults).get();
        } catch (ExecutionException ee){
            ee.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "400";
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
