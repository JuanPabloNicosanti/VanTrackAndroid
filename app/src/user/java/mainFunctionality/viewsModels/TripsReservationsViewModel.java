package mainFunctionality.viewsModels;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mercadopago.model.Payment;
import com.mercadopago.preferences.CheckoutPreference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import utn.proy2k18.vantrack.connector.HttpConnector;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.models.Rating;
import utn.proy2k18.vantrack.models.Reservation;
import utn.proy2k18.vantrack.utils.JacksonSerializer;
import utn.proy2k18.vantrack.utils.QueryBuilder;

import static com.google.android.gms.common.util.ArrayUtils.newArrayList;


public class TripsReservationsViewModel {
    private QueryBuilder queryBuilder = new QueryBuilder();
    private static final ObjectMapper objectMapper = JacksonSerializer.getObjectMapper();
    private static final String HTTP_GET = "GET";
    private static final String HTTP_PATCH = "PATCH";
    private static final String HTTP_POST = "POST";
    private static final String HTTP_DELETE = "DELETE";
    private static final String HTTP_PUT = "PUT";
    private HashMap<String, List<Reservation>> reservations = new HashMap<>();
    private static TripsReservationsViewModel viewModel;


    public TripsReservationsViewModel() { }

    public static TripsReservationsViewModel getInstance() {
        if (viewModel == null) {
            viewModel = new TripsReservationsViewModel();
        }
        return viewModel;
    }

    public List<Reservation> getReservations(String username) {
        if (!reservations.containsKey(username)) {
            HashMap<String, String> data = new HashMap<>();
            data.put("username", username);
            String url = queryBuilder.getReservationsQuery(data);
            List<Reservation> userReservations = getReservationsFromBack(url);
            reservations.put(username, userReservations);
        }
        return reservations.get(username);
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
        String url = queryBuilder.getModifyReservationUrl();
        try{
            String jsonResults = objectMapper.writeValueAsString(payload);
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

    public Reservation getReservationByTripId(int tripId, String username) {
        Reservation reservation = null;
        for (Reservation res: reservations.get(username)) {
            if (res.getBookedTrip().get_id() == tripId) {
                reservation = res;
                break;
            }
        }
        return reservation;
    }

    public Reservation getReservationById(int resId, String username) {
        Reservation reservation = null;
        for (Reservation res: reservations.get(username)) {
            if (res.get_id() == resId) {
                reservation = res;
                break;
            }
        }
        return reservation;
    }

    public void deleteReservation(Reservation reservation, String username) {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("username", username);
        payload.put("reservation_id", String.valueOf(reservation.get_id()));

        final HttpConnector HTTP_CONNECTOR = new HttpConnector();
        String url = queryBuilder.getDeleteReservationUrl(payload);
        try{
            String result = HTTP_CONNECTOR.execute(url, HTTP_DELETE).get();
            if (result.equals("200")) {
                reservations.get(username).remove(reservation);
            }
        } catch (ExecutionException ee){
            ee.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    public void createReservationForTrip(Trip trip, int travelersQty, int stopOriginId,
                                         String username, boolean isWaitList) {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("service_id", String.valueOf(trip.get_id()));
        payload.put("travelers_quantity", String.valueOf(travelersQty));
        payload.put("stop_origin", String.valueOf(stopOriginId));
        payload.put("username", username);
        payload.put("pending_reservation", String.valueOf(isWaitList));

        final HttpConnector HTTP_CONNECTOR = new HttpConnector();
        String url = queryBuilder.getCreateReservationUrl(payload);

        try{
            String result = HTTP_CONNECTOR.execute(url, HTTP_PUT).get();
            // TODO: add exception handling when failing to create reservation
            TypeReference resType = new TypeReference<Reservation>(){};
            Reservation newReservation = objectMapper.readValue(result, resType);
            reservations.get(username).add(newReservation);
        } catch (ExecutionException ee){
            ee.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CheckoutPreference createCheckoutPreference(HashMap<String, Object> preferenceMap) {
        final HttpConnector HTTP_CONNECTOR = new HttpConnector();
        String url = queryBuilder.getCreateMPPreferenceUrl();
        try {
            String preferencePayload = objectMapper.writeValueAsString(preferenceMap);
            String result = HTTP_CONNECTOR.execute(url, HTTP_POST, preferencePayload).get();
            TypeReference resType = new TypeReference<String>(){};
            if (result != null) {
                String preferenceString = objectMapper.readValue(result, resType);
                return new Gson().fromJson(preferenceString, CheckoutPreference.class);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void payReservation(Reservation reservation, Payment payment) {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("booking_id", String.valueOf(reservation.get_id()));
        payload.put("payment_id", String.valueOf(payment.getId()));

        final HttpConnector HTTP_CONNECTOR = new HttpConnector();
        String url = queryBuilder.getPayReservationUrl();
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            String result = HTTP_CONNECTOR.execute(url, HTTP_PATCH, jsonPayload).get();
            if (result.equals("200")) {
                reservation.payBooking();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public Reservation getReservationAtPosition(int position, String username) {
        return reservations.get(username).get(position);
    }

    public boolean isTripBooked(Trip trip, String username) {
        boolean isBooked = false;
        for (Reservation reservation: reservations.get(username)) {
            if (reservation.getBookedTrip().get_id() == trip.get_id()) {
                isBooked = true;
                break;
            }
        }

        return isBooked;
    }

    public void addRating(int reservationId, Rating rating, String username) {
        final HttpConnector HTTP_CONNECTOR = new HttpConnector();
        try {
            String body = objectMapper.writeValueAsString(rating);
            String url = queryBuilder.getCreateRatingUri(String.valueOf(reservationId));
            String result = HTTP_CONNECTOR.execute(url, HTTP_POST, body).get();
            Reservation reservation = getReservationById(reservationId, username);
            reservations.get(username).remove(reservation);
        } catch (ExecutionException ee) {
            ee.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
