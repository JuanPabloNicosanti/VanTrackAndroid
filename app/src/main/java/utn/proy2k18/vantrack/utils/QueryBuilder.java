package utn.proy2k18.vantrack.utils;

import java.util.HashMap;

public class QueryBuilder {

    private String baseUrl = "http://192.168.0.6:9290/";
    private String tripUri;
    private String reservationUri;
    private String tripsUri;
    private String reservationsUri;
    private String modifyReservationUri;
    private String paymentsUri;
    private String driverTripsUri;

    public QueryBuilder() {
        tripUri = "trip";
        tripsUri = "trips";
        reservationUri = "reservation";
        reservationsUri = "reservations";
        modifyReservationUri = "reservations/modify";
        paymentsUri = "mock/payments";
        driverTripsUri = "trips/driver";
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getPaymentsUri() {
        return paymentsUri;
    }

    private String getUrl(String urlType, HashMap<String, String> data) {
        return baseUrl + urlType + "?" + convertHashMapToString(data);
    }

    private String getUri(String urlType, HashMap<String, String> data) {
        return urlType + "?" + convertHashMapToString(data);
    }

    private String convertHashMapToString(HashMap<String, String> data) {
        String strData = data.toString().replaceAll("[{}]", "");
        return strData.replace(" ", "").replace(",", "&");
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

    public String getDriverTripsUrl(HashMap<String, String> data) {
        return getUrl(driverTripsUri, data);
    }

    public String getPaymentsQuery(HashMap<String, String> data) {
        return getUrl(paymentsUri, data);
    }

    public String getReservationQuery(HashMap<String, String> data) {
        return getUrl(reservationUri, data);
    }

    public String getModifyReservationUrl() {
        return baseUrl + getModifyReservationUri();
    }

    public String getModifyReservationUri() {
        return modifyReservationUri;
    }

    public void setModifyReservationUri(String modifyReservationUri) {
        this.modifyReservationUri = modifyReservationUri;
    }
}
