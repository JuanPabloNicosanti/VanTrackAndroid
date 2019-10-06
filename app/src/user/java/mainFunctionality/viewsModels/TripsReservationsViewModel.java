package mainFunctionality.viewsModels;

import com.mercadopago.android.px.model.Payment;

import org.joda.time.DateTime;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import mainFunctionality.reservations.CancellationCause;
import solid.collections.SolidList;
import utn.proy2k18.vantrack.exceptions.BackendConnectionException;
import utn.proy2k18.vantrack.exceptions.BackendException;
import utn.proy2k18.vantrack.exceptions.FailedToPayReservationException;
import utn.proy2k18.vantrack.exceptions.FailedToCreateReservationException;
import utn.proy2k18.vantrack.exceptions.FailedToDeleteReservationException;
import utn.proy2k18.vantrack.exceptions.FailedToGetReservationsException;
import utn.proy2k18.vantrack.exceptions.FailedToModifyReservationException;
import utn.proy2k18.vantrack.exceptions.FailedToRateTripException;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.mainFunctionality.search.TripStop;
import utn.proy2k18.vantrack.models.Rating;
import utn.proy2k18.vantrack.models.Reservation;
import utn.proy2k18.vantrack.utils.BackendMapper;
import utn.proy2k18.vantrack.utils.QueryBuilder;


public class TripsReservationsViewModel {
    private QueryBuilder queryBuilder = new QueryBuilder();
    private static final BackendMapper backendMapper = BackendMapper.getInstance();
    private static final String HTTP_GET = "GET";
    private static final String HTTP_PATCH = "PATCH";
    private static final String HTTP_POST = "POST";
    private static final String HTTP_DELETE = "DELETE";
    private static final String HTTP_PUT = "PUT";
    private HashMap<String, List<Reservation>> reservations = new HashMap<>();
    private static TripsReservationsViewModel viewModel;
    private List<CancellationCause> cancellationCauses;


    public TripsReservationsViewModel() { }

    public static TripsReservationsViewModel getInstance() {
        if (viewModel == null) {
            viewModel = new TripsReservationsViewModel();
        }
        return viewModel;
    }

    private void fetchReservations(String username) {
        HashMap<String, String> data = new HashMap<>();
        data.put("username", username);
        String url = queryBuilder.getUrl(QueryBuilder.RESERVATIONS, data);
        try {
            List<Reservation> userReservations = backendMapper.mapListFromBackend(Reservation.class,
                    url, HTTP_GET);
            reservations.put(username, userReservations);
        } catch (BackendConnectionException e) {
            throw new FailedToGetReservationsException();
        }  catch (BackendException be) {
            throw new FailedToGetReservationsException(be.getMessage());
        }
    }

    public List<Reservation> getReservations(String username) {
        if (!reservations.containsKey(username)) {
            this.fetchReservations(username);
        }
        sortReservationsByTripDate(username);
        return reservations.get(username);
    }

    private void sortReservationsByTripDate(String username) {
        Collections.sort(reservations.get(username), new Comparator<Reservation>() {
            @Override
            public int compare(Reservation reservation1, Reservation reservation2) {
                DateTime firstTripDT = getTripDateTimeFromReservation(reservation1);
                DateTime secondTripDT = getTripDateTimeFromReservation(reservation2);
                return firstTripDT.compareTo(secondTripDT);
            }
        });
    }

    private DateTime getTripDateTimeFromReservation(Reservation reservation) {
        return reservation.getBookedTrip().getDate().toDateTime(
                reservation.getBookedTrip().getTime());
    }

    public void modifyReservationHopOnStop(Reservation reservation, TripStop newStop) {
        HashMap<String, Integer> payload = new HashMap<>();
        payload.put("reservation_id", reservation.get_id());
        payload.put("stop_id", newStop.getId());

        String url = queryBuilder.getUrl(QueryBuilder.MODIFY_RESERVATION);
        try {
            String jsonResults = backendMapper.mapObjectForBackend(payload);
            String result = backendMapper.getFromBackend(url, HTTP_PATCH, jsonResults);
            if (result.equals("200")) {
                reservation.setHopOnStop(newStop);
            } else {
                throw new FailedToModifyReservationException();
            }
        } catch (BackendConnectionException e) {
            throw new FailedToModifyReservationException();
        }  catch (BackendException be) {
            throw new FailedToModifyReservationException(be.getMessage());
        }
    }

    public Reservation getReservationByTripId(int tripId, String username) {
        try {
            this.fetchReservations(username);
        } catch (FailedToGetReservationsException fgre) {
            // do nothing
        }
        for (Reservation res: reservations.get(username)) {
            if (res.getBookedTrip().get_id() == tripId) {
                return res;
            }
        }
        return null;
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

    public void deleteReservation(Reservation reservation, String username, CancellationCause cc) {
        HashMap<String, String> data = new HashMap<>();
        data.put("username", username);
        data.put("reservation_id", String.valueOf(reservation.get_id()));
        data.put("cancellation_cause_id", String.valueOf(cc.getId()));

        String url = queryBuilder.getUrl(QueryBuilder.DELETE_RESERVATION, data);
        String result;
        try {
            result = backendMapper.getFromBackend(url, HTTP_DELETE);
        } catch (BackendConnectionException e) {
            throw new FailedToDeleteReservationException();
        }  catch (BackendException be) {
            throw new FailedToDeleteReservationException(be.getMessage());
        }
        if (result.equals("200")) {
            reservations.get(username).remove(reservation);
        } else {
            throw new FailedToDeleteReservationException();
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

        String url = queryBuilder.getUrl(QueryBuilder.ADD_RESERVATION, payload);
        Reservation newReservation;
        try {
            newReservation = backendMapper.mapObjectFromBackend(Reservation.class, url, HTTP_PUT);
        } catch (BackendConnectionException e) {
            throw new FailedToCreateReservationException();
        }  catch (BackendException be) {
            throw new FailedToCreateReservationException(be.getMessage());
        }
        reservations.get(username).add(newReservation);
    }

    public String createCheckoutPreference(HashMap<String, Object> preferenceMap) {
        String url = queryBuilder.getUrl(QueryBuilder.CREATE_MP_PREFERENCE);
        try {
            String preferencePayload = backendMapper.mapObjectForBackend(preferenceMap);
            return backendMapper.mapObjectFromBackend(String.class, url, HTTP_POST,
                    preferencePayload);
        } catch (BackendConnectionException e) {
            throw new FailedToPayReservationException();
        }  catch (BackendException be) {
            throw new FailedToPayReservationException(be.getMessage());
        }
    }

    public void payReservation(Reservation reservation, Payment payment) {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("booking_id", String.valueOf(reservation.get_id()));
        payload.put("payment_id", String.valueOf(payment.getId()));

        String url = queryBuilder.getUrl(QueryBuilder.PAY_RESERVATION);
        try {
            String jsonPayload = backendMapper.mapObjectForBackend(payload);
            String result = backendMapper.getFromBackend(url, HTTP_PATCH, jsonPayload);
            if (result.equals("200")) {
                reservation.setPaymentId(payment.getId());
            }
        } catch (BackendConnectionException e) {
            throw new FailedToPayReservationException();
        }  catch (BackendException be) {
            throw new FailedToPayReservationException(be.getMessage());
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
        String url = queryBuilder.getUrl(QueryBuilder.ADD_RATING, String.valueOf(reservationId));
        try {
            String body = backendMapper.mapObjectForBackend(rating);
            // TODO: backend is not handling exceptions
            String result = backendMapper.getFromBackend(url, HTTP_POST, body);
        } catch (BackendConnectionException e) {
            throw new FailedToRateTripException();
        }  catch (BackendException be) {
            throw new FailedToRateTripException(be.getMessage());
        }
        Reservation reservation = getReservationById(reservationId, username);
        reservations.get(username).remove(reservation);
    }

    public List<CancellationCause> getCancellationCauses() {
        if (this.cancellationCauses == null) {
            String url = queryBuilder.getUrl(QueryBuilder.CANCELLATION_CAUSE);
            try {
                cancellationCauses = backendMapper.mapListFromBackend(CancellationCause.class, url,
                        HTTP_GET);
            } catch (BackendConnectionException e) {
                throw new FailedToDeleteReservationException();
            }  catch (BackendException be) {
                throw new FailedToDeleteReservationException(be.getMessage());
            }
        }
        return cancellationCauses;
    }

    public CancellationCause getCancellationCauseByDescription(String desc) {
        return SolidList.stream(this.cancellationCauses).filter(cc -> cc.getDescription()
                .equalsIgnoreCase(desc)).first().or(new CancellationCause());
    }
}
