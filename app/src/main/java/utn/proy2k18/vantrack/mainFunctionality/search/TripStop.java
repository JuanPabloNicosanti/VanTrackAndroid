package utn.proy2k18.vantrack.mainFunctionality.search;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.joda.time.LocalTime;

import java.io.Serializable;


public class TripStop implements Serializable {

    private int id;
    private LocalTime hour;
    private String description;
    @JsonProperty("lat")
    private Double latitude;
    @JsonProperty("lng")
    private Double longitude;

    public TripStop() { }

//    private TripStop(Parcel in) {
//        readFromParcel(in);
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalTime getHour() {
        return hour;
    }

    public void setHour(LocalTime hour) {
        this.hour = hour;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//
//        // We just need to write each field into the parcel. When we read from parcel, they
//        // will come back in the same order
//        dest.writeInt(getId());
//        dest.writeString(getHour().toString());
//        dest.writeString(getDescription());
//    }
//
//    private void readFromParcel(Parcel in) {
//
//        // We just need to read back each field in the order that it was written to the parcel
//        id = in.readInt();
//        hour = LocalTime.parse(in.readString());
//        description = in.readString();
//    }
//
//    // This field is needed for Android to be able to create new objects, individually or as arrays.
//    public static final Parcelable.Creator<TripStop> CREATOR =
//            new Parcelable.Creator<TripStop>() {
//                public TripStop createFromParcel(Parcel in) {
//                    return new TripStop(in);
//                }
//
//                public TripStop[] newArray(int size) {
//                    return new TripStop[size];
//                }
//            };

}
