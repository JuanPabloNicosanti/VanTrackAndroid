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

import java.util.List;

import mainFunctionality.viewsModels.TripsViewModel;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.mainFunctionality.search.TripsAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchResultsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultsFragment extends Fragment implements TripsAdapter.OnItemClickListener {

    private static final String ARG_PARAM1 = "isReturnSearch";

    private TripsAdapter tripsAdapter;
    private OnFragmentInteractionListener mListener;
    private TripsViewModel tripsModel;
    private List<Trip> trips;
    private boolean isReturnSearch;


    public SearchResultsFragment() {
        // Required empty public constructor
    }

    public static SearchResultsFragment newInstance(boolean isReturnSearch) {
        SearchResultsFragment searchResultsFragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, isReturnSearch);
        searchResultsFragment.setArguments(args);
        return searchResultsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tripsModel = ViewModelProviders.of(getActivity()).get(TripsViewModel.class);

        isReturnSearch = getArguments().getBoolean(ARG_PARAM1, false);
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                trips = tripsModel.getTrips(isReturnSearch);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);
        final RecyclerView mRecyclerView = view.findViewById(R.id.search_results_view);
        final RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(),
                1, GridLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        tripsAdapter = new TripsAdapter(trips);
        tripsAdapter.setOnItemClickListener(SearchResultsFragment.this);
        mRecyclerView.setAdapter(tripsAdapter);

        final Spinner filterByCompanySpinner = view.findViewById(R.id.company_filter_spinner);
        final Spinner sortOptionsSpinner = view.findViewById(R.id.sorting_options_spinner);
        final RangeSeekBar<Integer> tripsTimeRangeSeekBar = view.findViewById(R.id.trips_time_range_seek_bar);

        ArrayAdapter<CharSequence> sortOptionsAdapter = ArrayAdapter.createFromResource(
                container.getContext(), R.array.sorting_options, android.R.layout.simple_spinner_item);
        sortOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortOptionsSpinner.setAdapter(sortOptionsAdapter);

        sortOptionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id) {
                String spinnerOption = parent.getItemAtPosition(position).toString();
                tripsModel.sortTripsBySpinnerOption(spinnerOption);
                tripsAdapter.setItems(tripsModel.getFilteredTrips());
                tripsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<CharSequence> filterByCompanyAdapter = ArrayAdapter.createFromResource(
                container.getContext(), R.array.companies, android.R.layout.simple_spinner_item);
        filterByCompanyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterByCompanySpinner.setAdapter(filterByCompanyAdapter);

        filterByCompanySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id) {
                String companyName = parent.getItemAtPosition(position).toString();
                int minTimeSelected = tripsTimeRangeSeekBar.getSelectedMinValue();
                int maxTimeSelected = tripsTimeRangeSeekBar.getSelectedMaxValue();
                tripsAdapter.setItems(tripsModel.getTrips(companyName, minTimeSelected,
                        maxTimeSelected, sortOptionsSpinner.getSelectedItem().toString()));
                tripsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        tripsTimeRangeSeekBar.setRangeValues(tripsModel.getTripsMinTime(tripsModel.getFilteredTrips()),
                tripsModel.getTripsMaxTime(tripsModel.getFilteredTrips()));
        tripsTimeRangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue,
                                                    Integer maxValue) {
                Toast.makeText(getContext(), minValue + "-" + maxValue, Toast.LENGTH_SHORT).show();
                String companyName = filterByCompanySpinner.getSelectedItem().toString();
                tripsAdapter.setItems(tripsModel.getTrips(companyName, minValue, maxValue,
                        sortOptionsSpinner.getSelectedItem().toString()));
                tripsAdapter.notifyDataSetChanged();
            }
        });
        tripsTimeRangeSeekBar.setNotifyWhileDragging(true);
        tripsTimeRangeSeekBar.setTextAboveThumbsColor(Color.BLACK);

        return view;
    }

    public void onItemClick(final int position) {
        setFragment(TripFragment.newInstance(position));
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
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

