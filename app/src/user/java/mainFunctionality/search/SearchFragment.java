package mainFunctionality.search;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import utn.proy2k18.vantrack.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private Button returnDateButton ;
    private Button reservationDateButton;
    private boolean lastSearchWasRoundtrip;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() { return new SearchFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        final RadioButton oneWayTripRB = view.findViewById(R.id.radio_one_way_trip);
        final RadioButton roundTripRB = view.findViewById(R.id.radio_round_trip);
        final AutoCompleteTextView origTextView = view.findViewById(R.id.autocomplete_origin);
        final AutoCompleteTextView destTextView = view.findViewById(R.id.autocomplete_destination);
        returnDateButton = view.findViewById(R.id.reservation_return_date);
        reservationDateButton = view.findViewById(R.id.reservation_date);
        final Button searchButton = view.findViewById(R.id.search_button);


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
                returnDateButton.setText("");
                lastSearchWasRoundtrip = true;
            }
        });

        final ArrayAdapter<String> origDestAdapter = new ArrayAdapter<>(
                container.getContext(), android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.origen_destino));


        origTextView.setAdapter(origDestAdapter);
        origTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                origTextView.showDropDown();
            }
        });

        destTextView.setAdapter(origDestAdapter);
        destTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                destTextView.showDropDown();
            }
        });



        reservationDateButton.setText("");
        reservationDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToDatePicker(reservationDateButton);
                reservationDateButton.setError(null);
            }
        });

        returnDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToDatePicker(returnDateButton);
                returnDateButton.setError(null);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validateText(origTextView,destTextView,reservationDateButton,returnDateButton)){
                    search_for_results(origTextView.getText().toString(),
                            destTextView.getText().toString(), reservationDateButton.getText().toString(),
                            returnDateButton.getText().toString());
                }
            }
        });

        return view;
    }

    public void search_for_results(String tripOrigin, String tripDest, String tripDate,
                                   String tripReturnDate) {
        String[] tdSplitted = tripDate.split("/");
        String dateFormatted = String.format("%s-%s-%s", tdSplitted[2],tdSplitted[1],tdSplitted[0]);

        String returnDateFormatted = tripReturnDate;
        if (!tripReturnDate.equals(getResources().getString(R.string.no_return_date))) {
            String[] trdSplitter = tripReturnDate.split("/");
            returnDateFormatted = String.format("%s-%s-%s", trdSplitter[2], trdSplitter[1],
                    trdSplitter[0]);
        }

        SearchResultsFragment searchResultsFragment = SearchResultsFragment.newInstance(tripOrigin,
                tripDest, dateFormatted, returnDateFormatted);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, searchResultsFragment);
        ft.commit();
    }

    public void goToDatePicker(final Button button){
        int day, month,year;
        final Calendar c = Calendar.getInstance();
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int yearSelected, int monthOfYearSelected, int dayOfMonthSelected) {
                button.setText(String.format(Locale.ENGLISH,"%02d/%02d/%02d",
                        dayOfMonthSelected, monthOfYearSelected + 1, yearSelected));
            }
        }, day, month, year);
        datePickerDialog.updateDate(year, month, day);
        datePickerDialog.show();
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
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


    boolean validateText(TextView origin, TextView destination, Button reservation_date , Button return_date) {

        if (origin.getText().toString().matches("") || destination.getText().toString().matches("")
                || reservation_date.getText().toString().matches("")  || return_date.getText().toString().matches("")) {

            if(origin.getText().toString().matches(""))
                origin.setError("Ingresar Origen");


            if(destination.getText().toString().matches(""))
                destination.setError("Ingresar Destino");


            if(reservation_date.getText().toString().matches(""))
                reservation_date.setError("Ingresar Fecha");


            if(lastSearchWasRoundtrip && return_date.getText().toString().matches(""))
                    return_date.setError("Ingresar Fecha");


            return false;

        }else
            return true;


    }
}
