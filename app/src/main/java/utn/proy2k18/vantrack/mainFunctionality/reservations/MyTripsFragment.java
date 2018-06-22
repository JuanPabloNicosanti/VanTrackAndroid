package utn.proy2k18.vantrack.mainFunctionality.reservations;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.app.Dialog;
import android.content.DialogInterface;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;


import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.mainFunctionality.search.SearchResultsFragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyTripsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyTripsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyTripsFragment extends Fragment implements ReservationsAdapter.OnItemClickListener{

    private RecyclerView mRecyclerView;
    private ReservationsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private OnFragmentInteractionListener mListener;
    final List<Reservation> reservations = new ArrayList<Reservation>();
    private static final String ARG_PARAM1 = "positionMyTrip";
    private int position;


    public MyTripsFragment() {
        // Required empty public constructor
    }

// TODO: Cambiar el feed de datos. Historial?

    public static MyTripsFragment newInstance() {
        MyTripsFragment fragment = new MyTripsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        position = getArguments().getInt(ARG_PARAM1);


        View view = inflater.inflate(R.layout.fragment_my_trips, container, false);

        mRecyclerView = view.findViewById(R.id.reservations_view);

        // use a linear layout manager
        mLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        reservations.add(new Reservation("La Medalla", new Date(), "Echeverria del Lago", "Obelisco"));
        reservations.add(new Reservation("Adrogue Bus", new Date(), "Obelisco", "Echeverria del Lago"));
        reservations.add(new Reservation("La Medalla", new Date(), "Campos de Echeverria", "Obelisco"));
        reservations.add(new Reservation("Adrogue Bus", new Date(), "Obelisco", "Campos de Echeverria"));
        reservations.add(new Reservation("La Medalla", new Date(), "Malibu", "Obelisco"));
        reservations.add(new Reservation("Adrogue Bus", new Date(), "Obelisco", "Malibu"));
        reservations.add(new Reservation("La Medalla", new Date(), "El Centauro", "Obelisco"));
        reservations.add(new Reservation("Adrogue Bus", new Date(), "Obelisco", "El Centauro"));
        reservations.add(new Reservation("La Medalla", new Date(), "Saint Thomas", "Obelisco"));
        reservations.add(new Reservation("Adrogue Bus", new Date(), "Obelisco", "Saint Thomas"));
        // specify an adapter (see also next example)

        mAdapter = new ReservationsAdapter(reservations);


        mAdapter.setOnItemClickListener(MyTripsFragment.this);


        if(position > -1){
            mAdapter.remove_item(position);
        }


        mRecyclerView.setAdapter(mAdapter);
        // Inflate the layout for this fragment
        return view;

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


    public void onItemClick(final int position) {

        TripFragment newFragment = new TripFragment();

        Bundle args = new Bundle();
        args.putString("tripOrigin", reservations.get(position).getOrigin());
        args.putString("tripDestination", reservations.get(position).getDestination());
        args.putString("tripCompany", reservations.get(position).getCompany());
        args.putString("tripDate", reservations.get(position).getDate().toString());
        args.putInt("positionTrip",position);

        newFragment.setArguments(args);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,newFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();



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
            actionBar.setTitle(R.string.my_trips);
        }
    }






}

