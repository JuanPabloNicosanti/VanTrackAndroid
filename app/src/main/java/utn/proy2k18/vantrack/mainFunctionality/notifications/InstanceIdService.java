package utn.proy2k18.vantrack.mainFunctionality.notifications;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import utn.proy2k18.vantrack.mainFunctionality.preferences.Preferences;

public class InstanceIdService extends FirebaseInstanceIdService {

    public InstanceIdService() { super(); }

    @Override
    public void onTokenRefresh() {
        Preferences.setFirebaseToken(this, FirebaseInstanceId.getInstance().getToken());
    }
}
