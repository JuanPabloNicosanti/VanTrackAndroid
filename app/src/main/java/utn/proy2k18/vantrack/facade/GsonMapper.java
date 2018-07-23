package utn.proy2k18.vantrack.facade;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonMapper {
    private static Gson GSON;

    public static Gson getInstance(){
        if(GSON == null)
            GSON = Converters.registerDateTime(new GsonBuilder()).create();
        return GSON;
    }

    private GsonMapper(){}


}
