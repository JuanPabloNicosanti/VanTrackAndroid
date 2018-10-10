package utn.proy2k18.vantrack.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Rating implements Parcelable {

    private int tripId;
    private Float tripRating;
    private Float driverRating;
    private String comment;

    private Rating(Parcel in) {
        tripId = in.readInt();
        if (in.readByte() == 0) {
            tripRating = null;
        } else {
            tripRating = in.readFloat();
        }
        if (in.readByte() == 0) {
            driverRating = null;
        } else {
            driverRating = in.readFloat();
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
        dest.writeInt(tripId);
        if (tripRating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(tripRating);
        }
        if (driverRating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(driverRating);
        }
        dest.writeString(comment);
    }
}
