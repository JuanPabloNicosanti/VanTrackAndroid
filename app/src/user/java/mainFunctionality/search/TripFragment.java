package mainFunctionality.search;

import android.app.Activity;
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
import utn.proy2k18.vantrack.viewModels.UsersViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TripFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class TripFragment extends Fragment {

    private static final String ARG_PARAM1 = "trip";
    private static final String ARG_PARAM2 = "returnDate";
    private static final String ARG_PARAM3 = "argTripHopOnStop";

    private Trip trip;
    private String returnDate;
    private String argTripHopOnStop;
    private TripsReservationsViewModel reservationsModel;
    private OnFragmentInteractionListener mListener;
    private DateTimeFormatter tf = DateTimeFormat.forPattern("HH:mm");
    private Integer seatsQty;
    private String username = UsersViewModel.getInstance().getActualUserEmail();


    public TripFragment() {
        // Required empty public constructor
    }

    public static TripFragment newInstance(Trip trip, String returnDate, String argTripHopOnStop) {
        TripFragment tripFragment = new TripFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, trip);
        args.putString(ARG_PARAM2, returnDate);
        args.putString(ARG_PARAM3, argTripHopOnStop);
        tripFragment.setArguments(args);

        return tripFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reservationsModel = TripsReservationsViewModel.getInstance();
        trip = getArguments().getParcelable(ARG_PARAM1);
        returnDate = getArguments().getString(ARG_PARAM2);
        argTripHopOnStop = getArguments().getString(ARG_PARAM3);
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
        date.setText(trip.getFormattedDate());
        time.setText(trip.getTime().toString(tf));
        price.setText(String.valueOf(trip.getPrice()));
        stops.setText(trip.createStrStops());

        btnBookTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reservationsModel.isTripBooked(trip, username)) {
                    showErrorDialog(getActivity(), "Ya posee una reserva para este viaje.");
                } else {
                    chooseQtyOfSeatsAndConfirm(false);
                }
            }
        });

        btnBookTripSearchReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reservationsModel.isTripBooked(trip, username)) {
                    showErrorDialog(getActivity(), "Ya posee una reserva para este viaje.");
                } else {
                    chooseQtyOfSeatsAndConfirm(true);
                }
            }
        });

        return view;
    }

    private void chooseQtyOfSeatsAndConfirm(final boolean searchReturnTrips) {
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
                seatsQty = seatsQtyOptions.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean seatsAvailable = checkAvailableSeats(seatsQty);
                if (seatsAvailable) {
                    bookTrip(seatsQty, false);
                    setNextFragment(searchReturnTrips);
                } else {
                    showWaitListDialog(getActivity(), seatsQty, searchReturnTrips);
                }
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

    private boolean checkAvailableSeats(int seatsQty) {
        boolean seatsAvailable = false;
        if (trip.getSeatsAvailableQty() >= seatsQty) {
            seatsAvailable = true;
        }
        return seatsAvailable;
    }

    public static ArrayList<Integer> range(int min, int max) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = min; i <= max; i++) {
            list.add(i);
        }
        return list;
    }

    public void showErrorDialog(Activity activity, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setMessage(message)
                .setNeutralButton("Aceptar",null)
                .create();
        alertDialog.show();
    }

    public void showWaitListDialog(Activity activity, final Integer seatsQty,
                                   final boolean searchReturnTrips) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setMessage("No hay asientos disponibles. Desea anotarse en lista de espera?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bookTrip(seatsQty, true);
                        setNextFragment(searchReturnTrips);
                    }
                })
                .setNegativeButton("No", null)
                .create();
        alertDialog.show();
    }

    private void bookTrip(int seatsQty, boolean isWaitList) {
        Integer hopOnStopId = trip.getTripStopByDescription(argTripHopOnStop).getId();
        reservationsModel.createReservationForTrip(trip, seatsQty, hopOnStopId, username, isWaitList);
        subscribeToTripTopic(isWaitList);
    }

    private void subscribeToTripTopic(boolean isInWaitList) {
        String tripTopic = "trip__" + String.valueOf(trip.get_id());
        String superTripTopic = "super_trip__" + String.valueOf(trip.getTripSuperId());

        FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
        firebaseMessaging.subscribeToTopic(tripTopic);
        firebaseMessaging.subscribeToTopic(superTripTopic);

        if (isInWaitList) {
            Integer userId = UsersViewModel.getInstance().getActualUserId();
            String topicPrefix = String.format("user_%d_wait_list_trip__", userId)
                    .toLowerCase().replaceAll("@", "");
            String tripWaitListTopic = topicPrefix + String.valueOf(trip.get_id());
            firebaseMessaging.subscribeToTopic(tripWaitListTopic);
        }
    }

    private void setNextFragment(final boolean searchReturnTrips) {
        Fragment newFragment;
        if (searchReturnTrips) {
            newFragment = SearchResultsFragment.newInstance(true);
        } else {
            newFragment = new MyReservationsFragment();
        }
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, newFragment);
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
