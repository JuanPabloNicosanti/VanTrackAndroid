package mainFunctionality.search;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import utn.proy2k18.vantrack.mainFunctionality.Company;

public class Trip {

    private Company company;
    private Calendar calendar;
    private String origin;
    private String destination;
    private float price;

    public Trip(Company company, Date datetime, String origin, String destination, float price) {
        this.company = company;
        this.calendar = Calendar.getInstance();
        calendar.setTime(datetime);
        this.origin = origin;
        this.destination = destination;
        this.price = price;
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
        SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy");
        ft.setTimeZone(calendar.getTimeZone());
        return ft.format(calendar.getTime());
    }

    public int getTimeHour() {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

}
