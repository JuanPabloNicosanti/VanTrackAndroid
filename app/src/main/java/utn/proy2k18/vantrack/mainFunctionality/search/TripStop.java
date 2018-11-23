package utn.proy2k18.vantrack.mainFunctionality.search;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.joda.time.LocalTime;

import java.io.Serializable;


public class TripStop implements Serializable {

    private int id;
    private LocalTime hour;
    private String description;
    @JsonProperty("lat")
    private Double latitude;
    @JsonProperty("lng")
    private Double longitude;
    private Integer order;

    public TripStop() { }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalTime getHour() {
        return hour;
    }

    public void setHour(LocalTime hour) {
        this.hour = hour;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TripStop))
            return false;
        if (obj == this)
            return true;
        TripStop anotherStop = (TripStop) obj;
        return this.getId() == anotherStop.getId() &&
                this.getHour().isEqual(anotherStop.getHour()) &&
                this.getDescription().equalsIgnoreCase(anotherStop.getDescription()) &&
                this.getOrder().equals(anotherStop.getOrder());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + id;
        result = 31 * result + description.hashCode();
        result = 31 * result + hour.hashCode();
        result = 31 * result + order.hashCode();
        return result;
    }
}
