package mainFunctionality.reservations;

import java.util.List;

import utn.proy2k18.vantrack.facade.ReservationFacade;


public class TestReservations {

    private ReservationFacade reservationFacade;
    private List<Reservation> testReservations;

    public TestReservations() {
        this.reservationFacade = new ReservationFacade();
        this.testReservations = reservationFacade.getReservations();
    }

    public List<Reservation> getTestReservations() { return testReservations; }

}
