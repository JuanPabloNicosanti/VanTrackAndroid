package mainFunctionality.viewsModels;

import android.arch.lifecycle.ViewModel;

import java.util.List;

import utn.proy2k18.vantrack.Test.TestTrips;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;


public class TripsViewModel extends ViewModel {
    private final List<Trip> driverTrips = (new TestTrips()).getTestTrips(3);
    private final List<Trip> tripsToConfirm = (new TestTrips()).getTestTripsToConfirm();

    public void init(){ }

    public List<Trip> getDriverTrips() {
        return driverTrips;
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

    public void deleteTripAtPosition(int position) {
        driverTrips.remove(position);
    }

    public void addTripToDriverTrips(Trip trip) {
        driverTrips.add(trip);
        tripsToConfirm.remove(trip);
    }
}
