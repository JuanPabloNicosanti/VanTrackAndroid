package utn.proy2k18.vantrack.mainFunctionality.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import mainFunctionality.reservations.ReservationActivity;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.reservations.Reservation;


public class TripStopsFragment extends DialogFragment implements TripStopsAdapter.OnItemClickListener {

    private static final String ARG_PARAM1 = "reservation";
    private Reservation reservation;

    public TripStopsFragment() {
        // Required empty public constructor
    }

    public static TripStopsFragment newInstance(Reservation reservation) {
        TripStopsFragment tripStopsFragment = new TripStopsFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, reservation);
        tripStopsFragment.setArguments(args);

        return tripStopsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        reservation = getArguments().getParcelable(ARG_PARAM1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_stops, container, false);

        final RecyclerView mRecyclerView = view.findViewById(R.id.trip_stops_view);

        final RecyclerView.LayoutManager mLayoutManager = new
                GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        List<TripStop> tripStops = reservation.getBookedTrip().getStops();
        final TripStopsAdapter tripStopsAdapter = new TripStopsAdapter(tripStops);
        tripStopsAdapter.setOnItemClickListener(TripStopsFragment.this);
        mRecyclerView.setAdapter(tripStopsAdapter);

        return view;
    }

    public void onItemClick(final int position) {
        TripStop tripStop = reservation.getBookedTrip().getStops().get(position);
        reservation.setHopOnStop(tripStop);

        Intent resActivity = new Intent(getActivity(), ReservationActivity.class);
        resActivity.putExtra("reservation", reservation);
        startActivity(resActivity);
        dismiss();
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
            actionBar.setTitle(R.string.choose_stop);
        }
    }
}
