package utn.proy2k18.vantrack.mainFunctionality.search;

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
        Company adrogueBus = new Company("Adrogue Bus", 30611236);
        laMedalla.addCalification(5.0);
        laMedalla.addCalification(3.0);
        laMedalla.addCalification(4.5);
        laMedalla.addCalification(4.0);
        mercoBus.addCalification(2.0);
        mercoBus.addCalification(3.0);
        mercoBus.addCalification(4.5);
        adrogueBus.addCalification(1.5);
        adrogueBus.addCalification(2.5);
        adrogueBus.addCalification(2.5);

        final List<Trip> trips = new ArrayList<Trip>();
        trips.add(new Trip(laMedalla, new Date(new Date().getTime() + 2 * 3600*1000), "Echeverria del Lago", "Terminal Obelisco", 120));
        trips.add(new Trip(laMedalla, new Date(), "Terminal Obelisco", "Echeverria del Lago", 130));
        trips.add(new Trip(mercoBus, new Date(new Date().getTime() + 2 * 3600*1000), "Echeverria del Lago", "Terminal Obelisco", 135));
        trips.add(new Trip(mercoBus, new Date(), "Terminal Obelisco", "Echeverria del Lago", 145));
        trips.add(new Trip(laMedalla, new Date(), "Echeverria del Lago", "Terminal Obelisco", 150));
        trips.add(new Trip(laMedalla, new Date(), "Terminal Obelisco", "Echeverria del Lago", 160));
        trips.add(new Trip(mercoBus, new Date(), "Echeverria del Lago", "Terminal Obelisco", 165));
        trips.add(new Trip(mercoBus, new Date(), "Terminal Obelisco", "Echeverria del Lago", 175));
        trips.add(new Trip(mercoBus, new Date(new Date().getTime() - 4 * 3600*1000), "Echeverria del Lago", "Terminal Obelisco", 185));
        trips.add(new Trip(mercoBus, new Date(), "Terminal Obelisco", "Echeverria del Lago", 195));
        trips.add(new Trip(laMedalla, new Date(new Date().getTime() - 2 * 3600*1000), "Echeverria del Lago", "Terminal Obelisco", 110));
        trips.add(new Trip(laMedalla, new Date(), "Terminal Obelisco", "Echeverria del Lago", 100));
        trips.add(new Trip(mercoBus, new Date(new Date().getTime() - 2 * 3600*1000), "Echeverria del Lago", "Terminal Obelisco", 105));
        trips.add(new Trip(mercoBus, new Date(), "Terminal Obelisco", "Echeverria del Lago", 190));
        trips.add(new Trip(laMedalla, new Date(), "Campos de Echeverria", "Terminal Obelisco", 110));
        trips.add(new Trip(laMedalla, new Date(), "Terminal Obelisco", "Campos de Echeverria", 110));
        trips.add(new Trip(adrogueBus, new Date(), "Malibu", "Terminal Obelisco", 120));
        trips.add(new Trip(adrogueBus, new Date(), "Terminal Obelisco", "Malibu", 120));
        trips.add(new Trip(mercoBus, new Date(), "El Centauro", "Terminal Obelisco", 130));
        trips.add(new Trip(mercoBus, new Date(new Date().getTime() + 8 * 3600*1000), "Terminal Obelisco", "El Centauro", 130));
        trips.add(new Trip(adrogueBus, new Date(), "Saint Thomas", "Terminal Obelisco", 120));
        trips.add(new Trip(adrogueBus, new Date(), "Terminal Obelisco", "Saint Thomas", 120));

        return trips;
    }

}
