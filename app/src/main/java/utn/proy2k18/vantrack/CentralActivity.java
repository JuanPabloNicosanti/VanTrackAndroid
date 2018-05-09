package utn.proy2k18.vantrack;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CentralActivity extends AppCompatActivity implements BuscarFragment.OnFragmentInteractionListener, MyTripsFragment.OnFragmentInteractionListener {


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
                        setFragment(new BuscarFragment());
                        break;
                    case R.id.action_track:
                        Toast.makeText(CentralActivity.this,"Track my van!",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_trips:
                        setFragment(new MyTripsFragment());
                        break;
                    case R.id.action_more:
                        Toast.makeText(CentralActivity.this,"Ver mas",Toast.LENGTH_SHORT).show();
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
}
