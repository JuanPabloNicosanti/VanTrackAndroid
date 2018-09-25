package utn.proy2k18.vantrack;

import android.app.Application;
import android.content.Context;

import com.google.firebase.auth.FirebaseUser;

public class VanTrackApplication extends Application {

    public FirebaseUser user;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }
}
