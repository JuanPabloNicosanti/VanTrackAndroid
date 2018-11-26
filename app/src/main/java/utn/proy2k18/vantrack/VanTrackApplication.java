package utn.proy2k18.vantrack;

import android.app.Application;
import android.content.Context;

import com.google.firebase.auth.FirebaseUser;

public class VanTrackApplication extends Application {

    public FirebaseUser user;
    private static String GOOGLE_TOKEN;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }

    public static String getGoogleToken() {
        return GOOGLE_TOKEN;
    }

    public static void setGoogleToken(String googleToken) {
        GOOGLE_TOKEN = googleToken;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }
}
