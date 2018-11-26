package mainFunctionality.driverTrips;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.List;

import mainFunctionality.viewsModels.TripsViewModel;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.exceptions.BackendConnectionException;
import utn.proy2k18.vantrack.exceptions.NoPassengersException;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.models.PassengerReservation;

public class ConfirmPassengersFragment extends Fragment {

    private static final String BUNDLE_RECYCLER_LAYOUT = "classname.recycler.layout";
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_TRIP_PARAM = "trip";
    private int mColumnCount = 1;
    private Trip trip;
    private OnListFragmentInteractionListener mListener;
    private static ArrayList<PassengerReservation> currentSelectedItems;
    private static ArrayList<Integer> currentSelectedIndexes;
    private List<PassengerReservation> passengers;
    private static Trip lastTrip;
    private RecyclerView recyclerView;
    private TripsViewModel tripsModel;


    public ConfirmPassengersFragment() {
    }

    public static ConfirmPassengersFragment newInstance(int columnCount, Trip trip) {
        if(currentSelectedItems == null || trip.get_id() != lastTrip.get_id()) {
            currentSelectedItems = new ArrayList<>();
            currentSelectedIndexes = new ArrayList<>();
            lastTrip = trip;
        }
        ConfirmPassengersFragment fragment = new ConfirmPassengersFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putParcelable(ARG_TRIP_PARAM, trip);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tripsModel = TripsViewModel.getInstance();

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            trip = getArguments().getParcelable(ARG_TRIP_PARAM);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_passenger_list, container, false);
        Context context = view.getContext();
        GridLayoutManager mLayoutManager = new GridLayoutManager(context, mColumnCount);
        recyclerView = view.findViewById(R.id.passengersList);

        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(mLayoutManager);
        }

        try {
            passengers = tripsModel.getTripPassengers(trip.get_id());
        } catch (NoPassengersException | BackendConnectionException e) {
            showErrorDialog(getActivity(), e.getMessage());
            setFragment(new MyTripsFragment());
        }

        recyclerView.setAdapter(new ConfirmPassengerRecyclerViewAdapter(passengers,
                currentSelectedIndexes, mListener,
                new ConfirmPassengerRecyclerViewAdapter.OnItemCheckListener(){
            @Override
            public void onItemCheck(PassengerReservation passenger, Integer index) {
                currentSelectedIndexes.add(index);
                currentSelectedItems.add(passenger);
            }

            @Override
            public void onItemUncheck(PassengerReservation passenger, Integer index) {
                currentSelectedIndexes.remove(index);
                currentSelectedItems.remove(passenger);
            }
        }));

        Button confirmButton = view.findViewById(R.id.btn_confirmed_passengers);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Está seguro de que desea confirmar la lista de pasajeros? " +
                        "Si acepta, no podrá volver a modificar esta lista. De lo contrario, " +
                        "mientras no cierre la aplicación, se guardarán los cambios realizados.")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position1) {
                                trip.setConfirmed();
                                try {
                                    tripsModel.confirmTripPassengers(trip, currentSelectedItems);
                                } catch (JsonProcessingException jpe) {
                                    showErrorDialog(getActivity(), "Error al confirmar " +
                                            "los viajes, inténtelo de nuevo más tarde.");
                                } catch (BackendConnectionException bce) {
                                    showErrorDialog(getActivity(), bce.getMessage());
                                }
                                setFragment(TripFragment.newInstance(trip));
                            }
                        })
                        .setNegativeButton("No",null);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        return view;
    }

    public void setFragment(Fragment fragment) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.disallowAddToBackStack();
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
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(PassengerReservation item);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState != null)
        {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
            currentSelectedIndexes = savedInstanceState.getIntegerArrayList("passengers");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, recyclerView.getLayoutManager().onSaveInstanceState());
        outState.putIntegerArrayList("passengers",currentSelectedIndexes);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
            currentSelectedIndexes = savedInstanceState.getIntegerArrayList("passengers");
        }
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
            actionBar.setTitle(R.string.next_trip_passengers);
        }
    }
}
