package mainFunctionality.localization;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
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

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class MapsActivityUser extends FragmentActivity implements OnMapReadyCallback {
	
	private static final String ARG_PARAM1 = "reservation_id";
	private static GoogleMap mMap;
	public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
	
	private FusedLocationProviderClient googleClient;
	private LocationCallback locationCallback;
	private LocationRequest locationRequest;
	private DatabaseReference driverLocation;
	private DatabaseReference userLocation;
	private DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Trips");
	
	private LatLng vanLocation;
	private LatLng origin;
	private LatLng destination;
	private Marker marker;
	private Trip trip;
	private TripsReservationsViewModel reservationsModel = TripsReservationsViewModel.getInstance();
	public int switcher = 0;
	public String url;
	private Integer lastOriginValue = Integer.MAX_VALUE;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps_user);
		
		googleClient = LocationServices.getFusedLocationProviderClient(this);
		
		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		locationRequest.setInterval(20 * 1000);
		
		locationCallback = new LocationCallback() {
			@Override
			public void onLocationResult(LocationResult locationResult) {
				if (locationResult == null) {
					return;
				}
				updateLocation(locationResult.getLastLocation());
			}
		};
		
		FirebaseAuth mAuth = FirebaseAuth.getInstance();
		Bundle parameters = getIntent().getExtras();
		assert parameters != null;
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		
		int reservationId = parameters.getInt(ARG_PARAM1);
		Reservation reservation = reservationsModel.getReservationById(reservationId,
				user.getEmail());
		trip = reservation.getBookedTrip();
		String tripId = String.format("%s", trip.get_id());
		
		origin = new LatLng(reservation.getHopOnStop().getLatitude(),
				reservation.getHopOnStop().getLongitude());
		destination = trip.getLatLngDestination(trip.getDestination());
		
		// Nesting this way as it is the simplest way to post driver's location, so it encapsulates
		// it from Users' ones.
		driverLocation = database.child(tripId).child("Drivers");
		userLocation = database.child(tripId).child("Users").child(mAuth.getCurrentUser().getUid());
		
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			checkLocationPermission();
		}
		
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		googleClient.removeLocationUpdates(locationCallback);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (ContextCompat.checkSelfPermission(this,
				android.Manifest.permission.ACCESS_FINE_LOCATION)
				== PackageManager.PERMISSION_GRANTED) {
			googleClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
		}
	}
	
	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(this,
					android.Manifest.permission.ACCESS_FINE_LOCATION) ==
					PackageManager.PERMISSION_GRANTED) {
				createDefaultMarker(origin.latitude, origin.longitude);
				createDefaultMarker(destination.latitude, destination.longitude);
				
				mMap.setMyLocationEnabled(true);
			} else {
				checkLocationPermission();
			}
		} else {
			createDefaultMarker(origin.latitude, origin.longitude);
			createDefaultMarker(destination.latitude, destination.longitude);
			
			mMap.setMyLocationEnabled(true);
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		userLocation.removeValue();
	}
	
	public void checkLocationPermission() {
		if (ContextCompat.checkSelfPermission(this,
				android.Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this,
					new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
					MY_PERMISSIONS_REQUEST_LOCATION);
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
	                                       @NonNull int[] grantResults) {
		if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
					googleClient.getLastLocation().addOnSuccessListener(this, location -> {
						if (location != null) {
							googleClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
							mMap.setMyLocationEnabled(true);
						}
					});
				}
			} else {
				Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
			}
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
	protected Marker createDefaultMarker(double latitude, double longitude) {
		return mMap.addMarker(new MarkerOptions()
				.position(new LatLng(latitude, longitude))
				.anchor(0.5f, 0.5f)
				.icon(BitmapDescriptorFactory.defaultMarker()));
	}
	
	private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {
		
		View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.custom_marker, null);
		
		ImageView markerImageView = customMarkerView.findViewById(R.id.vanImage);
		markerImageView.setImageResource(resId);
		customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(),
				customMarkerView.getMeasuredHeight());
		
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
	
	protected void updateLocation(Location location) {
		try {
			Map<String, Object> latLng = new HashMap<String, Object>();
			
			latLng.put("latitude", location.getLatitude());
			latLng.put("longitude", location.getLongitude());
			
			userLocation.updateChildren(latLng);
		} catch (Exception ignored) {
		}
		
		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
		
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
					vanLocation = new LatLng(driver.getLatitude(), driver.getLongitude());
					
					//Build route from van to destination
					url = getMapsApiDirectionsUrl();
					ReadTask downloadTask = new ReadTask();
					downloadTask.execute(url);
				}
			}
			
			@Override
			public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
				switcher = 1;
				
				DriverLocationInMap driver = dataSnapshot.getValue(DriverLocationInMap.class);
				
				if (marker != null) {
					vanLocation = new LatLng(driver.getLatitude(), driver.getLongitude());
					marker.setPosition(new LatLng(vanLocation.latitude, vanLocation.longitude));
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
			public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
			}
		};
		driverLocation.addChildEventListener(childEventListener);
	}
	
	//Create polyline to trace route from current location to destination with waypoint in the origin
	private String getMapsApiDirectionsUrl() {
		String waypoints = "waypoints=optimize:true"
				+ "|" + origin.latitude + "," + origin.longitude;
		
		
		String sensor = "sensor=false";
		String departureTime = "departure_time=now";
		String origin = "origin=" + vanLocation.latitude + "," + vanLocation.longitude;
		String destinationPlace = "destination=" + destination.latitude + "," + destination.longitude;
		
		String key = "key=AIzaSyBC9R737UEpoOMHGt9yRyUCvs7ouqW-R_Y";
		
		String params = origin + "&" + destinationPlace + "&" + waypoints + "&" + departureTime + "&"
				+ sensor + "&" + key;
		String output = "json";
		return "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + params;
	}
	
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
			switch (switcher) {
				case 0:
					new ParserTask().execute(result);
					break;
				case 1:
					new CalculateETATask().execute(result);
					break;
				default:
			}
		}
	}
	
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
			if (polyLineOptions != null) {
				mMap.addPolyline(polyLineOptions);
			} else {
				if (marker != null)
					marker.remove();
			}
		}
	}
	
	public class CalculateETATask extends
			AsyncTask<String, Integer, HashMap<String, Integer>> {
		
		@Override
		protected HashMap<String, Integer> doInBackground(
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
		protected void onPostExecute(HashMap<String, Integer> data) {
			//Post distance and duration
			if (data.get("duration0") != null && data.get("duration1") != null) {
				Integer originDuration = data.get("duration0");
				Integer destinationDuration = data.get("duration1");
				TextView originETA = findViewById(R.id.time_to_origin);
				TextView destinationETA = findViewById(R.id.time_to_destination);
				
				//Control ETA
				
				//Cannot parse int at first call as text is empty
				if (lastOriginValue != Integer.MAX_VALUE)
					lastOriginValue = Integer.parseInt(originETA.getText().toString());
				else lastOriginValue = originDuration;
				
				// Check if the van is very close to origin, set ETA to 0 so after it goes
				// through this point the app knows it.
				if (lastOriginValue != 0) {
					originETA.setText(originDuration.toString());
					Integer destinationFinalValue = originDuration + destinationDuration;
					destinationETA.setText(destinationFinalValue.toString());
				} else if (originDuration <= 2) {
					originETA.setText("0");
					destinationETA.setText(destinationDuration.toString());
				} else {
					Integer destinationFinalValue = destinationDuration - originDuration;
					destinationETA.setText(destinationFinalValue.toString());
				}
				//Show popup when van is arriving to final destination.
				Integer destinationFinalValue = Integer.parseInt(destinationETA.getText().toString());
				if (destinationFinalValue <= 4) {
					destinationETA.setText("0");
					new AlertDialog.Builder(
							MapsActivityUser.this)
							.setTitle(R.string.trip_end)
							.setMessage(R.string.arrive_destination_msg)
							.setCancelable(false)
							.setPositiveButton("OK", (dialog, which) -> {
								trip.setFinished();
								Intent intent = new Intent(MapsActivityUser.this,
										CentralActivity.class);
								startActivity(intent);
							})
							.show();
				}
			}
		}
	}
}
