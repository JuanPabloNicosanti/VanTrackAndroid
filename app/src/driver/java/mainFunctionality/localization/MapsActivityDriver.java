package mainFunctionality.localization;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import mainFunctionality.CentralActivity;
import mainFunctionality.viewsModels.TripsViewModel;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.exceptions.FailedToEndTripException;

public class MapsActivityDriver extends FragmentActivity implements OnMapReadyCallback {
	
	private GoogleMap map;
	private FusedLocationProviderClient googleClient;
	private FirebaseUser user;
	
	public static String tripId = "-1";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps_driver);
		
		final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
			.findViewById(android.R.id.content)).getChildAt(0);
		
		user = FirebaseAuth.getInstance().getCurrentUser();
		
		Bundle parameters = getIntent().getExtras();
		if (parameters != null) {
			tripId = String.valueOf(parameters.getInt("tripId"));
		}
		
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
						
						stopService(locationIntent);
						
						Intent intent = new Intent(MapsActivityDriver.this, CentralActivity.class);
						finish();
						startActivity(intent);
						
					} catch (FailedToEndTripException fe) {
						dialog.dismiss();
						showErrorDialog(this, fe.getMessage());
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
	}
	
	@Override
	public void onMapReady(GoogleMap googleMap) {
		map = googleMap;
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		
		googleClient = LocationServices.getFusedLocationProviderClient(this);
		
		//Initialize Google Play Services
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(this,
			Manifest.permission.ACCESS_FINE_LOCATION)
			== PackageManager.PERMISSION_GRANTED ) {
				googleClient.getLastLocation().addOnSuccessListener(this, location -> {
					if (location != null) {
						LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
						
						map.setMyLocationEnabled(true);
						map.moveCamera(CameraUpdateFactory.newLatLng(coordinates));
						map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
							.target(coordinates).tilt(30)
							.zoom(15)
							.build()));
					}
				});
			} else {
				checkLocationPermission();
			}
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
					map.setMyLocationEnabled(true);
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