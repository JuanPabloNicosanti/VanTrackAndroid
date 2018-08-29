package utn.proy2k18.vantrack.utils;

import java.util.HashMap;

public class QueryBuilder {

    private String baseUrl = "http://192.168.0.21:9290/";
    private String tripUri;
    private String reservationUri;
    private String tripsUri;
    private String reservationsUri;
    private String paymentsUri;

    public QueryBuilder() {
        tripUri = "trip";
        tripsUri = "mock/trips";
        reservationUri = "reservation";
        reservationsUri = "mock/reservations";
        paymentsUri = "mock/payments";
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getPaymentsUri(HashMap<String, String> data) {
        return getUri(paymentsUri, data);
    }

    private String getUrl(String urlType, HashMap<String, String> data) {
        return baseUrl + urlType + "?" + convertHashMapToString(data);
    }

    private String getUri(String urlType, HashMap<String, String> data) {
        return urlType + "?" + convertHashMapToString(data);
    }

    private String convertHashMapToString(HashMap<String, String> data) {
        return data.toString().replaceAll("[{}]", "");
    }

    public String getTripQuery(HashMap<String, String> data) {
        return getUrl(tripUri, data);
    }

    public String getTripsQuery(HashMap<String, String> data) {
        return getUrl(tripsUri, data);
    }

    public String getReservationsQuery(HashMap<String, String> data) {
        return getUrl(reservationsUri, data);
    }

    public String getPaymentsQuery(HashMap<String, String> data) {
        return getUrl(paymentsUri, data);
    }

    public String getReservationQuery(HashMap<String, String> data) {
        return getUrl(reservationUri, data);
    }
}
