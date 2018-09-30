package mainFunctionality.viewsModels;

import android.arch.lifecycle.ViewModel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import utn.proy2k18.vantrack.Test.TestTrips;
import utn.proy2k18.vantrack.connector.HttpConnector;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.utils.JacksonSerializer;
import utn.proy2k18.vantrack.utils.QueryBuilder;

import static com.google.android.gms.common.util.ArrayUtils.newArrayList;


public class TripsViewModel extends ViewModel {

    private QueryBuilder queryBuilder = new QueryBuilder();
    private static final ObjectMapper objectMapper = JacksonSerializer.getObjectMapper();
    private static final String HTTP_GET = "GET";
    private List<Trip> driverTrips;
    private final List<Trip> tripsToConfirm = (new TestTrips()).getTestTripsToConfirm();
    private static TripsViewModel viewModel;

    public TripsViewModel() {}

    public static TripsViewModel getInstance() {
        if (viewModel == null) {
            viewModel = new TripsViewModel();
        }
        return viewModel;
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
        try{
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

    public List<Trip> getTripsToConfirm() {
        return tripsToConfirm;
    }

    public Trip getDriverTripAtPosition(int position) {
        return driverTrips.get(position);
    }

    public Trip getTripToConfirmAtPosition(int position) {
        return tripsToConfirm.get(position);
    }

    public void deleteTrip(int tripId) {
        for (Iterator<Trip> iter = driverTrips.listIterator(); iter.hasNext(); ) {
            Trip tripToRemove = iter.next();
            if (tripToRemove.get_id() == tripId) {
                iter.remove();
                break;
            }
        }
    }

    public void addTripToDriverTrips(Trip trip) {
        driverTrips.add(trip);
        tripsToConfirm.remove(trip);
    }
}
