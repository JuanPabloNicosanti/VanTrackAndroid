package utn.proy2k18.vantrack.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Rating {

    @JsonProperty("service_rating")
    private Integer serviceRating;
    @JsonProperty("driver_rating")
    private Integer driverRating;
    @JsonProperty
    private String comment;

    public Rating(Integer serviceRating, Integer driverRating, String comment) {
        this.serviceRating = serviceRating;
        this.driverRating = driverRating;
        this.comment = comment;
    }

    public Integer getServiceRating() {
        return serviceRating;
    }

    public void setServiceRating(Integer serviceRating) {
        this.serviceRating = serviceRating;
    }

    public Integer getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(Integer driverRating) {
        this.driverRating = driverRating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
