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

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

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

        // use a linear layout manager
        mLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        final List<Trip> baseTrips = createTripsList();
        final List<Trip> filteredTrips = filterTrips(baseTrips, argTripOrigin, argTripDestination,
                argTripDate);
        // specify an adapter (see also next example)
        mAdapter = new TripsAdapter(filteredTrips);
        mRecyclerView.setAdapter(mAdapter);
        // Inflate the layout for this fragment
        return view;

    }

    public List<Trip> filterTrips(List<Trip> baseTrips, String argTripOrigin,
                                  String argTripDestination, String argTripDate) {
        List<Trip> filteredTrips = new ArrayList<Trip>();

        for(int l=0; l<baseTrips.size(); l++){
            Trip trip = baseTrips.get(l);
            
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
        trips.add(new Trip("La Medalla", new Date(), "Echeverria del Lago", "Terminal Obelisco"));
        trips.add(new Trip("La Medalla", new Date(), "Terminal Obelisco", "Echeverria del Lago"));
        trips.add(new Trip("Merco Bus", new Date(), "Echeverria del Lago", "Terminal Obelisco"));
        trips.add(new Trip("Merco Bus", new Date(), "Terminal Obelisco", "Echeverria del Lago"));
        trips.add(new Trip("La Medalla", new Date(), "Campos de Echeverria", "Terminal Obelisco"));
        trips.add(new Trip("La Medalla", new Date(), "Terminal Obelisco", "Campos de Echeverria"));
        trips.add(new Trip("Merco Bus", new Date(), "Malibu", "Terminal Obelisco"));
        trips.add(new Trip("Merco Bus", new Date(), "Terminal Obelisco", "Malibu"));
        trips.add(new Trip("La Medalla", new Date(), "El Centauro", "Terminal Obelisco"));
        trips.add(new Trip("La Medalla", new Date(), "Terminal Obelisco", "El Centauro"));
        trips.add(new Trip("Merco Bus", new Date(), "Saint Thomas", "Terminal Obelisco"));
        trips.add(new Trip("Merco Bus", new Date(), "Terminal Obelisco", "Saint Thomas"));

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

