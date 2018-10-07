package mainFunctionality.search;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

import mainFunctionality.reservations.MyReservationsFragment;
import mainFunctionality.viewsModels.TripsReservationsViewModel;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TripFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class TripFragment extends Fragment {

    private static final String ARG_PARAM1 = "trip";
    private static final String ARG_PARAM2 = "returnDate";

    private Trip trip;
    private String returnDate;
    private TripsReservationsViewModel reservationsModel;
    private OnFragmentInteractionListener mListener;
    private DateTimeFormatter tf = DateTimeFormat.forPattern("HH:mm");
    private Integer seatsQty;
    private Integer newSeatsQty;

    public TripFragment() {
        // Required empty public constructor
    }

    public static TripFragment newInstance(Trip trip, String returnDate) {
        TripFragment tripFragment = new TripFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, trip);
        args.putString(ARG_PARAM2, returnDate);
        tripFragment.setArguments(args);

        return tripFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reservationsModel = TripsReservationsViewModel.getInstance();
        trip = getArguments().getParcelable(ARG_PARAM1);
        returnDate = getArguments().getString(ARG_PARAM2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip, container, false);

        TextView origin = view.findViewById(R.id.trip_fragment_origin);
        TextView destination = view.findViewById(R.id.trip_fragment_destination);
        TextView company = view.findViewById(R.id.trip_fragment_company);
        TextView date = view.findViewById(R.id.trip_fragment_date);
        TextView time = view.findViewById(R.id.trip_fragment_time);
        TextView price = view.findViewById(R.id.trip_price);
        TextView stops = view.findViewById(R.id.trip_fragment_stops);

        final Button btnBookTrip = view.findViewById(R.id.btn_book_trip);
        final Button btnBookTripSearchReturn = view.findViewById(R.id.btn_book_trip_search_return);

        final FrameLayout trip_booking_options = view.findViewById(R.id.trip_booking_options);
        trip_booking_options.setVisibility(View.VISIBLE);

        if (returnDate.equals(getResources().getString(R.string.no_return_date))) {
            btnBookTrip.setVisibility(View.VISIBLE);
            btnBookTripSearchReturn.setVisibility(View.GONE);
        } else {
            btnBookTrip.setVisibility(View.GONE);
            btnBookTripSearchReturn.setVisibility(View.VISIBLE);
        }

        origin.setText(trip.getOrigin());
        destination.setText(trip.getDestination());
        company.setText(trip.getCompanyName());
        date.setText(trip.getDate().toString());
        time.setText(trip.getTime().toString(tf));
        price.setText(String.valueOf(trip.getPrice()));
        stops.setText(trip.createStrStops());

        btnBookTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseQtyOfSeatsAndConfirm();
//                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//
//                if (reservationsModel.isTripBooked(trip)) {
//                    builder.setMessage("Ya posee una reserva para este viaje.")
//                        .setPositiveButton("Aceptar", null);
//
//                } else {
//                    builder.setMessage("Desea reservar el Viaje?")
//                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int position1) {
//                                bookTrip(seatsQty);
//                                setFragment(new MyReservationsFragment());
//                            }
//                        })
//                        .setNegativeButton("Cancelar", null);
//                }
//
//                AlertDialog alert = builder.create();
//                alert.show();
            }
        });

        btnBookTripSearchReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseQtyOfSeatsAndConfirm();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                if (reservationsModel.isTripBooked(trip)) {
                    builder.setMessage("Ya posee una reserva para este viaje.")
                        .setPositiveButton("Aceptar", null);

                } else {
                    builder.setMessage("Desea reservar el Viaje?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position1) {
                                bookTrip(seatsQty);

                                SearchResultsFragment searchResultsFragment =
                                        SearchResultsFragment.newInstance(true);
                                setFragment(searchResultsFragment);
                            }
                        })
                        .setNegativeButton("Cancelar", null);
                }

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        return view;
    }

    private void chooseQtyOfSeatsAndConfirm() {
        final ArrayList<Integer> seatsQtyOptions = range(1, trip.getSeatsMaxPerReservationQty());
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.seats_qty_dialog);

        final Spinner spinner = dialog.findViewById(R.id.seats_qty_spinner);
        Button confirmButton = dialog.findViewById(R.id.confirm_seats_qty_button);
        Button cancelButton = dialog.findViewById(R.id.cancel_seats_qty_button);

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, seatsQtyOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newSeatsQty = seatsQtyOptions.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                seatsQty = newSeatsQty;
                bookTrip(seatsQty);
                setFragment(new MyReservationsFragment());
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static ArrayList<Integer> range(int min, int max) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = min; i <= max; i++) {
            list.add(i);
        }
        return list;
    }

    private void bookTrip(int seatsQty) {
        // TODO: replace this for VanTrackApplication email
        String username = "lucas.lopez@gmail.com";
        Integer firstStopId = trip.getStops().get(0).getId();
        reservationsModel.createReservationForTrip(trip, seatsQty, firstStopId, username);
        subscribeToTripTopic();
    }

    private void subscribeToTripTopic() {
        String topic = "trips__" + String.valueOf(trip.get_id());
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack(null);
        ft.commit();
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
        void onFragmentInteraction(Uri uri);
    }
}
