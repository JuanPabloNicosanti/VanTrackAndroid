package utn.proy2k18.vantrack.Test;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import utn.proy2k18.vantrack.facade.TripFacade;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;

public class TestTrips {

    private TripFacade tripFacade;
    private List<Trip> testTrips;


    public TestTrips() {
        this.tripFacade = new TripFacade();
        this.testTrips = tripFacade.getTrips();
    }

    public List<Trip> getTestTrips() {
        return testTrips.subList(0, 5);
    }

    public List<Trip> getTestTripsToConfirm() {
        List<Trip> trips = testTrips.subList(0, testTrips.size());
        return getEarliestTrips(trips);
    }

    private List<Trip> getEarliestTrips (List<Trip> trips) {

        Collections.sort(trips, new Comparator<Trip>() {
        public int compare(Trip t1, Trip t2) {
            return t1.getDate().compareTo(t2.getDate());
            }
        });
        return trips.subList(0,5);
    }
}
