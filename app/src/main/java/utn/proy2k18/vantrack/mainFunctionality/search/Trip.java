package utn.proy2k18.vantrack.mainFunctionality.search;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.UUID;

import utn.proy2k18.vantrack.models.Company;

public class Trip implements Parcelable {
    private UUID uuid;
    private String _id;
    private Company company;
    private DateTime date;
    private String origin;
    private String destination;
    private float price;
    private String driverId;
    private SimpleDateFormat dtfOut = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    private DateTimeFormatter dtfIn = DateTimeFormat.forPattern("dd-MM-yyyyHH:mm");

    public Trip(){

    }

    public Trip(UUID uuid, String driverId) {
        this._id = uuid.toString();
        this.driverId = driverId;
    }

    public Trip(Parcel in) {
        readFromParcel(in);
    }

    public Trip(Company company, DateTime datetime, String origin, String destination, float price) {
        this.uuid = UUID.randomUUID();
        this._id = uuid.toString();
        this.company = company;
        this.date = datetime;
        this.origin = origin;
        this.destination = destination;
        this.price = price;
    }

    @Exclude
    public UUID getUuid() {
        return uuid;
    }

    @Exclude
    public void setUuid(UUID uuid) {
        UUID uuid1;
        this.uuid = uuid;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @Exclude
    public DateTime getDate() {
        return date;
    }

    @Exclude
    public void setDate(DateTime date) {
        this.date = date;
    }

    @Exclude
    public String getCalendarDate(){
        return this.dtfOut.format(this.date.toCalendar(Locale.getDefault()).getTime());
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

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    @Exclude
    public String getCompanyName() {
        return company.getBussinessName();
    }

    @Exclude
    public double getCompanyCalification() { return company.getCalificationAvg(); }

    @Exclude
    public int getTimeHour() {
        return this.date.getHourOfDay();
    }

    @Exclude
    public String getStrTime() {
        return String.format("%s:%s", this.date.getHourOfDay(), this.date.getMinuteOfHour());
    }

    @Exclude
    public void setDateTime(String textDateTime) {
        this.date = dtfIn.parseDateTime(textDateTime);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Trip)) {
            return false;
        }
        Trip other = (Trip) o;
        return this.get_id().equals(other.get_id());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        // We just need to write each field into the parcel. When we read from parcel, they
        // will come back in the same order
        dest.writeString(get_id());
        dest.writeString(getOrigin());
        dest.writeString(getDestination());
        dest.writeFloat(getPrice());
        dest.writeSerializable(getCompany());
        dest.writeString(date.toString());
    }

    private void readFromParcel(Parcel in) {

        // We just need to read back each field in the order that it was written to the parcel
        _id = in.readString();
        origin = in.readString();
        destination = in.readString();
        price = in.readFloat();
        company = (Company) in.readSerializable();
        date = DateTime.parse(in.readString());
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
