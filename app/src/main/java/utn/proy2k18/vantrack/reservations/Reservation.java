package utn.proy2k18.vantrack.reservations;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import org.joda.time.DateTime;

import java.util.UUID;

import utn.proy2k18.vantrack.mainFunctionality.search.Trip;


public class Reservation implements Parcelable {

    private DateTime reservationDate;
    private Trip bookedTrip;
    private UUID uuid;
    private String _id;
    private boolean isPaidValue;

    public Reservation(DateTime date, Trip trip) {
        this.uuid = UUID.randomUUID();
        this._id = uuid.toString();
        this.reservationDate = date;
        this.bookedTrip = trip;
        this.isPaidValue = false;
    }

    public Reservation(Parcel in) {
        readFromParcel(in);
    }

    public boolean isPaid() {
        return isPaidValue;
    }

    public void payBooking() {
        isPaidValue = true;
    }

    @Exclude
    public UUID getUuid() {
        return uuid;
    }

    public String get_id() {
        return bookedTrip.get_id();
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

    public float getPrice() { return bookedTrip.getPrice(); }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        // We just need to write each field into the parcel. When we read from parcel, they
        // will come back in the same order
        dest.writeString(get_id());
        dest.writeParcelable(getBookedTrip(), flags);
        dest.writeByte((byte) (isPaidValue ? 1 : 0));
        dest.writeString(reservationDate.toString());
    }

    private void readFromParcel(Parcel in) {

        // We just need to read back each field in the order that it was written to the parcel
        _id = in.readString();
        bookedTrip = in.readParcelable(Trip.class.getClassLoader());
        isPaidValue = in.readByte() != 0;
        reservationDate = DateTime.parse(in.readString());
    }

    //    This field is needed for Android to be able to create new objects, individually or as arrays.
    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator<Reservation>() {
                public Reservation createFromParcel(Parcel in) {
                    return new Reservation(in);
                }

                public Reservation[] newArray(int size) {
                    return new Reservation[size];
                }
            };
}