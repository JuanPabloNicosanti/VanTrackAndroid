package mainFunctionality.viewsModels;

import org.joda.time.LocalTime;
import org.joda.time.Minutes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import solid.collections.SolidList;
import utn.proy2k18.vantrack.exceptions.BackendConnectionException;
import utn.proy2k18.vantrack.exceptions.BackendException;
import utn.proy2k18.vantrack.exceptions.FailedToConfirmTripPassengersException;
import utn.proy2k18.vantrack.exceptions.FailedToEndTripException;
import utn.proy2k18.vantrack.exceptions.FailedToGetDriverTripsException;
import utn.proy2k18.vantrack.exceptions.FailedToGetTripPassengersException;
import utn.proy2k18.vantrack.exceptions.FailedToModifyTripException;
import utn.proy2k18.vantrack.exceptions.NoPassengersException;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.mainFunctionality.search.TripStop;
import utn.proy2k18.vantrack.models.PassengerReservation;
import utn.proy2k18.vantrack.utils.BackendMapper;
import utn.proy2k18.vantrack.utils.QueryBuilder;


public class TripsViewModel {

    private static final QueryBuilder queryBuilder = new QueryBuilder();
    private static final BackendMapper backendMapper = BackendMapper.getInstance();
    private static final String HTTP_GET = "GET";
    private static final String HTTP_PATCH = "PATCH";
    private static final String HTTP_PUT = "PUT";

    private TreeMap<String, List<Trip>> tripsByDriver = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private static TripsViewModel viewModel;


    public TripsViewModel() {}

    public static TripsViewModel getInstance() {
        if (viewModel == null) {
            viewModel = new TripsViewModel();
        }
        return viewModel;
    }

    public List<PassengerReservation> getTripPassengers(int tripId) {
        List<PassengerReservation> passengers;
        HashMap<String, String> data = new HashMap<>();
        data.put("service_id", String.valueOf(tripId));
        String url = queryBuilder.getUrl(QueryBuilder.TRIP_RESERVATION, data);
        try {
            passengers = backendMapper.mapListFromBackend(PassengerReservation.class, url, HTTP_GET);
            if (passengers.size() == 0) {
                throw new NoPassengersException();
            }
        } catch (BackendException be) {
            throw new FailedToGetTripPassengersException(be.getMessage());
        } catch (BackendConnectionException e) {
            throw new FailedToGetTripPassengersException();
        }
        return passengers;
    }

    public List<Trip> getDriverTrips(String username) {
        String upperUsername = username.toUpperCase();
        if (!tripsByDriver.containsKey(upperUsername)) {
            HashMap<String, String> data = new HashMap<>();
            data.put("username", username);
            String url = queryBuilder.getUrl(QueryBuilder.DRIVER_TRIPS, data);
            try {
                tripsByDriver.put(upperUsername, backendMapper.mapListFromBackend(
                        Trip.class, url, HTTP_GET));
            } catch (BackendException be) {
                throw new FailedToGetDriverTripsException(be.getMessage());
            } catch (BackendConnectionException e) {
                throw new FailedToGetDriverTripsException();
            }
            sortTripsByTime(upperUsername);
        }
        return tripsByDriver.get(upperUsername);
    }

    public Trip getDriverTripAtPosition(String username, int position) {
        return this.getDriverTrips(username).get(position);
    }

    public void confirmTripPassengers(Trip trip, List<PassengerReservation> passengers) {
        String url = queryBuilder.getUrl(QueryBuilder.TRIP_CONFIRM_PASSENGERS,
                String.valueOf(trip.get_id()));
        try {
            String userIds = backendMapper.mapObjectForBackend(getUserIds(passengers));
            String result = backendMapper.getFromBackend(url, HTTP_PATCH, userIds);
        } catch (BackendException be) {
            throw new FailedToConfirmTripPassengersException(be.getMessage());
        } catch (BackendConnectionException e) {
            throw new FailedToConfirmTripPassengersException();
        }
    }

    private List<Integer> getUserIds(List<PassengerReservation> passengers) {
        List<Integer> userIds = new ArrayList<>();
        for (PassengerReservation passenger: passengers) {
            Integer userId = passenger.getPassenger().getId();
            userIds.add(userId);
        }
        return userIds;
    }

    public void modifyTrip(String username, Trip tripModified) {
        List<Trip> tripsUpdated;
        String upperUsername = username.toUpperCase();
        HashMap<String, String> data = new HashMap<>();
        data.put("username", username);
        String url = queryBuilder.getUrl(QueryBuilder.DRIVER_TRIPS, data);
        try {
            String payload = backendMapper.mapObjectForBackend(tripModified);
            tripsUpdated = backendMapper.mapListFromBackend(Trip.class, url, HTTP_PUT, payload);
        } catch (BackendException be) {
            throw new FailedToModifyTripException(be.getMessage());
        } catch (BackendConnectionException e) {
            throw new FailedToModifyTripException();
        }
        tripsByDriver.put(upperUsername, tripsUpdated);
        sortTripsByTime(upperUsername);
    }

    // TODO: raise an exception if there is no next trip
    public Trip getNextTrip(String username) {
        String upperUsername = username.toUpperCase();
        if (tripsByDriver.containsKey(upperUsername) && this.getDriverTrips(username) != null) {
            return SolidList.stream(this.getDriverTrips(username)).filter(d -> !d.isConfirmed())
                    .first().or(new Trip());
        }
        return new Trip();
    }

    private void sortTripsByTime(String username) {
        Collections.sort(this.getDriverTrips(username), new Comparator<Trip>() {
            public int compare(Trip o1, Trip o2) {
                int compareByDate = o1.getDate().compareTo(o2.getDate());
                // Compares by hour if driver has 2 trips the same day
                if (compareByDate == 0) {
                    return o1.getTime().compareTo(o2.getTime());
                }
                return compareByDate;
            }
        });
    }

    public void endTrip(String username, String tripId) {
        String url = queryBuilder.getUrl(QueryBuilder.END_TRIP, tripId);
        try {
            String result = backendMapper.getFromBackend(url, HTTP_PATCH, tripId);
            if (result.equals("200")) {
                Trip trip = SolidList.stream(this.getDriverTrips(username)).filter(d ->
                        d.get_id() == Integer.parseInt(tripId)).first().get();
                this.getDriverTrips(username).remove(trip);
            } else {
                throw new FailedToEndTripException();
            }
        } catch (BackendException be) {
            throw new FailedToEndTripException(be.getMessage());
        } catch (BackendConnectionException e) {
            throw new FailedToEndTripException();
        }
    }

    public void deleteStopFromTrip(Trip trip, String tripStopDesc) {
        TripStop tripStop = trip.getTripStopByDescription(tripStopDesc);
        trip.getStops().remove(tripStop);
        if (trip.getOrigin().equalsIgnoreCase(tripStop.getDescription())) {
            TripStop newOrigin = trip.getStops().get(0);
            trip.setOrigin(newOrigin.getDescription());
            trip.setTime(newOrigin.getHour());
        } else if (trip.getDestination().equalsIgnoreCase(tripStop.getDescription())) {
            TripStop newDestination = trip.getStops().get(trip.getStops().size()-1);
            trip.setDestination(newDestination.getDescription());
        }
    }

    public void modifyTripStopTime(Trip trip, String newStopDesc, LocalTime newTime) {
        Integer timeDiff = 0;
        for (TripStop tripStop: trip.getStops()) {
            if (tripStop.equals(trip.getTripStopByDescription(newStopDesc))) {
                timeDiff = Minutes.minutesBetween(tripStop.getHour(), newTime).getMinutes();
            }
            if (timeDiff != 0) {
                tripStop.setHour(tripStop.getHour().plusMinutes(timeDiff));
                if (trip.getOrigin().equalsIgnoreCase(tripStop.getDescription())) {
                    trip.setTime(tripStop.getHour());
                }
            }
        }
    }
}
