package utn.proy2k18.vantrack.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Rating implements Parcelable {

    private int uuid;
    private Float service_rating;
    private Float driver_rating;
    private String comment;

    private Rating(Parcel in) {
        uuid = in.readInt();
        if (in.readByte() == 0) {
            service_rating = null;
        } else {
            service_rating = in.readFloat();
        }
        if (in.readByte() == 0) {
            driver_rating = null;
        } else {
            driver_rating = in.readFloat();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(uuid);
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
