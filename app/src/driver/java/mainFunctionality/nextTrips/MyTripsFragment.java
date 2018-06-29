package mainFunctionality.nextTrips;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.arch.lifecycle.ViewModelProviders;

import utn.proy2k18.vantrack.R;
import mainFunctionality.viewsModels.TripsViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyTripsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyTripsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyTripsFragment extends Fragment implements TripsAdapter.OnItemClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private TripsAdapter tripsAdapter;
    private OnFragmentInteractionListener mListener;
    private TripsViewModel tripsModel;

    public MyTripsFragment() {
        // Required empty public constructor
    }

// TODO: Cambiar el feed de datos. Historial?

    public static MyTripsFragment newInstance() {
        MyTripsFragment myTripsFragment = new MyTripsFragment();

        return myTripsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tripsModel = ViewModelProviders.of(getActivity()).get(TripsViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_reservations, container, false);
        final RecyclerView mRecyclerView = view.findViewById(R.id.reservations_view);

        final RecyclerView.LayoutManager mLayoutManager = new
                GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        tripsAdapter = new TripsAdapter(tripsModel.getTotalTrips());
        tripsAdapter.setOnItemClickListener(MyTripsFragment.this);
        mRecyclerView.setAdapter(tripsAdapter);


        return view;
    }

    public void onItemClick(final int position) {
        TripFragment newFragment = TripFragment.newInstance(position);

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

