package mainFunctionality;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.firebase.geofire.GeoFire;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import mainFunctionality.driverTrips.ConfirmPassengersFragment;
import mainFunctionality.driverTrips.MyTripsFragment;
import mainFunctionality.driverTrips.TripsToConfirmFragment;
import utn.proy2k18.vantrack.mainFunctionality.moreOptions.MoreOptionsFragment;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.models.Passenger;

public class CentralActivity extends AppCompatActivity implements MoreOptionsFragment.OnFragmentInteractionListener,
        MyTripsFragment.OnFragmentInteractionListener, TripsToConfirmFragment.OnFragmentInteractionListener,ConfirmPassengersFragment.OnListFragmentInteractionListener {

    private final HashMap<String, Fragment> fragments = new HashMap<>();
    private FirebaseUser user;
    private GeoFire geoFire;
    Fragment mContent = fragments.get("CONFIRM");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = (FirebaseUser) getIntent().getExtras().get("user");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geofire/driver/");
        geoFire = new GeoFire(ref);
        setContentView(R.layout.activity_central_driver);
        fragments.put("CONFIRM", new TripsToConfirmFragment());
        fragments.put("TRIPS", new MyTripsFragment());
        fragments.put("MORE", new MoreOptionsFragment());
        setFragment(fragments.get("TRIPS"));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_driver);
        mainFunctionality.BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.action_confirmTrips:
                        setFragment(fragments.get("CONFIRM"));
                        break;
                    case R.id.action_trips:
                        setFragment(fragments.get("TRIPS"));
                        break;
                    case R.id.action_more:
                        setFragment(fragments.get("MORE"));
                        break;
                }
                return false;
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
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
    public void onListFragmentInteraction(Passenger item) {

    }
}