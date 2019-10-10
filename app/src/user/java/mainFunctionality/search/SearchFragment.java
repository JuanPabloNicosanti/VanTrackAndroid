package mainFunctionality.search;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import mainFunctionality.viewsModels.TripsViewModel;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.exceptions.FailedToGetTripStopsException;
import utn.proy2k18.vantrack.exceptions.FailedToGetTripsException;
import utn.proy2k18.vantrack.exceptions.NoReturnTripsException;
import utn.proy2k18.vantrack.exceptions.NoTripsException;
import utn.proy2k18.vantrack.utils.DateTimePicker;
import utn.proy2k18.vantrack.viewModels.UsersViewModel;

public class SearchFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Button returnDateButton ;
    private Button reservationDateButton;
    private RadioButton roundTripRB;
    private TripsViewModel tripsModel;
    private UsersViewModel usersModel = UsersViewModel.getInstance();
    private boolean lastSearchWasRoundtrip;
    private List<String> stops;
    private FirebaseUser user;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() { return new SearchFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tripsModel = TripsViewModel.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        final RadioButton oneWayTripRB = view.findViewById(R.id.radio_one_way_trip);
        roundTripRB = view.findViewById(R.id.radio_round_trip);
        final AutoCompleteTextView origTextView = view.findViewById(R.id.autocomplete_origin);
        final AutoCompleteTextView destTextView = view.findViewById(R.id.autocomplete_destination);
        returnDateButton = view.findViewById(R.id.reservation_return_date);
        reservationDateButton = view.findViewById(R.id.reservation_date);
        final Button searchButton = view.findViewById(R.id.search_button);
        final DateTimePicker dateTimePicker = new DateTimePicker(getActivity());

        try {
            stops = tripsModel.getAllStops();
        } catch (FailedToGetTripStopsException fgtse) {
            showErrorDialog(getActivity(), fgtse.getMessage());
        }

        returnDateButton.setText(getResources().getString(R.string.no_return_date));
        oneWayTripRB.setChecked(true);
        oneWayTripRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                returnDateButton.setEnabled(false);
                returnDateButton.setError(null);
                returnDateButton.setText(getResources().getString(R.string.no_return_date));
                lastSearchWasRoundtrip = false;
            }
        });
        roundTripRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                returnDateButton.setEnabled(true);
                returnDateButton.setText(reservationDateButton.getText());
                lastSearchWasRoundtrip = true;
            }
        });

        final ArrayAdapter<String> origDestAdapter = new ArrayAdapter<>(
                container.getContext(), android.R.layout.simple_dropdown_item_1line, stops);

        origTextView.setAdapter(origDestAdapter);
        origTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                origTextView.showDropDown();
            }
        });

        origTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                {
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(arg1.getApplicationWindowToken(), 0);
                }
            }
        });

        destTextView.setAdapter(origDestAdapter);
        destTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                destTextView.showDropDown();
            }
        });

        destTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                {
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(arg1.getApplicationWindowToken(), 0);
                }
            }
        });

        reservationDateButton.setText("");
        reservationDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dateTimePicker.pickDate(reservationDateButton);
                reservationDateButton.setError(null);
                if(roundTripRB.isChecked() && returnDateButton.getText().equals("")) {
                    returnDateButton.setText(reservationDateButton.getText());
                }
            }
        });
        returnDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dateTimePicker.pickDate(returnDateButton);
                returnDateButton.setError(null);
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateText(origTextView, destTextView, reservationDateButton, returnDateButton)){
                    search_for_results(origTextView.getText().toString(),
                            destTextView.getText().toString(),
                            reservationDateButton.getText().toString(),
                            returnDateButton.getText().toString());
                }
            }
        });

        return view;
    }

    public void search_for_results(final String tripOrigin, final String tripDest,
                                   final String tripDate, final String tripReturnDate) {
        final String returnDate = roundTripRB.isChecked() ? tripReturnDate : null;
        try {
            if (usersModel.userHasChargePenalty(user.getEmail())) {
                showErrorDialog(getActivity(), getResources().getString(
                        R.string.extra_charge_message));
            }
            tripsModel.fetchTrips(tripOrigin, tripDest, tripDate, returnDate);
            setFragment(SearchResultsFragment.newInstance(false));
        } catch (FailedToGetTripsException fgte) {
            showErrorDialog(getActivity(), fgte.getMessage());
        } catch (NoTripsException nte) {
            showErrorDialog(getActivity(), nte.getMessage());
        } catch (NoReturnTripsException nrte) {
            if (roundTripRB.isChecked()) {
                showErrorDialog(getActivity(), nrte.getMessage());
            }
            setFragment(SearchResultsFragment.newInstance(false));
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void showErrorDialog(Activity activity, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setMessage(message)
                .setNeutralButton("Aceptar",null)
                .create();
        alertDialog.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = null;
        if (activity != null) {
            actionBar = activity.getSupportActionBar();
        }
        if (actionBar != null) {
            actionBar.setTitle(R.string.search);
        }
        if(lastSearchWasRoundtrip){
            returnDateButton.setEnabled(true);
            returnDateButton.setText("");
        }
    }


    boolean validateText(TextView origin, TextView destination, Button reservation_date,
                         Button return_date) {
        if (origin.getText().toString().isEmpty() ||
                destination.getText().toString().isEmpty() ||
                reservation_date.getText().toString().isEmpty()  ||
                return_date.getText().toString().isEmpty()) {

            if(origin.getText().toString().isEmpty())
                origin.setError("Ingresar Origen");

            if(destination.getText().toString().isEmpty())
                destination.setError("Ingresar Destino");

            if(reservation_date.getText().toString().isEmpty())
                reservation_date.setError("Ingresar Fecha");

            if(return_date.isEnabled() && return_date.getText().toString().isEmpty())
                return_date.setError("Ingresar Fecha");

            return false;
        } else if(!return_date.getText().toString().equals(getResources().getString(
                R.string.no_return_date)) && returnDateOlderThanDeparture(reservation_date,
                return_date)) {
            return_date.setError("La fecha de vuelta no puede ser menor a la de ida");
            return false;
        }
        return true;
    }

    boolean returnDateOlderThanDeparture(Button departureDateButton, Button returnDateButton){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd-MM-yyyy");
        LocalDate returnDate = LocalDate.parse(returnDateButton.getText().toString(), dateTimeFormatter);
        LocalDate reservationDate = LocalDate.parse(departureDateButton.getText().toString(), dateTimeFormatter);
        return returnDate.isBefore(reservationDate);
    }

}
