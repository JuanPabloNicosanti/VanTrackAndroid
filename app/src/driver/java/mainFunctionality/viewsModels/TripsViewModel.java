package mainFunctionality.viewsModels;

import android.arch.lifecycle.ViewModel;

import java.util.List;

import mainFunctionality.driverTrips.TestTrips;
import mainFunctionality.driverTrips.Trip;


public class TripsViewModel extends ViewModel {
    private final List<Trip> totalTrips = (new TestTrips()).getTestTrips();


    public List<Trip> getTotalTrips() {
        return totalTrips;
    }

    public Trip getTripAtPosition(int position) {
        return totalTrips.get(position);
    }

    public void deleteTripAtPosition(int position) {
        totalTrips.remove(position);
    }


}
