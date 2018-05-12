package utn.proy2k18.vantrack;


import java.text.SimpleDateFormat;
import java.util.Date;

public class Reservation  {

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

    public String getFormattedDate(){
        SimpleDateFormat ft = new SimpleDateFormat("E MM-dd hh:mm a");
        return ft.format(date);
    }


}