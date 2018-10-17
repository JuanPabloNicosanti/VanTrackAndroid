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
    @JsonProperty("notification_msg_id")
    private Integer notificationMessageId;

    public Notification() { }

    public Notification(String username, Integer tripId, Integer notificationMessageId) {
        this.notificationMessageId = notificationMessageId;
        this.username = username;
        this.tripId = tripId;
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

    public Integer getNotificationMessageId() {
        return notificationMessageId;
    }

    public void setNotificationMessageId(Integer notificationMessageId) {
        this.notificationMessageId = notificationMessageId;
    }
}
