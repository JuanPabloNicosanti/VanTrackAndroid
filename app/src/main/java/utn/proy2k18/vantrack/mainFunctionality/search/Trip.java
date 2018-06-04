package utn.proy2k18.vantrack.mainFunctionality.search;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Trip {

    private String company;
    private Date date;
    private String origin;
    private String destination;
    private float price;

    public Trip(String company, Date date, String origin, String destination, float price) {
        this.company = company;
        this.date = date;
        this.origin = origin;
        this.destination = destination;
        this.price = price;
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

    public float getPrice() { return price; }

    public String getFormattedDate(){
        SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy");
        return ft.format(date);
    }

}
