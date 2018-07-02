package mainFunctionality.reservations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import utn.proy2k18.vantrack.Test.TestTrips;


public class TestReservations {

    private List<Reservation> testReservations;
    private TestTrips testTrips;

    public TestReservations() {
        this.testTrips = new TestTrips();
        this.testReservations = createTestReservations();
    }

    public List<Reservation> getTestReservations() { return testReservations; }

    private List<Reservation> createTestReservations() {
        final List<Reservation> reservations = new ArrayList<Reservation>();

        reservations.add(new Reservation(new Date(), testTrips.getTrip("La Medalla","Terminal Obelisco","Echeverria del Lago")));
        reservations.add(new Reservation(new Date(), testTrips.getTrip("La Medalla","Echeverria del Lago","Terminal Obelisco")));
        reservations.add(new Reservation(new Date(), testTrips.getTrip("La Medalla","Campos de Echeverria","Terminal Obelisco")));
        reservations.add(new Reservation(new Date(), testTrips.getTrip("La Medalla","Terminal Obelisco", "Campos de Echeverria")));
        reservations.add(new Reservation(new Date(), testTrips.getTrip("Adrogue Bus", "Malibu", "Terminal Obelisco")));
        reservations.add(new Reservation(new Date(), testTrips.getTrip("Adrogue Bus", "Terminal Obelisco", "Malibu")));

        return reservations;
    }
}
