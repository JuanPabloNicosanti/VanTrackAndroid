package utn.proy2k18.vantrack.models;

import org.joda.time.DateTime;

public class UserPenalty {

    private Integer penaltyId;
    private String type;
    private String description;
    private DateTime dateTime;
    private Integer duration;

    public UserPenalty() { }

    public Integer getPenaltyId() {
        return penaltyId;
    }

    public void setPenaltyId(Integer penaltyId) {
        this.penaltyId = penaltyId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
