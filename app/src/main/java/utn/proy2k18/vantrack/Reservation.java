package utn.proy2k18.vantrack;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Reservation implements Parcelable {

    private String company;
    private Date date;
    private String origin;
    private String destination; // La idea es que haya un Enum o algo asi


    public Reservation(String company, Date date, String origin, String destination) {
        this.company = company;
        this.date = date;
        this.origin = origin;
        this.destination = destination;
    }

    public String getCompany() {
        return company;
    }

    public Date getDate() {
        return date;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(company);
        dest.writeLong(date.getTime());
        dest.writeString(origin);
        dest.writeString(destination);

    }

    public static final Creator<Reservation> CREATOR = new Creator<Reservation>() {
        @Override
        public Reservation createFromParcel(Parcel in) {
            String company = in.readString();
            Date date = new Date(in.readLong());
            String origin = in.readString();
            String destination = in.readString();
            return new Reservation(company, date,origin,destination);
        }

        @Override
        public Reservation[] newArray(int size) {
            return new Reservation[size];
        }

    };
}