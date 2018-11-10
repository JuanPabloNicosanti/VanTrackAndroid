package mainFunctionality.viewsModels;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import solid.collections.SolidList;
import utn.proy2k18.vantrack.exceptions.BackendException;
import utn.proy2k18.vantrack.exceptions.NoPassengersException;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.models.PassengerReservation;
import utn.proy2k18.vantrack.utils.BackendMapper;
import utn.proy2k18.vantrack.utils.QueryBuilder;


public class TripsViewModel {

    private static final QueryBuilder queryBuilder = new QueryBuilder();
    private static final BackendMapper backendMapper = BackendMapper.getInstance();
    private static final String HTTP_GET = "GET";
    private static final String HTTP_PATCH = "PATCH";
    private static final String HTTP_PUT = "PUT";

    private List<Trip> driverTrips;
    private HashMap<Integer, List<PassengerReservation>> tripPassengers = new HashMap<>();
    private static TripsViewModel viewModel;


    public TripsViewModel() {}

    public static TripsViewModel getInstance() {
        if (viewModel == null) {
            viewModel = new TripsViewModel();
        }
        return viewModel;
    }

    public List<PassengerReservation> getTripPassengers(int tripId) {
        if (tripPassengers.get(tripId) == null) {
            HashMap<String, String> data = new HashMap<>();
            data.put("service_id", String.valueOf(tripId));
            String url = queryBuilder.getTripReservationsUrl(data);
            List<PassengerReservation> passengers = backendMapper.mapListFromBackend(
                    PassengerReservation.class, url, HTTP_GET);
            tripPassengers.put(tripId, passengers);
        }
        List<PassengerReservation> passengers = tripPassengers.get(tripId);
        if (passengers.size() == 0) {
            throw new NoPassengersException();
        }
        return passengers;
    }

    public List<Trip> getDriverTrips(String username) {
        if (driverTrips == null) {
            HashMap<String, String> data = new HashMap<>();
            data.put("username", username);
            String url = queryBuilder.getDriverTripsUrl(data);
            driverTrips = backendMapper.mapListFromBackend(Trip.class, url, HTTP_GET);
            sortTripsByTime();
        }
        return driverTrips;
    }

    public Trip getDriverTripAtPosition(int position) {
        return driverTrips.get(position);
    }

    public void confirmTripPassengers(Trip trip, List<PassengerReservation> passengers) throws
            JsonProcessingException {
        String url = queryBuilder.getTripConfirmPassengersUrl(String.valueOf(trip.get_id()));
        String userIds = backendMapper.mapObjectForBackend(getUserIds(passengers));
        String result = backendMapper.getFromBackend(url, HTTP_PATCH, userIds);
    }

    private List<Integer> getUserIds(List<PassengerReservation> passengers) {
        List<Integer> userIds = new ArrayList<>();
        for (PassengerReservation passenger: passengers) {
            Integer userId = passenger.getPassenger().getId();
            userIds.add(userId);
        }
        return userIds;
    }

    public void modifyTrip(String username, Trip tripModified) throws JsonProcessingException {
        HashMap<String, String> data = new HashMap<>();
        data.put("username", username);
        String url = queryBuilder.getDriverTripsUrl(data);
        String payload = backendMapper.mapObjectForBackend(tripModified);
        List<Trip> tripsUpdated = backendMapper.mapListFromBackend(Trip.class, url, HTTP_PUT, payload);
        if (tripsUpdated != null) {
            driverTrips = tripsUpdated;
            sortTripsByTime();
        }
    }

    // TODO: raise an exception if there is no next trip
    public Trip getNextTrip() {
        if (driverTrips != null) {
            return SolidList.stream(driverTrips).filter(d -> !d.isConfirmed()).first().or(new Trip());
        }
        return new Trip();
    }

    private void sortTripsByTime() {
        Collections.sort(driverTrips, new Comparator<Trip>() {
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

    public void endTrip(String tripId) {
        String url = queryBuilder.endTrip(tripId);
        String result = backendMapper.getFromBackend(url, HTTP_PATCH, tripId);
        if (result.equals("200")) {
            Trip trip = SolidList.stream(driverTrips).filter(d -> d.get_id() == Integer.parseInt(tripId))
                    .first().get();
            driverTrips.remove(trip);
        } else {
            throw new BackendException("Error al finalizar el viaje.");
        }
    }
}
