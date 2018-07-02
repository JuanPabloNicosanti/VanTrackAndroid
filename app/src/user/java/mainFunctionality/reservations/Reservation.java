package mainFunctionality.reservations;

import java.text.SimpleDateFormat;
import java.util.Date;

import utn.proy2k18.vantrack.search.Trip;


public class Reservation {

    private Date reservationDate;
    private Trip bookedTrip;

    public Reservation(Date date, Trip trip) {
        this.bookedTrip = trip;
        this.reservationDate = date;
    }

    public Trip getBookedTrip() { return bookedTrip; }

    public String getTripCompanyName() {
        return bookedTrip.getCompanyName();
    }

    public Date getReservationDate() {
        return reservationDate;
    }

    public String getTripOrigin() {
        return bookedTrip.getOrigin();
    }

    public String getTripDestination() {
        return bookedTrip.getDestination();
    }

    public String getTripFormattedDate() { return bookedTrip.getFormattedDate(); }

    public String getTripFormattedHour() { return bookedTrip.getFormattedTime(); }

    public String getReservationFormattedDate(){
        SimpleDateFormat ft = new SimpleDateFormat("E MM-dd hh:mm a");
        return ft.format(reservationDate);
    }


}