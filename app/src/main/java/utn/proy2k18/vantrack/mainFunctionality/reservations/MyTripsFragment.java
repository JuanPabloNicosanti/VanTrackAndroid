package utn.proy2k18.vantrack.mainFunctionality.reservations;

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

import java.util.ArrayList;
import java.util.List;

import utn.proy2k18.vantrack.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyTripsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyTripsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyTripsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private OnFragmentInteractionListener mListener;
    final List<Reservation> reservations = new ArrayList<Reservation>();

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
        View view = inflater.inflate(R.layout.fragment_my_trips, container, false);

        mRecyclerView = view.findViewById(R.id.reservations_view);

        // use a linear layout manager
        mLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ReservationsAdapter(model.getReservations());
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
        reservations.remove(position);
        mAdapter.notifyItemRemoved(position);
    }
}

