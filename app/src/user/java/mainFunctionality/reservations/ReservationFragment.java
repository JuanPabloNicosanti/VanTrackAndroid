package mainFunctionality.reservations;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

import mainFunctionality.viewsModels.TripsReservationsViewModel;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.search.Trip;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReservationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ReservationFragment extends Fragment {

    private static final String ARG_PARAM1 = "reservationPosition";
    private int position;
    private TripsReservationsViewModel model;
    private OnFragmentInteractionListener mListener;


    public ReservationFragment() {
        // Required empty public constructor
    }

    public static ReservationFragment newInstance(int reservationPosition) {
        ReservationFragment reservationFragment = new ReservationFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, reservationPosition);
        reservationFragment.setArguments(args);

        return reservationFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(getActivity()).get(TripsReservationsViewModel.class);
        position = getArguments().getInt(ARG_PARAM1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);

        TextView origin = view.findViewById(R.id.reservation_fragment_origin);
        TextView destination = view.findViewById(R.id.reservation_fragment_destination);
        TextView company = view.findViewById(R.id.reservation_fragment_company);
        TextView date = view.findViewById(R.id.reservation_fragment_date);
        TextView hour = view.findViewById(R.id.reservation_fragment_hour);
        TextView price = view.findViewById(R.id.reservation_price);
        Button btn_cancel_trip = view.findViewById(R.id.btn_cancel_trip);

        final Reservation reservation = model.getReservationAtPosition(position);

        origin.setText(reservation.getTripOrigin());
        destination.setText(reservation.getTripDestination());
        company.setText(reservation.getTripCompanyName());
        date.setText(reservation.getTripFormattedDate());
        hour.setText(reservation.getTripFormattedHour());
        price.setText(String.valueOf(reservation.getBookedTrip().getPrice()));

        btn_cancel_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Desea eliminar el Viaje?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position1) {
                                final Trip trip = reservation.getBookedTrip();
                                unsubscribeFromTripTopic(trip);
                                model.deleteReservationAtPosition(position);

                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.replace(R.id.fragment_container, new MyReservationsFragment());
                                ft.commit();
                            }


                        })
                        .setNegativeButton("Cancelar",null);

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        return view;
    }

    private void unsubscribeFromTripTopic(Trip trip) {
        // topic string should be the trip unique id declared in DB
        String topic = "trips__" + trip.get_id();
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
}
