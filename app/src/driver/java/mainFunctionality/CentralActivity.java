package mainFunctionality;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import mainFunctionality.moreOptions.MoreOptionsFragment;
import mainFunctionality.nextTrips.MyTripsFragment;
import utn.proy2k18.vantrack.R;

public class CentralActivity extends AppCompatActivity implements MoreOptionsFragment.OnFragmentInteractionListener,
        MyTripsFragment.OnFragmentInteractionListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central_driver);


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_driver);
        mainFunctionality.BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.action_confirmTrips:
                        //setFragment(new mainFunctionality.reservationsList.ReservationsListFragment());
                        //agregar en implements!!
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
    public void onFragmentInteraction(Uri uri) {

    }

    //SETTINGS



}


