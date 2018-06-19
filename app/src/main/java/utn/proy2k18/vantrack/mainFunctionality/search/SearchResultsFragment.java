package utn.proy2k18.vantrack.mainFunctionality.search;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.arch.lifecycle.ViewModelProviders;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.mainFunctionality.TripsReservationsViewModel;
import utn.proy2k18.vantrack.mainFunctionality.reservations.Reservation;
import utn.proy2k18.vantrack.mainFunctionality.reservations.TestReservations;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchResultsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "tripOrigin";
    private static final String ARG_PARAM2 = "tripDestination";
    private static final String ARG_PARAM3 = "tripDate";
    private static final String ARG_PARAM4 = "tripReturnDate";

    private RecyclerView mRecyclerView;
    private TripsAdapter tripsAdapter;
    private OnFragmentInteractionListener mListener;
    private TripsReservationsViewModel model;

    private List<Trip> baseFilteredTrips;
    private List<Trip> tripsFilteredByCompany;
    private List<Trip> tripsFilteredByTime;
    private List<Trip> tripsFiltered;
    private Spinner sortOptionsSpinner;
    private String argTripOrigin;
    private String argTripDestination;
    private String argTripReturnDate;

    public SearchResultsFragment() {
        // Required empty public constructor
    }

// TODO: Cambiar el feed de datos. Historial?

    public static SearchResultsFragment newInstance(String tripOrigin, String tripDest,
                                                    String tripDate, String tripReturnDate) {
        SearchResultsFragment searchResultsFragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putString("tripOrigin", tripOrigin);
        args.putString("tripDestination", tripDest);
        args.putString("tripDate", tripDate);
        args.putString("tripReturnDate", tripReturnDate);
        searchResultsFragment.setArguments(args);

        return searchResultsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(getActivity()).get(TripsReservationsViewModel.class);

        argTripOrigin = getArguments().getString(ARG_PARAM1, "");
        argTripDestination = getArguments().getString(ARG_PARAM2, "");
        final String argTripDate = getArguments().getString(ARG_PARAM3, "");
        argTripReturnDate = getArguments().getString(ARG_PARAM4, "");

        final List<Trip> baseTrips = (new TestTrips()).getTestTrips();
        baseFilteredTrips = filterTrips(baseTrips, argTripOrigin, argTripDestination, argTripDate);
        tripsFilteredByCompany = baseFilteredTrips;
        tripsFilteredByTime = baseFilteredTrips;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);
        mRecyclerView = view.findViewById(R.id.search_results_view);

        final RecyclerView.LayoutManager mLayoutManager = new
                GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        tripsAdapter = new TripsAdapter(baseFilteredTrips, mRecyclerView);
        mRecyclerView.setAdapter(tripsAdapter);

        final Spinner filterByCompanySpinner = view.findViewById(R.id.company_filter_spinner);
        sortOptionsSpinner = view.findViewById(R.id.sorting_options_spinner);
        final int tripsMinTime = getTripsMinTime(baseFilteredTrips);
        final int tripsMaxTime = getTripsMaxTime(baseFilteredTrips);
        final RangeSeekBar<Integer> tripsTimeRangeSeekBar = view.findViewById(R.id.trips_time_range_seek_bar);
        final Button searchReturnTripsButton = view.findViewById(R.id.next_search_button);
        final Button bookTripButton = view.findViewById(R.id.book_trip_button);

        if (!argTripReturnDate.equals("")) {
            searchReturnTripsButton.setVisibility(View.VISIBLE);
        } else {
            bookTripButton.setVisibility(View.VISIBLE);
        }

        ArrayAdapter<CharSequence> filterByCompanyAdapter = ArrayAdapter.createFromResource(container.getContext(),
                R.array.companies, android.R.layout.simple_spinner_item);
        filterByCompanyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterByCompanySpinner.setAdapter(filterByCompanyAdapter);

        filterByCompanySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id) {
                String field = parent.getItemAtPosition(position).toString();

                if (field.equals("Seleccionar empresa")) {
                    tripsFilteredByCompany = baseFilteredTrips;
                } else {
                    tripsFilteredByCompany = filterTripsByCompany(baseFilteredTrips, field);
                }
                setNewAdapter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        ArrayAdapter<CharSequence> sortOptionsAdapter = ArrayAdapter.createFromResource(container.getContext(),
                R.array.sorting_options, android.R.layout.simple_spinner_item);
        sortOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortOptionsSpinner.setAdapter(sortOptionsAdapter);

        sortOptionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id) {
                String field = parent.getItemAtPosition(position).toString();
                sortTrips(field, tripsFiltered);
                tripsAdapter = new TripsAdapter(tripsFiltered, mRecyclerView);
                mRecyclerView.setAdapter(tripsAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        tripsTimeRangeSeekBar.setRangeValues(tripsMinTime, tripsMaxTime);
        tripsTimeRangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue,
                                                    Integer maxValue) {
                Toast.makeText(getContext(), minValue + "-" + maxValue, Toast.LENGTH_LONG).show();
                tripsFilteredByTime = filterTripsByTime(baseFilteredTrips, minValue, maxValue);
                setNewAdapter();
            }
        });
        tripsTimeRangeSeekBar.setNotifyWhileDragging(true);

        searchReturnTripsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reservation reservation = new Reservation(new Date(), tripsAdapter.getSelectedTrip());
                model.addReservation(reservation);
                search_for_results(argTripDestination, argTripOrigin, argTripReturnDate, "");
            }
        });

        bookTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reservation reservation = new Reservation(new Date(), tripsAdapter.getSelectedTrip());
                model.addReservation(reservation);
                Toast.makeText(mRecyclerView.getContext(), R.string.trip_booked, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public void search_for_results(String tripOrigin, String tripDest, String tripDate,
                                   String tripReturnDate) {
        SearchResultsFragment searchResultsFragment = SearchResultsFragment.newInstance(tripOrigin,
                tripDest, tripDate, tripReturnDate);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, searchResultsFragment);
        ft.commit();
    }

    private void setNewAdapter() {
        tripsFiltered = intersection(tripsFilteredByCompany, tripsFilteredByTime);
        sortTrips(sortOptionsSpinner.getSelectedItem().toString(), tripsFiltered);
        tripsAdapter = new TripsAdapter(tripsFiltered, mRecyclerView);
        mRecyclerView.setAdapter(tripsAdapter);
    }

    public <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();

        for (T t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }

    private List<Trip> filterTripsByCompany(List<Trip> trips, String companyName) {
        List<Trip> filteredTrips = new ArrayList<>();

        for (Trip trip : trips) {
            if (trip.getCompanyName().equals(companyName)) {
                filteredTrips.add(trip);
            }
        }
        return filteredTrips;
    }

    private void sortTrips(String field, List<Trip> trips) {
        switch (field) {
            case "Precio":
                Collections.sort(trips, new Comparator<Trip>() {
                    @Override
                    public int compare(final Trip t1, final Trip t2) {
                        return (int)(t1.getPrice() - t2.getPrice());
                    }
                });
                break;

            case "Calificacion":
                Collections.sort(trips, new Comparator<Trip>() {
                    @Override
                    public int compare(final Trip t1, final Trip t2) {
                        return Double.compare(t2.getCompanyCalification(),
                                t1.getCompanyCalification());
                    }
                });
                break;

            case "Seleccione campo":
                break;
        }
    }

    private List<Trip> filterTripsByTime(List<Trip> trips, int minValue, int maxValue) {
        List<Trip> filteredTrips = new ArrayList<>();

        for(Trip trip : trips){
            if (trip.getTimeHour() >= minValue && trip.getTimeHour() <= maxValue) {
                filteredTrips.add(trip);
            }
        }
        return filteredTrips;
    }

    private int getTripsMaxTime(List<Trip> trips) {
        int maxValue = 0;
        for(Trip trip : trips) {
            if(trip.getTimeHour() > maxValue) {
                maxValue = trip.getTimeHour();
            }
        }
        return maxValue;
    }

    private int getTripsMinTime(List<Trip> trips) {
        int minValue = 24;
        for(Trip trip : trips) {
            if(trip.getTimeHour() < minValue) {
                minValue = trip.getTimeHour();
            }
        }
        return minValue;
    }

    private List<Trip> filterTrips(List<Trip> baseTrips, String argTripOrigin,
                                   String argTripDestination, String argTripDate) {
        List<Trip> filteredTrips = new ArrayList<>();

        for(Trip trip : baseTrips){
            if (trip.getDestination().equals(argTripDestination) &&
                    trip.getOrigin().equals(argTripOrigin) &&
                    trip.getFormattedDate().equals(argTripDate)) {
                filteredTrips.add(trip);
            }
        }

        return filteredTrips;
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
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = null;
        if (activity != null) {
            actionBar = activity.getSupportActionBar();
        }
        if (actionBar != null) {
            actionBar.setTitle(R.string.search_results);
        }
    }
}

