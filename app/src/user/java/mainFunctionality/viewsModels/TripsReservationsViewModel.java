package mainFunctionality.viewsModels;

import android.arch.lifecycle.ViewModel;

import java.util.Date;
import java.util.List;

import mainFunctionality.reservations.Reservation;
import mainFunctionality.reservations.TestReservations;
import mainFunctionality.search.Trip;




public class TripsReservationsViewModel extends ViewModel {
    private List<Reservation> reservations = (new TestReservations()).getTestReservations();

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    public void addReservationForTrip(Trip trip) {
        reservations.add(new Reservation(new Date(), trip));
    }

    public List<Reservation> getReservations() { return reservations; }

    public Reservation getReservationAtPosition(int position) {
        return reservations.get(position);
    }

    public void deleteReservationAtPosition(int position) {
        reservations.remove(position);
    }
}
