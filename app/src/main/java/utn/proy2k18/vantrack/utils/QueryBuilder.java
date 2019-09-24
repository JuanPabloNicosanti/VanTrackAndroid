package utn.proy2k18.vantrack.utils;

import java.util.HashMap;

public class QueryBuilder {

    private static final String BASE_URL = "http://192.168.0.169:9290/";

    // Trips
    public static final String TRIP = "trip";
    public static final String TRIPS = "trips";
    public static final String DRIVER_TRIPS = "trips/driver";
    public static final String END_TRIP = "trips/driver/finish/";
    public static final String ALL_STOPS_DESCRIPTIONS = "stops";
    // Reservations
    public static final String RESERVATION = "reservation";
    public static final String RESERVATIONS = "reservations";
    public static final String MODIFY_RESERVATION = "reservations/modify";
    public static final String TRIP_RESERVATION = "reservations/users";
    public static final String TRIP_CONFIRM_PASSENGERS = "reservations/confirm/";
    public static final String DELETE_RESERVATION = "reservations/cancel";
    public static final String ADD_RESERVATION = "reservations/create";
    public static final String ADD_RATING = "reservations/rate/";
    public static final String CANCELLATION_CAUSE = "cancellation/causes";
    // Payments
    public static final String CREATE_MP_PREFERENCE = "mock/payments";
    public static final String PAY_RESERVATION = "reservations/pay";
    // Users
    public static final String CREATE_USER = "users/create";
    public static final String ACTUAL_USER = "users";
    public static final String MODIFY_USER_PWD = "users/password";
    // Notifications
    public static final String NOTIFICATIONS = "notifications";

    public QueryBuilder() { }

    public String getUrl(String urlType, HashMap<String, String> data) {
        return BASE_URL + urlType + "?" + convertHashMapToString(data);
    }

    public String getUrl(String uri, String resourceId) {
        return BASE_URL + uri + resourceId;
    }

    public String getUrl(String uri) {
        return BASE_URL + uri;
    }

    private String convertHashMapToString(HashMap<String, String> data) {
        String strData = data.toString().replaceAll("[{}]", "");
        return strData.replace(" ", "").replace(",", "&");
    }
}
