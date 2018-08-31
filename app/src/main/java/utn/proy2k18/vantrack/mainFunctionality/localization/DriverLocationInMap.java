package utn.proy2k18.vantrack.mainFunctionality.localization;

public class DriverLocationInMap {
    private double latitude;
    private double longitude;

    public DriverLocationInMap() {
    }

    public DriverLocationInMap(double latitude, double longitude, String title, String snippet) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
