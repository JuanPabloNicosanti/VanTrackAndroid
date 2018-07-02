package utn.proy2k18.vantrack.mainFunctionality.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;

public class Preferences {
    private static final String PREF_NAME = "PREF_NAME";
    private static final String FIREBASE_TOKEN = "FIREBASE_TOKEN";

    public static String getFirebaseToken(Context context) {
        return FirebaseInstanceId.getInstance().getToken();
//        return getPreferences(context).getString(FIREBASE_TOKEN, null);
    }

    public static void setFirebaseToken(Context context, String token) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.putString(FIREBASE_TOKEN, token);
        editor.apply();
    }

    private static SharedPreferences.Editor getPreferencesEditor(Context context) {
        return getPreferences(context).edit();
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
}
