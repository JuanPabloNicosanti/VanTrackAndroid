package utn.proy2k18.vantrack.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Rating implements Parcelable {

    @JsonProperty
    private Integer service_rating;
    @JsonProperty
    private Integer driver_rating;
    @JsonProperty
    private String comment;

    private Rating(Parcel in) {

        if (in.readByte() == 0) {
            service_rating = null;
        } else {
            service_rating = in.readInt();
        }
        if (in.readByte() == 0) {
            driver_rating = null;
        } else {
            driver_rating = in.readInt();
        }
        comment = in.readString();
    }

    public static final Creator<Rating> CREATOR = new Creator<Rating>() {
        @Override
        public Rating createFromParcel(Parcel in) {
            return new Rating(in);
        }

        @Override
        public Rating[] newArray(int size) {
            return new Rating[size];
        }
    };

        public Rating(Integer sRating, Integer dRating, String comment){
            service_rating = sRating;
            driver_rating = dRating;
            this.comment = comment;
        }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        if (service_rating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(service_rating);
        }
        if (driver_rating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(driver_rating);
        }
        dest.writeString(comment);
    }
}
