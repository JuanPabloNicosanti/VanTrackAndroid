package utn.proy2k18.vantrack.reservations;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.joda.time.DateTime;

import utn.proy2k18.vantrack.mainFunctionality.search.Trip;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Reservation implements Parcelable {

    @JsonProperty("booking_date")
    private DateTime reservationDate;
    @JsonProperty("booked_trip")
    private Trip bookedTrip;
    @JsonProperty("booking_id")
    private int _id;
    @JsonProperty("paid")
    private boolean isPaidValue;
    @JsonProperty("travelers_qty")
    private int travelersQty;

    public Reservation() {}

    private Reservation(Parcel in) {
        readFromParcel(in);
    }

    public int getTravelersQty() {
        return travelersQty;
    }

    public boolean isPaid() {
        return isPaidValue;
    }

    public void payBooking() {
        isPaidValue = true;
    }

    public int get_id() {
        return _id;
    }

    public Trip getBookedTrip() { return bookedTrip; }

    public DateTime getReservationDate() {
        return reservationDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        // We just need to write each field into the parcel. When we read from parcel, they
        // will come back in the same order
        dest.writeInt(get_id());
        dest.writeParcelable(getBookedTrip(), flags);
        dest.writeByte((byte) (isPaidValue ? 1 : 0));
        dest.writeString(reservationDate.toString());
    }

    private void readFromParcel(Parcel in) {

        // We just need to read back each field in the order that it was written to the parcel
        _id = in.readInt();
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