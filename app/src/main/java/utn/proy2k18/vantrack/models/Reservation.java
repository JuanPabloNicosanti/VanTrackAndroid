package utn.proy2k18.vantrack.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.joda.time.DateTime;

import java.math.BigDecimal;

import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.mainFunctionality.search.TripStop;


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
    @JsonProperty("stop")
    private TripStop hopOnStop;
    @JsonProperty("booking_price")
    private BigDecimal reservationPrice;
    @JsonProperty("pending_reservation")
    private boolean pendingReservation;

    public Reservation() {}

    private Reservation(Parcel in) {
        readFromParcel(in);
    }

    public int getTravelersQty() {
        return travelersQty;
    }

    public void setTravelersQty(int travelersQty) {
        this.travelersQty = travelersQty;
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

    public TripStop getHopOnStop() {
        return hopOnStop;
    }

    public boolean isPendingReservation() {
        return pendingReservation;
    }

    public void setPendingReservation(boolean pendingReservation) {
        this.pendingReservation = pendingReservation;
    }

    public TripStop getHopOnStopByDescription(String description) {
        for (TripStop tripStop: bookedTrip.getStops()) {
            if (tripStop.getDescription().equals(description)) {
                return tripStop;
            }
        }
        return null;
    }

    public void setHopOnStop(TripStop hopOnStop) {
        this.hopOnStop = hopOnStop;
    }

    public BigDecimal getReservationPrice() {
        return reservationPrice;
    }

    public void setReservationPrice(BigDecimal reservationPrice) {
        this.reservationPrice = reservationPrice;
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
        dest.writeSerializable(getHopOnStop());
    }

    private void readFromParcel(Parcel in) {

        // We just need to read back each field in the order that it was written to the parcel
        _id = in.readInt();
        bookedTrip = in.readParcelable(Trip.class.getClassLoader());
        isPaidValue = in.readByte() != 0;
        reservationDate = DateTime.parse(in.readString());
        hopOnStop = (TripStop) in.readSerializable();
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