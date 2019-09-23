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

import java.util.HashMap;

import mainFunctionality.reservations.MyReservationsFragment;
import mainFunctionality.search.SearchFragment;
import mainFunctionality.search.SearchResultsFragment;
import mainFunctionality.notifications.NotificationFragment;
import mainFunctionality.moreOptions.MoreOptionsFragment;

import mainFunctionality.search.TripFragment;
import utn.proy2k18.vantrack.R;


public class CentralActivity extends AppCompatActivity implements SearchFragment.OnFragmentInteractionListener,
        MyReservationsFragment.OnFragmentInteractionListener, MoreOptionsFragment.OnFragmentInteractionListener,
        SearchResultsFragment.OnFragmentInteractionListener {

    private final HashMap<String, Fragment> fragments = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central_user);
        fragments.put("SEARCH", new SearchFragment());
        fragments.put("TRIPS", new MyReservationsFragment());
        fragments.put("MORE", new MoreOptionsFragment());

        if (getIntent().getAction() != null &&
                getIntent().getAction().equals("OPEN_NOTIFICATIONS_FRAGMENT")) {
            setFragment(NotificationFragment.newInstance(true));
        } else {
            setFragment(fragments.get("TRIPS"));
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
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

    private void setFragment(Fragment fragment) {
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
        if (fragment instanceof TripFragment)
            super.onBackPressed();
        else
            startActivity(new Intent(CentralActivity.this, CentralActivity.class));
    }
}
