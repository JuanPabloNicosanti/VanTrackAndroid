package utn.proy2k18.vantrack;

import android.app.Application;

import com.google.firebase.auth.FirebaseUser;

public class VanTrackApplication extends Application {

    public FirebaseUser user;

    public FirebaseUser getUser() {
        return user;
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }
}
