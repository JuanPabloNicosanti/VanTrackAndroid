package utn.proy2k18.vantrack.mainFunctionality.reservations;

import android.arch.lifecycle.ViewModelProviders;
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

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.app.Dialog;
import android.content.DialogInterface;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import java.util.List;
import java.util.ArrayList;

import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.mainFunctionality.search.SearchResultsFragment;
import utn.proy2k18.vantrack.mainFunctionality.viewsModels.TripsReservationsViewModel;

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
    private static final String ARG_PARAM1 = "positionMyTrip";
    private TripsReservationsViewModel model;

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
        model = ViewModelProviders.of(getActivity()).get(TripsReservationsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final int position = getArguments().getInt(ARG_PARAM1, -1);


        View view = inflater.inflate(R.layout.fragment_my_trips, container, false);

        mRecyclerView = view.findViewById(R.id.reservations_view);

        // use a linear layout manager
        mLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ReservationsAdapter(model.getReservations());
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
        Reservation reservation = model.getReservations().get(position);

        Bundle args = new Bundle();
        args.putString("tripOrigin", reservation.getTripOrigin());
        args.putString("tripDestination", reservation.getTripDestination());
        args.putString("tripCompany", reservation.getTripCompanyName());
        args.putString("tripDate", reservation.getTripFormattedDate());
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

    private void remove_item (int position){
        model.getReservations().remove(position);
        mAdapter.notifyItemRemoved(position);
    }
}

