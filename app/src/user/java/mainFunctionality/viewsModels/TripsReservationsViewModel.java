package mainFunctionality.viewsModels;

import android.arch.lifecycle.ViewModel;

import org.joda.time.DateTime;

import java.util.List;

import utn.proy2k18.vantrack.Test.TestReservations;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.reservations.Reservation;


public class TripsReservationsViewModel extends ViewModel {
    private List<Reservation> reservations = (new TestReservations()).getTestReservations();

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    public void addReservationForTrip(Trip trip) {
        reservations.add(new Reservation(new DateTime(), trip));
    }

    public List<Reservation> getReservations() { return reservations; }

    public Reservation getReservationAtPosition(int position) {
        return reservations.get(position);
    }

    public void deleteReservationAtPosition(int position) {
        reservations.remove(position);
    }

    public boolean isTripBooked(Trip trip) {
        boolean isBooked = false;
        for (Reservation reservation: reservations) {
            if (reservation.getBookedTrip().equals(trip)) {
                isBooked = true;
                break;
            }
        }

        return isBooked;
    }
}
