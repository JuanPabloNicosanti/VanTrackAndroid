package mainFunctionality.driverTrips;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import utn.proy2k18.vantrack.mainFunctionality.Company;

public class TestTrips {

    private List<Trip> testTrips;
    private Random randomGenerator;

    public TestTrips() {
        this.testTrips = createTestTrips();
        this.randomGenerator = new Random();
    }

    public List<Trip> getTestTrips() { return testTrips; }

    public Trip getTrip(String companyName, String origin, String destination) {
        Trip someTrip = getAnyTrip();

        for (Trip trip: testTrips) {
            if (trip.getCompanyName().equals(companyName) && trip.getOrigin().equals(origin) &&
                    trip.getDestination().equals(destination)) {
                someTrip = trip;
                break;
            }
        }

        return someTrip;
    }

    public Trip getAnyTrip() {
        final int index = randomGenerator.nextInt(testTrips.size());
        return testTrips.get(index);
    }

    public List<Trip> createTestTrips() {
        Company laMedalla = new Company("La Medalla", 30611234);
        Company mercoBus = new Company("Merco Bus", 30611235);

        final List<Trip> trips = new ArrayList<Trip>();
        trips.add(new Trip(laMedalla, new Date(new Date().getTime() + 2 * 3600*1000), "Echeverria del Lago", "Terminal Obelisco", 120));
        trips.add(new Trip(laMedalla, new Date(), "Terminal Obelisco", "Echeverria del Lago", 130));
        trips.add(new Trip(mercoBus, new Date(new Date().getTime() + 2 * 3600*1000), "Echeverria del Lago", "Terminal Obelisco", 135));

        return trips;
    }

}
