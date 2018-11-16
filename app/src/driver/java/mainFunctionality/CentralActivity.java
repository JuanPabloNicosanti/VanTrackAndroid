package mainFunctionality;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.firebase.geofire.GeoFire;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mainFunctionality.driverTrips.ConfirmPassengersFragment;
import mainFunctionality.driverTrips.MyTripsFragment;
import mainFunctionality.moreOptions.MoreOptionsFragment;
import mainFunctionality.viewsModels.TripsViewModel;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.VanTrackApplication;
import utn.proy2k18.vantrack.exceptions.BackendConnectionException;
import utn.proy2k18.vantrack.exceptions.BackendException;
import utn.proy2k18.vantrack.models.PassengerReservation;
import utn.proy2k18.vantrack.viewModels.UsersViewModel;

public class CentralActivity extends AppCompatActivity implements MoreOptionsFragment.OnFragmentInteractionListener,
        MyTripsFragment.OnFragmentInteractionListener,
        ConfirmPassengersFragment.OnListFragmentInteractionListener {

    private FirebaseUser user;
    private GeoFire geoFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = (FirebaseUser) extras.get("user");
            ((VanTrackApplication) this.getApplication()).setUser(user);
        }
        else user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geofire/driver/");
        geoFire = new GeoFire(ref);
        setContentView(R.layout.activity_central_driver);
        final MyTripsFragment tripsFragment = new MyTripsFragment();
        final MoreOptionsFragment moreOptionsFragment = new MoreOptionsFragment();
        setFragment(tripsFragment);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_driver);
        mainFunctionality.BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.action_confirmTrips:
                        // Declared here as it waits for a specific trip, can't initialize it before loading MyTripsFragment
                        ConfirmPassengersFragment confirmFragment = ConfirmPassengersFragment.newInstance(
                                1, TripsViewModel.getInstance().getNextTrip(user.getEmail()));
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

    public FirebaseUser getUser() {
        return user;
    }

    public GeoFire getGeoFire() {
        return geoFire;
    }

    @Override
    public void onListFragmentInteraction(PassengerReservation item) {

    }

    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            startActivity(new Intent(CentralActivity.this, CentralActivity.class));
        } else {
            getFragmentManager().popBackStack();
        }

    }
}