package mainFunctionality.viewsModels;

import android.arch.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import utn.proy2k18.vantrack.connector.HttpConnector;
import utn.proy2k18.vantrack.facade.GsonMapper;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.reservations.Reservation;
import utn.proy2k18.vantrack.utils.QueryBuilder;

import static com.google.android.gms.common.util.ArrayUtils.newArrayList;


public class TripsReservationsViewModel extends ViewModel {
    private QueryBuilder queryBuilder = new QueryBuilder();
    private static final Gson GSON = GsonMapper.getInstance();
    private static final String HTTP_GET = "GET";
    private List<Reservation> reservations = null;

    public void addReservationForTrip(Trip trip) {
        reservations.add(new Reservation(new DateTime(), trip));
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
            Type listType = new TypeToken<ArrayList<Reservation>>(){}.getType();
            return GSON.fromJson(result, listType);
        }catch (ExecutionException ee){
            ee.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        return newArrayList();
    }

    public Reservation getReservationAtPosition(int position) {
        return reservations.get(position);
    }

    public void deleteReservationAtPosition(int position) {
        reservations.remove(position);
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
