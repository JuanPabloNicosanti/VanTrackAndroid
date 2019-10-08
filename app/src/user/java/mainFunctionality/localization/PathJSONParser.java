package mainFunctionality.localization;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    protected HashMap<String,Integer> parseDuration(JSONObject json) {
        HashMap<String,Integer> durationsList = new HashMap<>();
        
        try {
        JSONArray jRoutes = json.getJSONArray("routes");
        /* Traversing all routes */
        for (int i = 0; i < jRoutes.length(); i++) {
            JSONArray jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");

            /* Traversing all legs */
            for (int j = 0; j < jLegs.length(); j++) {
                JSONObject jDuration = (JSONObject)((JSONObject) jLegs.get(j)).get("duration");
                
                Integer duration = (Integer) jDuration.get("value");
                Integer minutes = duration/60;
                
                durationsList.put("duration"+j, minutes);
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return durationsList;
    }

    protected List<Double> parseDistance(JSONObject json) {
        List<Double> distancesList = new ArrayList<>();
        
        try {
            JSONArray jsonArray = json.getJSONArray("legs");
            long totalDistance = 0;
            for (int i = 0; i < jsonArray.length(); i++) {
                // distance
                JSONArray partialDistance = (JSONArray) (jsonArray.get(0));
                
                long distance = Long.parseLong(partialDistance.get(1).toString());
                totalDistance = totalDistance + distance;
            }
            // convert to kilometer
            Double dist = totalDistance / 1000.0;
            distancesList.add(dist);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return distancesList;
    }
}
