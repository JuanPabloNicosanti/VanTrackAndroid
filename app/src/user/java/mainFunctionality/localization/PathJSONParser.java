package mainFunctionality.localization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.android.volley.request.JsonArrayRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;

public class PathJSONParser {

    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {
        List<List<HashMap<String, String>>> routes = new ArrayList<>();
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        try {
            jRoutes = jObject.getJSONArray("routes");
            /* Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List<HashMap<String, String>> path = new ArrayList<>();

                /* Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    /* Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline;
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps
                                .get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /* Traversing all points */
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("lat",
                                    Double.toString(list.get(l).latitude));
                            hm.put("lng",
                                    Double.toString(list.get(l).longitude));
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return routes;
    }

    /**
     * Method Courtesy :
     * jeffreysambells.com/2010/05/27
     * /decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    public int parseDuration(String json) {

        JSONArray jsonArray;
        try {
            JSONObject object = (JSONObject) new JSONTokener(json).nextValue();

            JSONObject legsJson = object.getJSONObject("legs");
            jsonArray = object.getJSONArray("legs");
            int totalSeconds = 0;
            for (int i = 0; i < legsJson.length(); i++) {
                // ETA
                JSONArray partialDuration = (JSONArray) (jsonArray.get(1));
                int duration = Integer.parseInt(partialDuration.get(1).toString());
                totalSeconds = totalSeconds + duration;
            }
            int days = totalSeconds / 86400;
            int hours = (totalSeconds - days * 86400) / 3600;
            int minutes = (totalSeconds - days * 86400 - hours * 3600) / 60;
            int seconds = totalSeconds - days * 86400 - hours * 3600 - minutes * 60;
            return minutes;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double parseDistance(String json) {

        JSONArray jsonArray;
        try {
            JSONObject object = (JSONObject) new JSONTokener(json).nextValue();

            JSONObject legsJson = object.getJSONObject("legs");
            jsonArray = object.getJSONArray("legs");
            long totalDistance = 0;
            for (int i = 0; i < legsJson.length(); i++) {
                // distance
                JSONArray partialDistance = (JSONArray) (jsonArray.get(0));
                Long distance = Long.parseLong(partialDistance.get(1).toString());
                totalDistance = totalDistance + distance;
            }
            // convert to kilometer
            double dist = totalDistance / 1000.0;
            return dist;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
