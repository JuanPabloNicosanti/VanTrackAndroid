package utn.proy2k18.vantrack.mainFunctionality.search;

import android.os.Parcel;
import android.os.Parcelable;
import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.android.gms.maps.model.LatLng;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

import utn.proy2k18.vantrack.models.Company;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Trip implements Parcelable {
    @JsonProperty("trip_id")
    private int _id;
    @JsonProperty("company")
    private Company company;
    @JsonProperty("date")
    private LocalDate date;
    @JsonProperty("time")
    private LocalTime time;
    @JsonProperty("origin")
    private String origin;
    @JsonProperty("destination")
    private String destination;
    @JsonProperty("price")
    private float price;
    @JsonProperty("driver_id")
    private int driverId;
    @JsonProperty("stops")
    private List<TripStop> stops;

    private Trip(){ }

    public Trip(Parcel in) {
        readFromParcel(in);
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getCompanyName() {
        return getCompany().getBussinessName();
    }

    public float getCompanyCalification() {
        return getCompany().getCalification();
    }

    public int getTimeHour() {
        return this.time.getHourOfDay();
    }

    public List<TripStop> getStops() {
        return stops;
    }

    public void setStops(List<TripStop> stops) {
        this.stops = stops;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Trip)) {
            return false;
        }
        Trip other = (Trip) o;
        return this.get_id() == other.get_id();
    }

    public String createStrStops() {
        String strStops = "";
        int i = 0;
        int qty_stops = getStops().size();
        for (TripStop tripStop: getStops()) {
            String c = ", ";
            strStops += tripStop.getDescription();
            i++;
            if (i == qty_stops) {
                c = ".";
            }
            strStops += c;
        }
        return strStops;
    }

    public LatLng getLatLngDestination(String description) {
        for (TripStop tripStop : this.getStops()) {
            if (tripStop.getDescription().equals(description)) {
                return new LatLng(tripStop.getLatitude(), tripStop.getLongitude());
            }
        }
        return new LatLng(0,0);
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
        dest.writeString(getOrigin());
        dest.writeString(getDestination());
        dest.writeFloat(getPrice());
        dest.writeSerializable(getCompany());
        dest.writeString(getDate().toString());
        dest.writeString(getTime().toString());
        dest.writeInt(getDriverId());
        dest.writeList(getStops());
    }

    private void readFromParcel(Parcel in) {

        // We just need to read back each field in the order that it was written to the parcel
        _id = in.readInt();
        origin = in.readString();
        destination = in.readString();
        price = in.readFloat();
        company = (Company) in.readSerializable();
        date = LocalDate.parse(in.readString());
        time = LocalTime.parse(in.readString());
        driverId = in.readInt();
        stops = new ArrayList<TripStop>();
        in.readList(stops, null);
    }

//    This field is needed for Android to be able to create new objects, individually or as arrays.
    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator<Trip>() {
                public Trip createFromParcel(Parcel in) {
                    return new Trip(in);
                }

                public Trip[] newArray(int size) {
                    return new Trip[size];
                }
            };
}
