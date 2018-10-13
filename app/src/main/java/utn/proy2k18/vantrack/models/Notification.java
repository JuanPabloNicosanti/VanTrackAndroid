package utn.proy2k18.vantrack.models;


import com.fasterxml.jackson.annotation.JsonProperty;

import org.joda.time.DateTime;

public class Notification {

    @JsonProperty("notification_id")
    private Integer notificationId;
    @JsonProperty("username")
    private String username;
    @JsonProperty("trip_id")
    private Integer tripId;
    @JsonProperty("message")
    private String description;
    @JsonProperty("date")
    private DateTime dateTime;

    public Notification() { }

    public Notification(Integer notificationId, String username, Integer tripId, String description,
                        DateTime dateTime) {
        this.notificationId = notificationId;
        this.username = username;
        this.tripId = tripId;
        this.description = description;
        this.dateTime = dateTime;
    }

    public Integer getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getTripId() {
        return tripId;
    }

    public void setTripId(Integer tripId) {
        this.tripId = tripId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }
}
