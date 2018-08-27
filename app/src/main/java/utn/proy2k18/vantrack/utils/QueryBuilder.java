package utn.proy2k18.vantrack.utils;

import java.util.HashMap;

public class QueryBuilder {

    private String baseUrl = "http://192.168.0.21:9290/";
    private String tripBaseUrl;
    private String reservationBaseUrl;
    private String tripsBaseUrl;
    private String reservationsBaseUrl;

    public QueryBuilder() {
        tripBaseUrl = "trip";
        tripsBaseUrl = "mock/trips";
        reservationBaseUrl = "reservation";
        reservationsBaseUrl = "mock/reservations";
    }

    private String getUrl(String urlType, HashMap<String, String> data) {
        return baseUrl + urlType + "?" + convertHashMapToString(data);
    }

    private String convertHashMapToString(HashMap<String, String> data) {
        return data.toString().replaceAll("[{}]", "");
    }

    public String getTripQuery(HashMap<String, String> data) {
        return getUrl(tripBaseUrl, data);
    }

    public String getTripsQuery(HashMap<String, String> data) {
        return getUrl(tripsBaseUrl, data);
    }

    public String getReservationsQuery(HashMap<String, String> data) {
        return getUrl(reservationsBaseUrl, data);
    }

    public String getReservationQuery(HashMap<String, String> data) {
        return getUrl(reservationBaseUrl, data);
    }
}
