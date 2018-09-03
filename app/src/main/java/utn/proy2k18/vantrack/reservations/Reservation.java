package utn.proy2k18.vantrack.reservations;

import com.google.firebase.database.Exclude;

import org.joda.time.DateTime;

import java.util.UUID;

import utn.proy2k18.vantrack.mainFunctionality.search.Trip;


public class Reservation {

    private DateTime reservationDate;
    private Trip bookedTrip;
    private UUID uuid;
    private String _id;

    public Reservation(DateTime date, Trip trip) {
        this.uuid = UUID.randomUUID();
        this._id = uuid.toString();
        this.reservationDate = date;
        this.bookedTrip = trip;
    }

    @Exclude
    public UUID getUuid() {
        return uuid;
    }

    public String get_id() {
        return _id;
    }

    public Trip getBookedTrip() { return bookedTrip; }

    public String getTripCompanyName() {
        return bookedTrip.getCompanyName();
    }

    public DateTime getReservationDate() {
        return reservationDate;
    }

    public String getTripOrigin() {
        return bookedTrip.getOrigin();
    }

    public String getTripDestination() {
        return bookedTrip.getDestination();
    }

    public String getTripFormattedDate() { return bookedTrip.getCalendarDate(); }

    public String getTripStrTime() { return bookedTrip.getStrTime(); }

    public String getReservationFormattedDate(){
        return this.reservationDate.toLocalDate().toString();
    }


}