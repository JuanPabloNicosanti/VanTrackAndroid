package utn.proy2k18.vantrack.search;

import com.google.firebase.database.Exclude;

import org.joda.time.DateTime;

import java.util.UUID;

import utn.proy2k18.vantrack.mainFunctionality.Company;

public class Trip {
    private UUID uuid;
    private String _id;
    private Company company;
    private DateTime date;
    private String origin;
    private String destination;
    private float price;
    private String driverId;

    public Trip() {
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

    public String getStrDate(){
        return this.date.toString();
    }

    public void setStrDate(String date){
        this.date = new DateTime(date);
    }

    @Exclude
    public String getCalendarDate(){
        return this.date.toLocalDate().toString();
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
    public void setDateHour(String date, String hour) {
        // TODO: add minutes
        this.date = new DateTime(date).withTime(Integer.parseInt(hour), 0, 0, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Trip)) {
            return false;
        }
        Trip other = (Trip) o;
        return this.get_id().equals(other.get_id());
    }
}
