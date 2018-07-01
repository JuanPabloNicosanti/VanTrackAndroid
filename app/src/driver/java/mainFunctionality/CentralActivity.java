package mainFunctionality;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import mainFunctionality.driverTrips.TripsToConfirmFragment;
import mainFunctionality.moreOptions.MoreOptionsFragment;
import mainFunctionality.driverTrips.MyTripsFragment;
import mainFunctionality.notifications.NotificationFragment;
import utn.proy2k18.vantrack.R;

public class CentralActivity extends AppCompatActivity implements MoreOptionsFragment.OnFragmentInteractionListener,
        MyTripsFragment.OnFragmentInteractionListener, TripsToConfirmFragment.OnFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central_driver);
        if (getIntent().getAction() != null &&
                getIntent().getAction().equals("OPEN_NOTIFICATIONS_FRAGMENT")) {
            Bundle extras = getIntent().getExtras();
            String notificationTitle;
            String notificationMessage;

            if (extras != null) {
                notificationTitle = extras.getString("notificationTitle");
                notificationMessage = extras.getString("notificationMessage");
            } else {
                notificationTitle = "NO_NOTIFICATION";
                notificationMessage = "NO_NOTIFICATION";
            }

            setFragment(NotificationFragment.newInstance(notificationTitle, notificationMessage));
        } else {
            setFragment(new MyTripsFragment());
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_driver);
        mainFunctionality.BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.action_confirmTrips:
                        setFragment(new TripsToConfirmFragment());
                        break;
                    case R.id.action_trips:
                        setFragment(new MyTripsFragment());
                        break;
                    case R.id.action_more:
                        setFragment(new MoreOptionsFragment());
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
}