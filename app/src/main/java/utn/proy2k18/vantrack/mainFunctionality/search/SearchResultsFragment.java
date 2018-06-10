package utn.proy2k18.vantrack.mainFunctionality.search;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import utn.proy2k18.vantrack.R;

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

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private OnFragmentInteractionListener mListener;

    public SearchResultsFragment() {
        // Required empty public constructor
    }

// TODO: Cambiar el feed de datos. Historial?

    public static SearchResultsFragment newInstance() { return new SearchResultsFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String argTripOrigin = getArguments().getString(ARG_PARAM1);
        String argTripDestination = getArguments().getString(ARG_PARAM2);
        String argTripDate = getArguments().getString(ARG_PARAM3);

        View view = inflater.inflate(R.layout.fragment_search_results, container, false);
        mRecyclerView = view.findViewById(R.id.search_results_view);

        mLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        final List<Trip> baseTrips = createTripsList();
        final List<Trip> filteredTrips = filterTrips(baseTrips, argTripOrigin, argTripDestination,
                argTripDate);

        mAdapter = new TripsAdapter(filteredTrips);
        mRecyclerView.setAdapter(mAdapter);

        Spinner sortOptionsSpinner = (Spinner) view.findViewById(R.id.sorting_options_spinner);
        ArrayAdapter<CharSequence> sortOptionsAdapter = ArrayAdapter.createFromResource(container.getContext(),
                R.array.sorting_options, android.R.layout.simple_spinner_item);
        sortOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortOptionsSpinner.setAdapter(sortOptionsAdapter);

        sortOptionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id) {
                String field = parent.getItemAtPosition(position).toString();
                sortFilteredTrips(field, filteredTrips);
                mAdapter = new TripsAdapter(filteredTrips);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        Spinner filterByCompanySpinner = (Spinner) view.findViewById(R.id.company_filter_spinner);
        ArrayAdapter<CharSequence> filterByCompanyAdapter = ArrayAdapter.createFromResource(container.getContext(),
                R.array.companies, android.R.layout.simple_spinner_item);
        filterByCompanyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterByCompanySpinner.setAdapter(filterByCompanyAdapter);

        filterByCompanySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id) {
                String field = parent.getItemAtPosition(position).toString();
                List<Trip> tripsFilteredByCompany;

                if (field.equals("Seleccionar empresa")) {
                    tripsFilteredByCompany = filteredTrips;
                } else {
                    tripsFilteredByCompany = filterTripsByCompany(filteredTrips, field);
                }

                mAdapter = new TripsAdapter(tripsFilteredByCompany);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        final RangeSeekBar<Integer> tripsTimeRangeSeekBar = view.findViewById(R.id.trips_time_range_seek_bar);
        tripsTimeRangeSeekBar.setRangeValues(getTripsMinTime(filteredTrips), getTripsMaxTime(filteredTrips));

        tripsTimeRangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue,
                                                    Integer maxValue) {
                Toast.makeText(getContext(), minValue + "-" + maxValue, Toast.LENGTH_LONG).show();
                List<Trip> filteredTripsByTime = filterTripsByTime(filteredTrips, minValue, maxValue);
                System.out.println(filteredTripsByTime.size());
                mAdapter = new TripsAdapter(filteredTripsByTime);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
        tripsTimeRangeSeekBar.setNotifyWhileDragging(true);

        return view;
    }

    private void sortFilteredTrips(String field, List<Trip> filteredTrips) {
        switch (field) {
            case "Precio (de menor a mayor)":
                Collections.sort(filteredTrips, new Comparator<Trip>() {
                    @Override
                    public int compare(final Trip t1, final Trip t2) {
                        return (int)(t1.getPrice() - t2.getPrice());
                    }
                });
                break;

            case "Precio (de mayor a menor)":
                Collections.sort(filteredTrips, new Comparator<Trip>() {
                    @Override
                    public int compare(final Trip t1, final Trip t2) {
                        return (int)(t2.getPrice() - t1.getPrice());
                    }
                });
                break;

            case "Seleccione campo":
                break;
        }
    }

    private List<Trip> filterTripsByCompany(List<Trip> baseTrips, String companyName) {
        List<Trip> filteredTrips = new ArrayList<Trip>();

        for (Trip trip : baseTrips) {
            if (trip.getCompany().equals(companyName)) {
                filteredTrips.add(trip);
            }
        }
        return filteredTrips;
    }

    private List<Trip> filterTripsByTime(List<Trip> trips, int minValue, int maxValue) {
        List<Trip> filteredTrips = new ArrayList<Trip>();

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
        List<Trip> filteredTrips = new ArrayList<Trip>();

        for(Trip trip : baseTrips){
            if (trip.getDestination().equals(argTripDestination) &&
                    trip.getOrigin().equals(argTripOrigin) &&
                    trip.getFormattedDate().equals(argTripDate)) {
                filteredTrips.add(trip);
            }
        }
        return filteredTrips;
    }

    public List<Trip> createTripsList() {
        final List<Trip> trips = new ArrayList<Trip>();
        trips.add(new Trip("La Medalla", new Date(new Date().getTime() + 2 * 3600*1000), "Echeverria del Lago", "Terminal Obelisco", 120));
        trips.add(new Trip("La Medalla", new Date(), "Terminal Obelisco", "Echeverria del Lago", 120));
        trips.add(new Trip("Merco Bus", new Date(new Date().getTime() + 2 * 3600*1000), "Echeverria del Lago", "Terminal Obelisco", 115));
        trips.add(new Trip("Merco Bus", new Date(), "Terminal Obelisco", "Echeverria del Lago", 115));
        trips.add(new Trip("La Medalla", new Date(), "Echeverria del Lago", "Terminal Obelisco", 120));
        trips.add(new Trip("La Medalla", new Date(), "Terminal Obelisco", "Echeverria del Lago", 120));
        trips.add(new Trip("Merco Bus", new Date(), "Echeverria del Lago", "Terminal Obelisco", 115));
        trips.add(new Trip("Merco Bus", new Date(), "Terminal Obelisco", "Echeverria del Lago", 115));
        trips.add(new Trip("La Medalla", new Date(new Date().getTime() + 4 * 3600*1000), "Echeverria del Lago", "Terminal Obelisco", 120));
        trips.add(new Trip("La Medalla", new Date(), "Terminal Obelisco", "Echeverria del Lago", 120));
        trips.add(new Trip("Merco Bus", new Date(new Date().getTime() + 4 * 3600*1000), "Echeverria del Lago", "Terminal Obelisco", 115));
        trips.add(new Trip("Merco Bus", new Date(), "Terminal Obelisco", "Echeverria del Lago", 115));
        trips.add(new Trip("La Medalla", new Date(new Date().getTime() - 2 * 3600*1000), "Echeverria del Lago", "Terminal Obelisco", 120));
        trips.add(new Trip("La Medalla", new Date(), "Terminal Obelisco", "Echeverria del Lago", 120));
        trips.add(new Trip("Merco Bus", new Date(new Date().getTime() - 2 * 3600*1000), "Echeverria del Lago", "Terminal Obelisco", 115));
        trips.add(new Trip("Merco Bus", new Date(), "Terminal Obelisco", "Echeverria del Lago", 115));
        trips.add(new Trip("La Medalla", new Date(), "Campos de Echeverria", "Terminal Obelisco", 110));
        trips.add(new Trip("La Medalla", new Date(), "Terminal Obelisco", "Campos de Echeverria", 110));
        trips.add(new Trip("Merco Bus", new Date(), "Malibu", "Terminal Obelisco", 120));
        trips.add(new Trip("Merco Bus", new Date(), "Terminal Obelisco", "Malibu", 120));
        trips.add(new Trip("La Medalla", new Date(), "El Centauro", "Terminal Obelisco", 130));
        trips.add(new Trip("La Medalla", new Date(), "Terminal Obelisco", "El Centauro", 130));
        trips.add(new Trip("Merco Bus", new Date(), "Saint Thomas", "Terminal Obelisco", 120));
        trips.add(new Trip("Merco Bus", new Date(), "Terminal Obelisco", "Saint Thomas", 120));

        return trips;
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

