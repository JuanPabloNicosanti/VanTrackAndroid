package utn.proy2k18.vantrack.mainFunctionality.viewsModels;

import android.arch.lifecycle.ViewModel;

import java.util.Date;
import java.util.List;

import utn.proy2k18.vantrack.mainFunctionality.reservations.Reservation;
import utn.proy2k18.vantrack.mainFunctionality.reservations.TestReservations;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;

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
