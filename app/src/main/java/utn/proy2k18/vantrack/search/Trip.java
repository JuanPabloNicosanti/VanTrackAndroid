package utn.proy2k18.vantrack.search;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import utn.proy2k18.vantrack.mainFunctionality.Company;

public class Trip {
    private UUID _id;
    private Company company;
    private Calendar calendar;
    private String origin;
    private String destination;
    private float price;
    private String driverId;

    public Trip(Company company, Date datetime, String origin, String destination, float price) {
        this._id = UUID.randomUUID();
        this.company = company;
        this.calendar = Calendar.getInstance();
        calendar.setTime(datetime);
        this.origin = origin;
        this.destination = destination;
        this.price = price;
    }

    public UUID get_id() {
        return _id;
    }

    public String getCompanyName() {
        return company.getCompanyName();
    }

    public double getCompanyCalification() { return company.getCalification(); }

    public Calendar getDate() {
        return calendar;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public float getPrice() { return price; }

    public String getFormattedDate(){
        SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        ft.setTimeZone(calendar.getTimeZone());
        return ft.format(calendar.getTime());
    }

    public void setDate(String newStrDate) {
        try {
            SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            calendar.setTime(ft.parse(newStrDate));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
    }

    public int getTimeHour() {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }



    public String getFormattedTime(){
        SimpleDateFormat ft = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        ft.setTimeZone(calendar.getTimeZone());
        return ft.format(calendar.getTime());
    }

    public void setHour(String newStrDate) {
        try {
            SimpleDateFormat ft = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            calendar.setTime(ft.parse(newStrDate));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
    }

    public void setDateHour(String newStrDate,String newStrTime) {
        try {
            SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.ENGLISH);
            String concatDate = newStrDate +" "+ newStrTime;
            calendar.setTime(ft.parse(concatDate));

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
    }

}
