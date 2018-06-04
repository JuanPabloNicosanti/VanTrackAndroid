package utn.proy2k18.vantrack.mainFunctionality.search;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Trip {

    private String company;
    private Date date;
    private String origin;
    private String destination;

    public Trip(String company, Date date, String origin, String destination) {
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

    public String getFormattedDate(){
        SimpleDateFormat ft = new SimpleDateFormat("dd/MM/YYYY");
        return ft.format(date);
    }

}
