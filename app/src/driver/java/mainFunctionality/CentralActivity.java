package mainFunctionality;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;

import com.firebase.geofire.GeoFire;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mainFunctionality.driverTrips.ConfirmPassengersFragment;
import mainFunctionality.driverTrips.MyTripsFragment;
import mainFunctionality.driverTrips.TripFragment;
import mainFunctionality.moreOptions.MoreOptionsFragment;
import mainFunctionality.viewsModels.TripsViewModel;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.models.PassengerReservation;


public class CentralActivity extends AppCompatActivity implements MoreOptionsFragment.OnFragmentInteractionListener,
        MyTripsFragment.OnFragmentInteractionListener,
        ConfirmPassengersFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geofire/driver/");
        GeoFire geoFire = new GeoFire(ref);
        setContentView(R.layout.activity_central_driver);
        final MyTripsFragment tripsFragment = new MyTripsFragment();
        final MoreOptionsFragment moreOptionsFragment = new MoreOptionsFragment();
        setFragment(tripsFragment);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_driver);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.action_confirmTrips:
                        // Declared here as it waits for a specific trip, can't initialize it before loading MyTripsFragment
                        ConfirmPassengersFragment confirmFragment = ConfirmPassengersFragment.newInstance(
                                1, TripsViewModel.getInstance().getNextTrip(currentUser.getEmail()));
                        setFragment(confirmFragment);
                        break;
                    case R.id.action_trips:
                        setFragment(tripsFragment);
                        break;
                    case R.id.action_more:
                        setFragment(moreOptionsFragment);
                        break;
                }
                return false;
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) { }

    @Override
    public void onListFragmentInteraction(PassengerReservation item) {

    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof TripFragment)
            super.onBackPressed();
        else
            startActivity(new Intent(CentralActivity.this, CentralActivity.class));
    }
}