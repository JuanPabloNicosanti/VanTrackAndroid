package utn.proy2k18.vantrack.facade;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import utn.proy2k18.vantrack.connector.HttpConnector;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;

import static com.google.android.gms.common.util.ArrayUtils.newArrayList;

public class TripFacade {

    private static final Gson GSON = GsonMapper.getInstance();
    private static final String URL = "http://www.mocky.io/v2/5b5149782e000074005c182e";
    private static final String HTTP_GET = "GET";

    public List<Trip> getTrips(){
        final HttpConnector HTTP_CONNECTOR = HttpConnector.getInstance();
        try{
            String result = HTTP_CONNECTOR.execute(URL, HTTP_GET).get();
            Type listType = new TypeToken<ArrayList<Trip>>(){}.getType();
            return GSON.fromJson(result, listType);
        }catch (ExecutionException ee){
            ee.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        return newArrayList();
    }
}
