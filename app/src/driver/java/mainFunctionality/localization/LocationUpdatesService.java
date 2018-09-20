package mainFunctionality.localization;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class LocationUpdatesService extends Service implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    public static Location mCurrentLocation;
    private DatabaseReference mDriverLocation;

    @Override
    public void onCreate() {

        if (isGooglePlayServicesAvailable()) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(60 * 1000);
            mLocationRequest.setFastestInterval(60 * 1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDriverLocation = mDatabase.child("Trips").child(MapsActivityDriver.tripId).child("Drivers").child("DriverLocationInMap");
            //mLocationRequest.setSmallestDisplacement(10.0f);  /* min dist for location change, here it is 10 meter */
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            mGoogleApiClient.connect();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //Check Google play is available or not
    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        return ConnectionResult.SUCCESS == status;
    }


    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected void startLocationUpdates() {
        try {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
            }
        } catch (IllegalStateException e) {}
    }

    @Override
    public void onLocationChanged(Location location) {
        Map<String, Object> latLng = new HashMap<String, Object>();
        latLng.put("latitude",location.getLatitude());
        latLng.put("longitude",location.getLongitude());
        mDriverLocation.updateChildren(latLng);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
