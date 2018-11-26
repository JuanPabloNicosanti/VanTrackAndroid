package mainFunctionality.localization;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mainFunctionality.CentralActivity;
import mainFunctionality.viewsModels.TripsReservationsViewModel;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.connector.HttpConnector;
import utn.proy2k18.vantrack.mainFunctionality.localization.DriverLocationInMap;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.models.Reservation;
import utn.proy2k18.vantrack.viewModels.UsersViewModel;

public class MapsActivityUser extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String ARG_PARAM1 = "reservation_id";
    private static GoogleMap mMap;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private DatabaseReference mDriverLocation;
    private DatabaseReference mUserLocation;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
            .child("Trips");

    private LatLng mVanLocation;
    private LatLng mOrigin;
    private LatLng mDestination;
    private Marker marker;
    private Trip trip;
    private TripsReservationsViewModel reservationsModel = TripsReservationsViewModel.getInstance();
    private UsersViewModel usersModel = UsersViewModel.getInstance();
    public int switcher = 0;
    public String url;
    private Integer lastOriginValue = Integer.MAX_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_user) ;

        //Grab Trip to use Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Bundle parameters = getIntent().getExtras();
        assert parameters != null;
        Integer reservationId = parameters.getInt(ARG_PARAM1);
        Reservation reservation = reservationsModel.getReservationById(reservationId,
                usersModel.getActualUserEmail());
        assert reservation != null;
        trip = reservation.getBookedTrip();
        String tripId = String.format("%s", trip.get_id());
        mOrigin = new LatLng(reservation.getHopOnStop().getLatitude(),
                reservation.getHopOnStop().getLongitude());
        mDestination = trip.getLatLngDestination(trip.getDestination());
        // Nesting this way as it is the simplest way to post driver's location, so it encapsulates
        // it from Users' ones.
        mDriverLocation = mDatabase.child(tripId).child("Drivers");
        mUserLocation = mDatabase.child(tripId).child("Users").child(
                mAuth.getCurrentUser().getUid());

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                    this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() &&
                ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED)
            {
                buildGoogleApiClient();
                createDefaultMarker(mOrigin.latitude,mOrigin.longitude);
                createDefaultMarker(mDestination.latitude,mDestination.longitude);
                mMap.setMyLocationEnabled(true);
            }
            else
                {
                    checkLocationPermission();
                    buildGoogleApiClient();
                }
        }
        else {
            buildGoogleApiClient();
            createDefaultMarker(mOrigin.latitude,mOrigin.longitude);
            createDefaultMarker(mDestination.latitude,mDestination.longitude);
            mMap.setMyLocationEnabled(true);
        }
    }

    //Create van marker for all users
    protected Marker createMarker(double latitude, double longitude) {
        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(
                        R.drawable.ic_volkswagen_van))));
    }

    //Create marker for origin and destination
    public Marker createDefaultMarker(double latitude, double longitude) {
        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.defaultMarker()));
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setFastestInterval(30 * 1000);
        mLocationRequest.setSmallestDisplacement(20);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        Location mUserLastLocation = location;
        try {
            Map<String, Object> latLng = new HashMap<String, Object>();
            latLng.put("latitude",location.getLatitude());
            latLng.put("longitude",location.getLongitude());
            mUserLocation.updateChildren(latLng);
        }
        catch (Exception ignored) {
        }

        LatLng latLng = new LatLng(mUserLastLocation.getLatitude(), mUserLastLocation.getLongitude());

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(latLng).tilt(30)
                .zoom(15)
                .build()));

        final ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                if (marker == null) {
                        DriverLocationInMap driver = dataSnapshot.getValue(DriverLocationInMap.class);
                        marker = createMarker(driver.getLatitude(), driver.getLongitude());
                        marker.setVisible(true);
                        mVanLocation = new LatLng(driver.getLatitude(), driver.getLongitude());
                        //Build route from van to destination
                        url = getMapsApiDirectionsUrl();
                        ReadTask downloadTask = new ReadTask();
                        downloadTask.execute(url);
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                    switcher=1;
                    DriverLocationInMap driver = dataSnapshot.getValue(DriverLocationInMap.class);
                    if(marker!=null) {
                        mVanLocation = new LatLng(driver.getLatitude(), driver.getLongitude());
                        marker.setPosition(new LatLng(mVanLocation.latitude, mVanLocation.longitude));
                        url = getMapsApiDirectionsUrl();
                        ReadTask downloadTask = new ReadTask();
                        downloadTask.execute(url);
                    }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    marker.remove();
                }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };
        mDriverLocation.addChildEventListener(childEventListener);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) { }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onStop() {
        mUserLocation.removeValue();
        super.onStop();
    }

    //Create polyline to trace route from current location to destination with waypoint in the origin
    private String getMapsApiDirectionsUrl() {
        String waypoints = "waypoints=optimize:true"
                + "|" + mOrigin.latitude + "," + mOrigin.longitude;


        String sensor = "sensor=false";
        String departureTime = "departure_time=now";
        String origin= "origin=" + mVanLocation.latitude + "," + mVanLocation.longitude;
        String destination = "destination=" + mDestination.latitude + "," + mDestination.longitude;
        String key = "key=AIzaSyBC9R737UEpoOMHGt9yRyUCvs7ouqW-R_Y";
        String params = origin + "&" + destination + "&" + waypoints  + "&" + departureTime + "&"
                + sensor + "&" + key;
        String output = "json";
        return "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
    }

    //Custom marker for viewing a Van instead of a default red marker
    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {

        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.custom_marker, null);
        ImageView markerImageView = customMarkerView.findViewById(R.id.vanImage);
        markerImageView.setImageResource(resId);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(),
                customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(),
                customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    @SuppressLint("StaticFieldLeak")
    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {

            String data = "";
            try {
                HttpConnector http = new HttpConnector();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            switch(switcher){
                case 0: new ParserTask().execute(result);
                break;
                case 1: new CalculateETATask().execute(result);
                break;
                default:
            }
        }
    }
    @SuppressLint("StaticFieldLeak")
    public class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }
                polyLineOptions.addAll(points);
                polyLineOptions.width(5);
                polyLineOptions.color(Color.BLUE);
            }
            if(polyLineOptions !=null) {
                mMap.addPolyline(polyLineOptions);
            }
            else {
                if(marker!=null)
                marker.remove();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class CalculateETATask extends
            AsyncTask<String, Integer,HashMap<String,Integer>> {

        @Override
        protected HashMap<String,Integer> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            HashMap<String, Integer> data = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                data = parser.parseDuration(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(HashMap<String,Integer> data) {
            //Post distance and duration
            if(data.get("duration0")!=null && data.get("duration1")!=null) {
                Integer originDuration = data.get("duration0");
                Integer destinationDuration = data.get("duration1");
                TextView originETA = findViewById(R.id.time_to_origin);
                TextView destinationETA = findViewById(R.id.time_to_destination);

                //Control ETA

                //Cannot parse int at first call as text is empty
                if(lastOriginValue != Integer.MAX_VALUE)
                lastOriginValue = Integer.parseInt(originETA.getText().toString());
                else lastOriginValue = originDuration;

                // Check if the van is very close to origin, set ETA to 0 so after it goes
                // through this point the app knows it.
                if(lastOriginValue != 0) {
                    originETA.setText(originDuration.toString());
                    Integer destinationFinalValue = originDuration + destinationDuration;
                    destinationETA.setText(destinationFinalValue.toString());
                }
                else if(originDuration <= 2) {
                    originETA.setText("0");
                    destinationETA.setText(destinationDuration.toString());
                }
                else {
                    Integer destinationFinalValue =  destinationDuration - originDuration;
                    destinationETA.setText(destinationFinalValue.toString());
                }
                //Show popup when van is arriving to final destination.
                Integer destinationFinalValue = Integer.parseInt(destinationETA.getText().toString());
                if(destinationFinalValue <= 1){
                    new AlertDialog.Builder(
                            MapsActivityUser.this)
                            .setTitle(R.string.trip_end)
                            .setMessage(R.string.arrive_destination_msg)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    trip.setFinished();
                                    Intent intent = new Intent(MapsActivityUser.this,
                                            CentralActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .show();
                }
            }
        }
    }
}
