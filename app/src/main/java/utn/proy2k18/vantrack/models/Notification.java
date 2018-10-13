package utn.proy2k18.vantrack.models;


public class Notification {

    private Integer notificationId;
    private String username;
    private Integer tripId;
    private String description;

    public Notification(Integer notificationId, String username, Integer tripId, String description) {
        this.notificationId = notificationId;
        this.username = username;
        this.tripId = tripId;
        this.description = description;
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
}
