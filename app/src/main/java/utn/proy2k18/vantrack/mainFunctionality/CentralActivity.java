package utn.proy2k18.vantrack.mainFunctionality;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;

import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.mainFunctionality.localization.MapsActivity;
import utn.proy2k18.vantrack.mainFunctionality.moreOptions.MoreOptionsFragment;
import utn.proy2k18.vantrack.mainFunctionality.reservations.MyReservationsFragment;
import utn.proy2k18.vantrack.mainFunctionality.search.SearchFragment;
import utn.proy2k18.vantrack.mainFunctionality.search.SearchResultsFragment;

public class CentralActivity extends AppCompatActivity implements SearchFragment.OnFragmentInteractionListener,
        MyReservationsFragment.OnFragmentInteractionListener, MoreOptionsFragment.OnFragmentInteractionListener,
        SearchResultsFragment.OnFragmentInteractionListener {

    /**No estoy seguro si es mejor, pero estabamos creando fragments siempre y no necesitamos tantos...*/
    private HashMap<String, Fragment> fragments = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central);
        fragments.put("SEARCH", new SearchFragment());
        fragments.put("TRIPS", new MyReservationsFragment());
        fragments.put("MORE", new MoreOptionsFragment());
        setFragment(fragments.get("TRIPS"));

        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
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

    //SETTINGS
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.commonmenus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.location:
                this.verifyGPSIsEnabledAndGetLocation();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void verifyGPSIsEnabledAndGetLocation(){
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            this.showGPSDisabledAlertToUser();
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            startActivity(new Intent(this, MapsActivity.class));
    }

    private void showGPSDisabledAlertToUser() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle(R.string.location_alert_title)
                .setMessage(R.string.location_alert_content)
                .setCancelable(false)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener(){
                            public void onClick(final DialogInterface dialog, final int id) {
                                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                .setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                dialog.cancel();
                            }
                        })
                .show();
    }

}
