package mainFunctionality.viewsModels;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.mercadopago.model.Payment;
import com.mercadopago.preferences.CheckoutPreference;

import java.util.HashMap;
import java.util.List;

import utn.proy2k18.vantrack.exceptions.BackendConnectionException;
import utn.proy2k18.vantrack.exceptions.BackendException;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.mainFunctionality.search.TripStop;
import utn.proy2k18.vantrack.models.Rating;
import utn.proy2k18.vantrack.models.Reservation;
import utn.proy2k18.vantrack.utils.BackendMapper;
import utn.proy2k18.vantrack.utils.QueryBuilder;


public class TripsReservationsViewModel {
    private QueryBuilder queryBuilder = new QueryBuilder();
    private static final BackendMapper backendMapper = new BackendMapper();
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
            List<Reservation> userReservations = backendMapper.mapListFromBackend(Reservation.class,
                    url, HTTP_GET);
            reservations.put(username, userReservations);
        }
        return reservations.get(username);
    }

    public void modifyReservationHopOnStop(Reservation reservation, TripStop newStop)
            throws BackendException, JsonProcessingException {
        HashMap<String, Integer> payload = new HashMap<>();
        payload.put("reservation_id", reservation.get_id());
        payload.put("stop_id", newStop.getId());

        String url = queryBuilder.getModifyReservationUrl();
        String jsonResults = backendMapper.mapObjectForBackend(payload);
        String result = backendMapper.getFromBackend(url, HTTP_PUT, jsonResults);
        if (result.equals("200")) {
            reservation.setHopOnStop(newStop);
        } else {
            throw new BackendException("Error al realizar el cambio en la reserva");
        }
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

        String url = queryBuilder.getDeleteReservationUrl(payload);
        String result = backendMapper.getFromBackend(url, HTTP_DELETE);
        if (result.equals("200")) {
            reservations.get(username).remove(reservation);
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

        String url = queryBuilder.getCreateReservationUrl(payload);
        Object response = backendMapper.mapObjectFromBackend(Reservation.class, url, HTTP_PUT);
        try {
            Reservation newReservation = (Reservation) response;
            reservations.get(username).add(newReservation);
        } catch (ClassCastException e) {
            throw (BackendException) response;
        }
    }

    public CheckoutPreference createCheckoutPreference(HashMap<String, Object> preferenceMap)
            throws BackendConnectionException, JsonProcessingException {
        String url = queryBuilder.getCreateMPPreferenceUrl();
        String preferencePayload = backendMapper.mapObjectForBackend(preferenceMap);
        Object response = backendMapper.mapObjectFromBackend(String.class, url, HTTP_POST,
                preferencePayload);
        try {
            String strPreference = (String) response;
            return new Gson().fromJson(strPreference, CheckoutPreference.class);
        } catch (ClassCastException e) {
            throw (BackendException) response;
        }
    }

    public void payReservation(Reservation reservation, Payment payment) throws
            BackendConnectionException, JsonProcessingException {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("booking_id", String.valueOf(reservation.get_id()));
        payload.put("payment_id", String.valueOf(payment.getId()));

        String url = queryBuilder.getPayReservationUrl();
        String jsonPayload = backendMapper.mapObjectForBackend(payload);
        String result = backendMapper.getFromBackend(url, HTTP_PATCH, jsonPayload);
        if (result.equals("200")) {
            reservation.payBooking();
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

    public void addRating(int reservationId, Rating rating, String username) throws
            BackendConnectionException, JsonProcessingException {
        String url = queryBuilder.getCreateRatingUri(String.valueOf(reservationId));
        String body = backendMapper.mapObjectForBackend(rating);
        // TODO: handle exceptions somehow (backend is not handling them)
        String result = backendMapper.getFromBackend(url, HTTP_POST, body);
        Reservation reservation = getReservationById(reservationId, username);
        reservations.get(username).remove(reservation);
    }
}
