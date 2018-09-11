package mainFunctionality.reservations;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.reservations.Reservation;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReservationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ReservationFragment extends Fragment {

    private static final String ARG_PARAM1 = "reservation";
    private static final String ARG_PARAM2 = "paymentStatus";
    private String paymentStatus;
    private TripsReservationsViewModel model;
    private OnFragmentInteractionListener mListener;
    private Reservation reservation;


    public ReservationFragment() {
        // Required empty public constructor
    }

    public static ReservationFragment newInstance(Reservation reservation) {
        ReservationFragment reservationFragment = new ReservationFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, reservation);
        reservationFragment.setArguments(args);

        return reservationFragment;
    }

    public static ReservationFragment newInstance(Reservation reservation, String paymentStatus) {
        ReservationFragment reservationFragment = new ReservationFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, reservation);
        args.putString(ARG_PARAM2, paymentStatus);
        reservationFragment.setArguments(args);

        return reservationFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(getActivity()).get(TripsReservationsViewModel.class);
        reservation = getArguments().getParcelable(ARG_PARAM1);
        paymentStatus = getArguments().getString(ARG_PARAM2, "NO_STATUS");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);

        TextView origin = view.findViewById(R.id.reservation_fragment_origin);
        TextView destination = view.findViewById(R.id.reservation_fragment_destination);
        TextView company = view.findViewById(R.id.reservation_fragment_company);
        TextView date = view.findViewById(R.id.reservation_fragment_date);
        TextView time = view.findViewById(R.id.reservation_fragment_time);
        TextView price = view.findViewById(R.id.reservation_price);
        Button btnCancelTrip = view.findViewById(R.id.btn_cancel_booking);
        Button btnPayReservation = view.findViewById(R.id.btn_pay_booking);

        if (paymentStatus.equals("approved")) {
            reservation.payBooking();
            btnPayReservation.setVisibility(View.GONE);
        } else {
            if (reservation.isPaid()) {
                btnPayReservation.setVisibility(View.GONE);
            }
        }

        origin.setText(reservation.getTripOrigin());
        destination.setText(reservation.getTripDestination());
        company.setText(reservation.getTripCompanyName());
        date.setText(reservation.getTripFormattedDate());
        time.setText(reservation.getTripStrTime());
        price.setText(String.valueOf(reservation.getBookedTrip().getPrice()));

        btnCancelTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Desea eliminar el Viaje?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position1) {
                                final Trip trip = reservation.getBookedTrip();
                                unsubscribeFromTripTopic(trip);
                                model.deleteReservation(reservation.get_id());

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

        btnPayReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PaymentActivity.class);
                Bundle b = new Bundle();
                b.putParcelable("reservation", reservation);
                intent.putExtras(b);
                startActivity(intent);
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
