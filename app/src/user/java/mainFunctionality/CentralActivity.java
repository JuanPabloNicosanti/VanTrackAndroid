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

import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

import mainFunctionality.reservations.MyReservationsFragment;
import mainFunctionality.search.SearchFragment;
import mainFunctionality.search.SearchResultsFragment;
import mainFunctionality.notifications.NotificationFragment;
import mainFunctionality.moreOptions.MoreOptionsFragment;

import mainFunctionality.search.TripFragment;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.VanTrackApplication;


public class CentralActivity extends AppCompatActivity implements SearchFragment.OnFragmentInteractionListener,
        MyReservationsFragment.OnFragmentInteractionListener, MoreOptionsFragment.OnFragmentInteractionListener,
        SearchResultsFragment.OnFragmentInteractionListener {

    private final HashMap<String, Fragment> fragments = new HashMap<>();
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central_user);
        fragments.put("SEARCH", new SearchFragment());
        fragments.put("TRIPS", new MyReservationsFragment());
        fragments.put("MORE", new MoreOptionsFragment());
        Bundle extras = getIntent().getExtras();

        if (getIntent().getAction() != null &&
                getIntent().getAction().equals("OPEN_NOTIFICATIONS_FRAGMENT")) {
            setFragment(new NotificationFragment());
        } else {
            if (extras != null) {
                user = (FirebaseUser) extras.get("user");
                ((VanTrackApplication) this.getApplication()).setUser(user);
            }
            setFragment(fragments.get("TRIPS"));
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_search:
                        setFragment(fragments.get("SEARCH"));
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

    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (fragment instanceof TripFragment) {
            super.onBackPressed();
        }
          else
              startActivity(new Intent(CentralActivity.this, CentralActivity.class));
        }

    }
