package utn.proy2k18.vantrack;

import android.app.Application;
import android.content.Context;


public class VanTrackApplication extends Application {

    private static String GOOGLE_TOKEN;
    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public Context getContext(){
        return mContext;
    }

    public static String getGoogleToken() {
        return GOOGLE_TOKEN;
    }

    public static void setGoogleToken(String googleToken) {
        GOOGLE_TOKEN = googleToken;
    }

}
