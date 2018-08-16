package utn.proy2k18.vantrack.Test;

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

    public List<Trip> getTestTrips() { return testTrips; }

    public List<Trip> getTestTrips(Integer n) { return testTrips.subList(0,n); }

    public List<Trip> getTestTripsToConfirm() { return testTrips.subList(testTrips.size()-5, testTrips.size()); }
}
