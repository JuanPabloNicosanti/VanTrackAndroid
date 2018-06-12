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
import utn.proy2k18.vantrack.mainFunctionality.Company;

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
    private List<Trip> baseFilteredTrips;
    private List<Trip> tripsFilteredByCompany;
    private List<Trip> tripsFilteredByTime;
    private List<Trip> tripsFiltered;
    private Spinner sortOptionsSpinner;

    public SearchResultsFragment() {
        // Required empty public constructor
    }

// TODO: Cambiar el feed de datos. Historial?

    public static SearchResultsFragment newInstance() { return new SearchResultsFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String argTripOrigin = getArguments().getString(ARG_PARAM1);
        String argTripDestination = getArguments().getString(ARG_PARAM2);
        String argTripDate = getArguments().getString(ARG_PARAM3);

        List<Trip> baseTrips = createTestTrips();
        baseFilteredTrips = filterTrips(baseTrips, argTripOrigin, argTripDestination, argTripDate);
        tripsFilteredByCompany = baseFilteredTrips;
        tripsFilteredByTime = baseFilteredTrips;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);
        mRecyclerView = view.findViewById(R.id.search_results_view);

        mLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new TripsAdapter(baseFilteredTrips);
        mRecyclerView.setAdapter(mAdapter);

        final Spinner filterByCompanySpinner = (Spinner) view.findViewById(R.id.company_filter_spinner);
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

        sortOptionsSpinner = (Spinner) view.findViewById(R.id.sorting_options_spinner);
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
                mAdapter = new TripsAdapter(tripsFiltered);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        final int tripsMinTime = getTripsMinTime(baseFilteredTrips);
        final int tripsMaxTime = getTripsMaxTime(baseFilteredTrips);
        final RangeSeekBar<Integer> tripsTimeRangeSeekBar = view.findViewById(R.id.trips_time_range_seek_bar);
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

        return view;
    }

    private void setNewAdapter() {
        tripsFiltered = intersection(tripsFilteredByCompany, tripsFilteredByTime);
        sortTrips(sortOptionsSpinner.getSelectedItem().toString(), tripsFiltered);
        mAdapter = new TripsAdapter(tripsFiltered);
        mRecyclerView.setAdapter(mAdapter);
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
        List<Trip> filteredTrips = new ArrayList<Trip>();

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

    public List<Trip> createTestTrips() {
        Company laMedalla = new Company("La Medalla", 30611234);
        Company mercoBus = new Company("Merco Bus", 30611235);
        Company adrogueBus = new Company("Adrogue Bus", 30611236);
        laMedalla.addCalification(5.0);
        laMedalla.addCalification(3.0);
        laMedalla.addCalification(4.5);
        laMedalla.addCalification(4.0);
        mercoBus.addCalification(2.0);
        mercoBus.addCalification(3.0);
        mercoBus.addCalification(4.5);
        adrogueBus.addCalification(1.5);
        adrogueBus.addCalification(2.5);
        adrogueBus.addCalification(2.5);

        final List<Trip> trips = new ArrayList<Trip>();
        trips.add(new Trip(laMedalla, new Date(new Date().getTime() + 2 * 3600*1000), "Echeverria del Lago", "Terminal Obelisco", 120));
        trips.add(new Trip(laMedalla, new Date(), "Terminal Obelisco", "Echeverria del Lago", 120));
        trips.add(new Trip(mercoBus, new Date(new Date().getTime() + 2 * 3600*1000), "Echeverria del Lago", "Terminal Obelisco", 115));
        trips.add(new Trip(mercoBus, new Date(), "Terminal Obelisco", "Echeverria del Lago", 115));
        trips.add(new Trip(laMedalla, new Date(), "Echeverria del Lago", "Terminal Obelisco", 120));
        trips.add(new Trip(laMedalla, new Date(), "Terminal Obelisco", "Echeverria del Lago", 120));
        trips.add(new Trip(mercoBus, new Date(), "Echeverria del Lago", "Terminal Obelisco", 115));
        trips.add(new Trip(mercoBus, new Date(), "Terminal Obelisco", "Echeverria del Lago", 115));
        trips.add(new Trip(mercoBus, new Date(new Date().getTime() - 4 * 3600*1000), "Echeverria del Lago", "Terminal Obelisco", 115));
        trips.add(new Trip(mercoBus, new Date(), "Terminal Obelisco", "Echeverria del Lago", 115));
        trips.add(new Trip(laMedalla, new Date(new Date().getTime() - 2 * 3600*1000), "Echeverria del Lago", "Terminal Obelisco", 120));
        trips.add(new Trip(laMedalla, new Date(), "Terminal Obelisco", "Echeverria del Lago", 120));
        trips.add(new Trip(mercoBus, new Date(new Date().getTime() - 2 * 3600*1000), "Echeverria del Lago", "Terminal Obelisco", 115));
        trips.add(new Trip(mercoBus, new Date(), "Terminal Obelisco", "Echeverria del Lago", 115));
        trips.add(new Trip(laMedalla, new Date(), "Campos de Echeverria", "Terminal Obelisco", 110));
        trips.add(new Trip(laMedalla, new Date(), "Terminal Obelisco", "Campos de Echeverria", 110));
        trips.add(new Trip(adrogueBus, new Date(), "Malibu", "Terminal Obelisco", 120));
        trips.add(new Trip(adrogueBus, new Date(), "Terminal Obelisco", "Malibu", 120));
        trips.add(new Trip(mercoBus, new Date(), "El Centauro", "Terminal Obelisco", 130));
        trips.add(new Trip(mercoBus, new Date(), "Terminal Obelisco", "El Centauro", 130));
        trips.add(new Trip(adrogueBus, new Date(), "Saint Thomas", "Terminal Obelisco", 120));
        trips.add(new Trip(adrogueBus, new Date(), "Terminal Obelisco", "Saint Thomas", 120));

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

