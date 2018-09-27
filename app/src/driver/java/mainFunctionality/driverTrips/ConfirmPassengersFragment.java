package mainFunctionality.driverTrips;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private ArrayList<Passenger> currentSelectedItems = new ArrayList<>();

    public ConfirmPassengersFragment() {
    }
    @SuppressWarnings("unused")
    public static ConfirmPassengersFragment newInstance(int columnCount) {
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
            RecyclerView recyclerView = view.findViewById(R.id.passengersList);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            final ArrayList<Passenger> passengers = Passenger.createList();
            recyclerView.setAdapter(new ConfirmPassengerRecyclerViewAdapter(passengers, mListener, new ConfirmPassengerRecyclerViewAdapter.OnItemCheckListener(){
                @Override
                public void onItemCheck(Passenger passenger) {
                    currentSelectedItems.add(passenger);
                }

                @Override
                public void onItemUncheck(Passenger passenger) {
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Passenger item);
    }
}
