package mainFunctionality.viewsModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import mainFunctionality.localization.MapsActivityUser;
import utn.proy2k18.vantrack.models.LatLng;
import utn.proy2k18.vantrack.utils.JacksonSerializer;
import utn.proy2k18.vantrack.utils.QueryBuilder;

public class LatLngViewModel {
    private QueryBuilder queryBuilder = new QueryBuilder();
    private static final ObjectMapper objectMapper = JacksonSerializer.getObjectMapper();
    private static LatLng latLng;

    public LatLng getLatLng(@JsonProperty("place") String placeOfInterest) {
        HashMap<String, String> param = new HashMap<>();
        param.put("place", placeOfInterest);
        String url = queryBuilder.getLatLngQuery(param);
        LatLng latLng = getLatLngFromBack(url);
        return latLng;
    }

    private LatLng getLatLngFromBack(String url) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String res) {
                // called when response HTTP status is "200 OK"
                TypeReference latLngResultsType = new TypeReference<LatLng>() {
                };
                try {
                    latLng = objectMapper.readValue(res, latLngResultsType);
                    MapsActivityUser.createDefaultMarker(latLng.getLatitude(),latLng.getLongitude());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                 latLng = new LatLng(0, 0);
            }
        });
        return latLng;
    }
}

