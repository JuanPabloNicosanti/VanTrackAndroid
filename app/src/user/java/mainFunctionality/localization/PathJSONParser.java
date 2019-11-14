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
        int index = 0;
        int len = encoded.length();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            }
            
            while (b >= 0x20);
            
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            }
            
            while (b >= 0x20);
            
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    protected HashMap<String, Number> parseDuration(JSONObject json) {
    	int count = 0;
        HashMap<String,Number> durationsList = new HashMap<>();
        
        try {
            JSONArray elements = json
                .getJSONArray("rows")
                .getJSONObject(0)
                .getJSONArray ("elements");
            
            if (elements.length() > 1) {
                Integer durationToOrigin = Integer.parseInt(elements
                    .getJSONObject(0)
                    .getJSONObject("duration_in_traffic")
                    .get("value").toString()) / 60;
    
                durationsList.put("minutesToOrigin", durationToOrigin);
                count++;
            }
            
            Integer durationToDestination = Integer.parseInt(elements
                .getJSONObject(count)
                .getJSONObject("duration_in_traffic")
                .get("value").toString()) / 60;
	
	        Double distanceToDestination = Double.parseDouble(elements
		        .getJSONObject(count)
		        .getJSONObject("distance")
		        .get("value").toString()) / 1000;
	        
            durationsList.put("minutesToDestination", durationToDestination);
            durationsList.put("kilometersToDestination", distanceToDestination);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return durationsList;
    }
}
