package utn.proy2k18.vantrack.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PassengerReservation {

    @JsonProperty("reservation")
    private Reservation reservation;
    @JsonProperty("user")
    private Passenger passenger;

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }
}
