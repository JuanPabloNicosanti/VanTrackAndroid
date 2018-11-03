package mainFunctionality.viewsModels;

import android.arch.lifecycle.ViewModel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import solid.collections.SolidList;
import utn.proy2k18.vantrack.connector.HttpConnector;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.models.PassengerReservation;
import utn.proy2k18.vantrack.utils.JacksonSerializer;
import utn.proy2k18.vantrack.utils.QueryBuilder;

import static com.google.android.gms.common.util.ArrayUtils.newArrayList;


public class TripsViewModel extends ViewModel {

    private QueryBuilder queryBuilder = new QueryBuilder();
    private static final ObjectMapper objectMapper = JacksonSerializer.getObjectMapper();
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

    public List<PassengerReservation> getTripPassengers(int trip_id) {
        if (tripPassengers.get(trip_id) == null) {
            HashMap<String, String> data = new HashMap<>();
            data.put("service_id", String.valueOf(trip_id));
            String url = queryBuilder.getTripReservationsUrl(data);

            final HttpConnector HTTP_CONNECTOR = HttpConnector.getInstance();
            try {
                String result = HTTP_CONNECTOR.execute(url, HTTP_GET).get();
                TypeReference listType = new TypeReference<List<PassengerReservation>>(){};
                List<PassengerReservation> passengers = objectMapper.readValue(result, listType);
                tripPassengers.put(trip_id, passengers);
            } catch (ExecutionException ee){
                ee.printStackTrace();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return tripPassengers.get(trip_id);
    }

    public List<Trip> getDriverTrips(String username) {
        if (driverTrips == null) {
            HashMap<String, String> data = new HashMap<>();
            data.put("username", username);
            String url = queryBuilder.getDriverTripsUrl(data);
            driverTrips = getDriverTripsFromBack(url);
        }
        return driverTrips;
    }

    private List<Trip> getDriverTripsFromBack(String url){
        final HttpConnector HTTP_CONNECTOR = HttpConnector.getInstance();
        try {
            String result = HTTP_CONNECTOR.execute(url, HTTP_GET).get();
            TypeReference listType = new TypeReference<List<Trip>>(){};
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

    public Trip getDriverTripAtPosition(int position) {
        return driverTrips.get(position);
    }

    public void confirmTripPassengers(Trip trip, List<PassengerReservation> passengers) {
        final HttpConnector HTTP_CONNECTOR = HttpConnector.getInstance();
        String url = queryBuilder.getTripConfirmPassengersUrl(String.valueOf(trip.get_id()));
        try {
            String userIds = getJsonUserIds(passengers);
            String result = HTTP_CONNECTOR.execute(url, HTTP_PATCH, userIds).get();
        } catch (ExecutionException ee){
            ee.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private String getJsonUserIds(List<PassengerReservation> passengers) throws JsonProcessingException {
        List<Integer> userIds = new ArrayList<>();
        for (PassengerReservation passenger: passengers) {
            Integer userId = passenger.getPassenger().getId();
            userIds.add(userId);
        }
        return objectMapper.writeValueAsString(userIds);
    }

    public void modifyTrip(String username, Trip tripModified) {
        HashMap<String, String> data = new HashMap<>();
        data.put("username", username);
        String url = queryBuilder.getDriverTripsUrl(data);

        final HttpConnector HTTP_CONNECTOR = HttpConnector.getInstance();
        try {
            String payload = objectMapper.writeValueAsString(tripModified);
            String result = HTTP_CONNECTOR.execute(url, HTTP_PUT, payload).get();
            TypeReference listType = new TypeReference<List<Trip>>(){};
            List<Trip> tripsUpdated = objectMapper.readValue(result, listType);
            if (tripsUpdated != null) {
                driverTrips = tripsUpdated;
            }
        } catch (ExecutionException ee){
            ee.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public Trip getNextTrip() {
        if (driverTrips != null) {
            Trip trip;
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
            trip = SolidList.stream(driverTrips).filter(d -> !d.isConfirmed()).first().or(new Trip());
            return trip;
        }
        return new Trip();
    }
}
