package utn.proy2k18.vantrack.mainFunctionality;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import android.view.Menu;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.mainFunctionality.moreOptions.MoreOptionsFragment;
import utn.proy2k18.vantrack.mainFunctionality.reservations.MyTripsFragment;
import utn.proy2k18.vantrack.mainFunctionality.search.SearchFragment;

public class CentralActivity extends AppCompatActivity implements SearchFragment.OnFragmentInteractionListener,
        MyTripsFragment.OnFragmentInteractionListener, MoreOptionsFragment.OnFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central);


        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.action_search:
                        setFragment(new SearchFragment());
                        break;
                    case R.id.action_track:
                        Toast.makeText(CentralActivity.this,"Track my van!",Toast.LENGTH_SHORT).show();
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
            case R.id.settings_my_account:
                Toast.makeText(CentralActivity.this,"Mi cuenta!",Toast.LENGTH_SHORT).show();

                return true;
            case R.id.settings_notifications:
                setFragment(new MoreOptionsFragment());
                return true;
            case R.id.settings_help:
                Toast.makeText(CentralActivity.this,"Ayuda!",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.settings_close_session:
                Toast.makeText(CentralActivity.this,"Cerrar!",Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
