package mainFunctionality.localization;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;

import android.os.Looper;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import mainFunctionality.CentralActivity;
import mainFunctionality.viewsModels.TripsViewModel;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.exceptions.BackendConnectionException;
import utn.proy2k18.vantrack.exceptions.BackendException;
public class MapsActivityDriver extends FragmentActivity implements OnMapReadyCallback {
	
	private GoogleMap map;
	private FusedLocationProviderClient googleClient;
	private LocationCallback locationCallback;
	private LocationRequest locationRequest;
	private DatabaseReference driverLocation;
	private FirebaseUser user;
	
	public static String tripId = "-1";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps_driver);
		
		googleClient = LocationServices.getFusedLocationProviderClient(this);
		
		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		locationRequest.setInterval(10 * 1000);
		
		locationCallback = new LocationCallback() {
			@Override
			public void onLocationResult(LocationResult locationResult) {
				if (locationResult == null) {
					return;
				}
				updateLocation(locationResult.getLastLocation());
			}
		};
		
		final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
				.findViewById(android.R.id.content)).getChildAt(0);
		
		user = FirebaseAuth.getInstance().getCurrentUser();
		
		//Assign all references in database
		DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
		
		Bundle parameters = getIntent().getExtras();
		if (parameters != null) {
			tripId = String.valueOf(parameters.getInt("tripId"));
		}
		driverLocation = mDatabase.child("Trips").child(tripId).child("Drivers").child("DriverLocationInMap");
		driverLocation.child("latitude").setValue(0.0);
		driverLocation.child("longitude").setValue(0.0);
		
		// Start location service
		Intent locationIntent = new Intent(this, MyLocationService.class);
		startService(locationIntent);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			checkLocationPermission();
		}
		
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
		
		// Initialize end trip button
		TripsViewModel viewModel = TripsViewModel.getInstance();
		
		Button endTrip = viewGroup.findViewById(R.id.btn_end_trip);
		
		endTrip.setOnClickListener(v -> {
			AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivityDriver.this);
			builder.setMessage("Desea finalizar el viaje?")
					.setPositiveButton("Si", (dialog, position1) -> {
						try {
							viewModel.endTrip(user.getEmail(), tripId);
							
							driverLocation.removeValue();
							
							stopService(locationIntent);
							
							Intent intent = new Intent(MapsActivityDriver.this, CentralActivity.class);
							finish();
							startActivity(intent);
							
						} catch (BackendException | BackendConnectionException be) {
							dialog.dismiss();
							showErrorDialog(this, be.getMessage());
						}
					})
					.setNegativeButton("No", null);
			AlertDialog alert = builder.create();
			alert.show();
		});
	}
	
	public void showErrorDialog(Activity activity, String message) {
		AlertDialog alertDialog = new AlertDialog.Builder(activity)
				.setMessage(message)
				.setNeutralButton("Aceptar", null)
				.create();
		alertDialog.show();
	}
	
	@Override
	public void onPause() {
		super.onPause();
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
		map = googleMap;
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		
		//Initialize Google Play Services
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(this,
					Manifest.permission.ACCESS_FINE_LOCATION)
					== PackageManager.PERMISSION_GRANTED) {
				map.setMyLocationEnabled(true);
			} else {
				checkLocationPermission();
			}
		} else {
			map.setMyLocationEnabled(true);
		}
	}
	
	public void updateLocation(Location location) {
		
		Map<String, Object> latLng = new HashMap<String, Object>();
		latLng.put("latitude", location.getLatitude());
		latLng.put("longitude", location.getLongitude());
		
		driverLocation.updateChildren(latLng);
		
		LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
		
		map.moveCamera(CameraUpdateFactory.newLatLng(coordinates));
		map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
				.target(coordinates).tilt(30)
				.zoom(15)
				.build()));
	}
	
	public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
	
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
							map.setMyLocationEnabled(true);
						}
					});
				}
			} else {
				Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
}