package utn.proy2k18.vantrack.mainFunctionality.viewsModels;

import android.arch.lifecycle.ViewModel;

import java.util.List;

import utn.proy2k18.vantrack.mainFunctionality.reservations.Reservation;
import utn.proy2k18.vantrack.mainFunctionality.reservations.TestReservations;

public class TripsReservationsViewModel extends ViewModel {
    private List<Reservation> reservations = (new TestReservations()).getTestReservations();

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    public List<Reservation> getReservations() { return reservations; }
}
