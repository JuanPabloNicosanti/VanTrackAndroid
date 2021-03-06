package mainFunctionality.reservations;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import mainFunctionality.viewsModels.TripsReservationsViewModel;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.exceptions.FailedToGetReservationsException;
import utn.proy2k18.vantrack.models.Reservation;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyReservationsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyReservationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class MyReservationsFragment extends Fragment implements ReservationsAdapter.OnItemClickListener {

    private OnFragmentInteractionListener mListener;
    private TripsReservationsViewModel model;
    private FirebaseUser user;
    private List<Reservation> reservations;

    public MyReservationsFragment() {
        // Required empty public constructor
    }

    public static MyReservationsFragment newInstance() {
        return new MyReservationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = TripsReservationsViewModel.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_reservations, container, false);
        final RecyclerView mRecyclerView = view.findViewById(R.id.reservations_view);

        final RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(),
                1, GridLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    reservations = model.getReservations(user.getEmail());
                } catch (FailedToGetReservationsException fgre) {
                    showErrorDialog(getActivity(), fgre.getMessage());
                }
            }
        });
        final ReservationsAdapter resAdapter = new ReservationsAdapter(reservations);
        resAdapter.setOnItemClickListener(MyReservationsFragment.this);
        mRecyclerView.setAdapter(resAdapter);

        return view;
    }

    public void showErrorDialog(Activity activity, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setMessage(message)
                .setNeutralButton("Aceptar",null)
                .create();
        alertDialog.show();
    }

    public void onItemClick(final int position) {
        Reservation reservation = model.getReservationAtPosition(position, user.getEmail());
        Intent intent = new Intent(getActivity(), ReservationActivity.class);
        intent.putExtra("reservation_id", reservation.get_id());
        startActivity(intent);
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
            actionBar.setTitle(R.string.my_trips);
        }
    }

}

