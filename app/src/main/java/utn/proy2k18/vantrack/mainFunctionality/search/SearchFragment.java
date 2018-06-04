package utn.proy2k18.vantrack.mainFunctionality.search;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
    private Button reservationDate;
    private Button reservationHour;

    private OnFragmentInteractionListener mListener;

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

        final ArrayAdapter<String> orig_dest_adapter = new ArrayAdapter<String>(
                container.getContext(), android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.origen_destino));

        final AutoCompleteTextView origTextView = view.findViewById(R.id.autocomplete_origin);
        origTextView.setAdapter(orig_dest_adapter);
        origTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                origTextView.showDropDown();
            }
        });

        final AutoCompleteTextView destTextView = view.findViewById(R.id.autocomplete_destination);
        destTextView.setAdapter(orig_dest_adapter);
        destTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                destTextView.showDropDown();
            }
        });

        reservationDate = view.findViewById(R.id.reservation_date);
        reservationDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToDatePicker();
            }
        });

        reservationHour = view.findViewById(R.id.reservation_hour);
        reservationHour.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToHourPicker();
            }
        });

        Button searchButton = (Button) view.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_for_results(origTextView.getText().toString(),
                        destTextView.getText().toString(), reservationDate.getText().toString());
            }
        });

        return view;
    }

    private void search_for_results(String tripOrigin, String tripDest, String tripDate) {
        AdvancedSearchFragment advancedSearchFragment = new AdvancedSearchFragment();
        Bundle args = new Bundle();
        args.putString("tripOrigin", tripOrigin);
        args.putString("tripDestination", tripDest);
        args.putString("tripDate", tripDate);
        advancedSearchFragment.setArguments(args);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, advancedSearchFragment);
        ft.commit();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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


    public void goToDatePicker(){
        int day, month,year;
        final Calendar c = Calendar.getInstance();
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int yearSelected, int monthOfYearSelected, int dayOfMonthSelected) {
                reservationDate.setText(String.format(Locale.getDefault(),"%02d/%02d/%02d",
                        dayOfMonthSelected, monthOfYearSelected + 1, yearSelected));
            }
        }, day, month, year);
        datePickerDialog.updateDate(year, month, day);
        datePickerDialog.show();
    }

    public void goToHourPicker(){
        int hour,minutes;
        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minutes = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourSelected, int minutesSelected) {
                reservationHour.setText(String.format(Locale.getDefault(),"%d:%d", hourSelected, minutesSelected));
            }
        },hour,minutes,true);
        timePickerDialog.updateTime(hour,minutes);
        timePickerDialog.show();
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
    }
}
