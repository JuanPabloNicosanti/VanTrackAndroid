package mainFunctionality.driverTrips;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.models.Passenger;

public class ConfirmPassengersFragment extends Fragment {

    private static final String BUNDLE_RECYCLER_LAYOUT = "classname.recycler.layout";
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private static ArrayList<Passenger> currentSelectedItems;
    private static ArrayList<Integer> currentSelectedIndexes;
    private static int lastPosition = -1;
    private RecyclerView recyclerView;


    public ConfirmPassengersFragment() {
    }
    @SuppressWarnings("unused")
    public static ConfirmPassengersFragment newInstance(int columnCount, int position) {
        if(currentSelectedItems == null || position != lastPosition) {
            currentSelectedItems = new ArrayList<>();
            currentSelectedIndexes = new ArrayList<>();
            lastPosition = position;
        }
        ConfirmPassengersFragment fragment = new ConfirmPassengersFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
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

            final ArrayList<Passenger> passengers = Passenger.createList();
            recyclerView.setAdapter(new ConfirmPassengerRecyclerViewAdapter(passengers, currentSelectedIndexes, mListener, new ConfirmPassengerRecyclerViewAdapter.OnItemCheckListener(){
                @Override
                public void onItemCheck(Passenger passenger, Integer index) {
                    currentSelectedIndexes.add(index);
                    currentSelectedItems.add(passenger);
                }

                @Override
                public void onItemUncheck(Passenger passenger, Integer index) {
                    currentSelectedIndexes.remove(index);
                    currentSelectedItems.remove(passenger);
                }
            }));
            //TODO: Modificar para que traiga por query solo los pasajeros que están dentro de este viaje. Ahora hace Passenger.createList

        Button confirmButton = view.findViewById(R.id.btn_confirmed_passengers);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO: Enviar lista de asistentes al back
                }
        });
        return view;
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
        void onListFragmentInteraction(Passenger item);
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
}
