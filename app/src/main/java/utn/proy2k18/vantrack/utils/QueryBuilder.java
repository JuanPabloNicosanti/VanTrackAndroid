package utn.proy2k18.vantrack.utils;

import java.util.HashMap;

public class QueryBuilder {

    private final String baseUrl = "http://192.168.0.19:9290/";
    private final String tripUri;
    private final String reservationUri;
    private final String tripsUri;
    private final String reservationsUri;
    private final String modifyReservationUri;
    private final String driverTripsUri;
    private final String tripReservationsUri;
    private final String tripConfirmPassengersUri;
    private final String deleteReservationUri;
    private final String addReservationUri;
    private final String addRatingUri;
    private final String createMPPreferenceUri;
    private final String payReservationUri;
    private final String createUserUri;
    private final String notificationsUri;
    private final String actualUser;
    private final String endTrip;

    public QueryBuilder() {
        tripUri = "trip";
        tripsUri = "trips";
        reservationUri = "reservation";
        reservationsUri = "reservations";
        modifyReservationUri = "reservations/modify";
        createMPPreferenceUri = "mock/payments";
        payReservationUri = "reservations/pay";
        driverTripsUri = "trips/driver";
        tripReservationsUri = "reservations/users";
        tripConfirmPassengersUri = "reservations/confirm/";
        deleteReservationUri = "reservations/cancel";
        addReservationUri = "reservations/create";
        notificationsUri = "notifications";
        addRatingUri = "reservations/rate/";
        createUserUri = "users/create";
        actualUser = "users";
        endTrip = "trips/end";
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    private String getUrl(String urlType, HashMap<String, String> data) {
        return baseUrl + urlType + "?" + convertHashMapToString(data);
    }

    private String getUrl(String urlType, String tripId) {
        return baseUrl + urlType + tripId;
    }

    private String getUrl(String urlType) {
        return baseUrl + urlType;
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

    public String getCreateMPPreferenceUrl() {
        return getUrl(createMPPreferenceUri);
    }

    public String getReservationQuery(HashMap<String, String> data) {
        return getUrl(reservationUri, data);
    }

    public String getModifyReservationUrl() {
        return baseUrl + modifyReservationUri;
    }

    public String getTripReservationsUrl(HashMap<String, String> data) {
        return getUrl(tripReservationsUri, data);
    }

    public String getDeleteReservationUrl(HashMap<String, String> data) {
        return getUrl(deleteReservationUri, data);
    }

    public String getTripConfirmPassengersUrl(String tripId) {
        return getUrl(tripConfirmPassengersUri, tripId);
    }

    public String getCreateReservationUrl(HashMap<String, String> data){
        return getUrl(addReservationUri, data);
    }

    public String getPayReservationUrl(){
        return getUrl(payReservationUri);
    }

    public String getNotificationsUrl(HashMap<String, String> params) {
        return getUrl(notificationsUri, params);
    }

    public String getNotificationsUrl() {
        return getUrl(notificationsUri);
    }

    public String getCreateRatingUri(String reservationId) {
        return getUrl(addRatingUri,reservationId);
    }

    public String getCreateUserUrl() {
        return getUrl(createUserUri);
    }

    public String getActualUser(HashMap<String,String> email) {
        return getUrl(actualUser, email);
    }

    public String endTrip(String tripId) {
        return getUrl(endTrip,tripId );
    }
}
