package mainFunctionality.search;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import mainFunctionality.viewsModels.TripsViewModel;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.search.TripsAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchResultsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultsFragment extends Fragment implements TripsAdapter.OnItemClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "tripOrigin";
    private static final String ARG_PARAM2 = "tripDestination";
    private static final String ARG_PARAM3 = "tripDate";
    private static final String ARG_PARAM4 = "tripReturnDate";

    private TripsAdapter tripsAdapter;
    private OnFragmentInteractionListener mListener;
    private TripsViewModel tripsModel;
    private String argTripReturnDate;

    public SearchResultsFragment() {
        // Required empty public constructor
    }

// TODO: Cambiar el feed de datos. Historial?

    public static SearchResultsFragment newInstance(String tripOrigin, String tripDest,
                                                    String tripDate, String tripReturnDate) {
        SearchResultsFragment searchResultsFragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, tripOrigin);
        args.putString(ARG_PARAM2, tripDest);
        args.putString(ARG_PARAM3, tripDate);
        args.putString(ARG_PARAM4, tripReturnDate);
        searchResultsFragment.setArguments(args);

        return searchResultsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tripsModel = ViewModelProviders.of(getActivity()).get(TripsViewModel.class);
        tripsModel.init();

        final String argTripOrigin = getArguments().getString(ARG_PARAM1, "");
        final String argTripDestination = getArguments().getString(ARG_PARAM2, "");
        final String argTripDate = getArguments().getString(ARG_PARAM3, "");
        argTripReturnDate = getArguments().getString(ARG_PARAM4, "");

        tripsModel.filterBaseTrips(argTripOrigin, argTripDestination, argTripDate);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);
        final RecyclerView mRecyclerView = view.findViewById(R.id.search_results_view);

        final RecyclerView.LayoutManager mLayoutManager = new
                GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        tripsAdapter = new TripsAdapter(tripsModel.getBaseFilteredTrips());
        tripsAdapter.setOnItemClickListener(SearchResultsFragment.this);
        mRecyclerView.setAdapter(tripsAdapter);

        final Spinner filterByCompanySpinner = view.findViewById(R.id.company_filter_spinner);
        final Spinner sortOptionsSpinner = view.findViewById(R.id.sorting_options_spinner);
        final RangeSeekBar<Integer> tripsTimeRangeSeekBar = view.findViewById(R.id.trips_time_range_seek_bar);

        ArrayAdapter<CharSequence> filterByCompanyAdapter = ArrayAdapter.createFromResource(container.getContext(),
                R.array.companies, android.R.layout.simple_spinner_item);
        filterByCompanyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterByCompanySpinner.setAdapter(filterByCompanyAdapter);

        filterByCompanySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id) {
                String companyName = parent.getItemAtPosition(position).toString();
                if (companyName.equals("Seleccionar empresa")) {
                    companyName = null;
                }
                tripsModel.filterTripsByCompany(companyName);
                tripsAdapter.setItems(tripsModel.getFilteredTrips());
                tripsAdapter.notifyDataSetChanged();
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
                String orderField = parent.getItemAtPosition(position).toString();
                switch (orderField) {
                    case "Precio":
                        tripsModel.sortTripsByPrice();
                        break;

                    case "Calificacion":
                        tripsModel.sortTripsByCompanyName();
                        break;

                    case "Seleccione campo":
                        break;
                }
                tripsAdapter.setItems(tripsModel.getFilteredTrips());
                tripsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        tripsTimeRangeSeekBar.setRangeValues(tripsModel.getTripsMinTime(), tripsModel.getTripsMaxTime());
        tripsTimeRangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue,
                                                    Integer maxValue) {
                Toast.makeText(getContext(), minValue + "-" + maxValue, Toast.LENGTH_LONG).show();
                tripsModel.filterTripsByTime(minValue, maxValue);
                tripsAdapter.setItems(tripsModel.getFilteredTrips());
                tripsAdapter.notifyDataSetChanged();
            }
        });
        tripsTimeRangeSeekBar.setNotifyWhileDragging(true);
        tripsTimeRangeSeekBar.setTextAboveThumbsColor(Color.DKGRAY);

        return view;
    }

    public void onItemClick(final int position) {
        TripFragment newFragment = TripFragment.newInstance(position, argTripReturnDate);

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, newFragment);
        ft.addToBackStack(null);
        ft.commit();
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

