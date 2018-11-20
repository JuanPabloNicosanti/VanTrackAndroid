package mainFunctionality.search;

import android.app.Activity;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Locale;

import mainFunctionality.reservations.MyReservationsFragment;
import mainFunctionality.viewsModels.TripsReservationsViewModel;
import mainFunctionality.viewsModels.TripsViewModel;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.exceptions.BackendConnectionException;
import utn.proy2k18.vantrack.exceptions.BackendException;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.mainFunctionality.search.TripStop;
import utn.proy2k18.vantrack.viewModels.UsersViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TripFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class TripFragment extends Fragment {

    private static final String ARG_PARAM1 = "position";

    private Trip trip;
    private Boolean hasReturnSearch;
    private Boolean isReturnSearch;
    private TripsViewModel tripsModel;
    private TripsReservationsViewModel reservationsModel;
    private OnFragmentInteractionListener mListener;
    private DateTimeFormatter tf = DateTimeFormat.forPattern("HH:mm");
    private Integer seatsQty;
    private String username;


    public TripFragment() {
        // Required empty public constructor
    }

    public static TripFragment newInstance(Integer position) {
        TripFragment tripFragment = new TripFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, position);
        tripFragment.setArguments(args);

        return tripFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reservationsModel = TripsReservationsViewModel.getInstance();
        tripsModel = ViewModelProviders.of(getActivity()).get(TripsViewModel.class);
        try {
            username = UsersViewModel.getInstance().getActualUserEmail();
        } catch (BackendException be) {
            showErrorDialog(getActivity(), be.getErrorMsg());
        } catch (BackendConnectionException bce) {
            showErrorDialog(getActivity(), bce.getMessage());
        }
        final Integer tripPosition = getArguments().getInt(ARG_PARAM1);
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                trip = tripsModel.getFilteredTripAtPosition(tripPosition);
                hasReturnSearch = tripsModel.hasReturnTrips();
                isReturnSearch = tripsModel.isReturnSearch();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip, container, false);

        TextView company = view.findViewById(R.id.trip_fragment_company);
        TextView date = view.findViewById(R.id.trip_fragment_date);
        TextView price = view.findViewById(R.id.trip_price);
        final Button btnBookTrip = view.findViewById(R.id.btn_book_trip);
        btnBookTrip.setVisibility(View.VISIBLE);

        populateStopsLayout(inflater, container, view);
        company.setText(trip.getCompanyName());
        date.setText(trip.getFormattedDate());
        price.setText(String.format(Locale.getDefault(), "$%.2f", trip.getPrice()));

        if (hasReturnSearch && !isReturnSearch) {
            btnBookTrip.setText(getResources().getString(R.string.book_trip_and_search_return));
        } else {
            btnBookTrip.setText(getResources().getString(R.string.book_trip));
        }

        btnBookTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reservationsModel.isTripBooked(trip, username)) {
                    showErrorDialog(getActivity(), "Ya posee una reserva para este viaje.");
                } else {
                    chooseQtyOfSeatsAndConfirm();
                }
            }
        });

        return view;
    }

    private void populateStopsLayout(LayoutInflater inflater, ViewGroup container, View view) {
        LinearLayout stopsLayout = view.findViewById(R.id.stops_layout);
        LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        for (TripStop tripStop: trip.getStops()) {
            TextView stopDesc = (TextView) inflater.inflate(R.layout.stop_user, container, false);
            if (tripStop.getDescription().equalsIgnoreCase(tripsModel.getArgTripOriginHopOnStop())
                    || tripStop.getDescription().equalsIgnoreCase(
                    tripsModel.getArgTripDestinationHopOnStop())) {
                stopDesc.setTypeface(stopDesc.getTypeface(), Typeface.BOLD);
            }
            stopDesc.setText(String.format("\u2022 %s - %s", tripStop.getDescription(),
                    tripStop.getHour().toString(tf)));
            stopDesc.setLayoutParams(lparams);
            stopsLayout.addView(stopDesc);
        }
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
                    setNextFragment();
                } else {
                    showWaitListDialog(getActivity(), seatsQty);
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

    public void showWaitListDialog(Activity activity, final Integer seatsQty) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setMessage("No hay asientos disponibles. Desea anotarse en lista de espera?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bookTrip(seatsQty, true);
                        setNextFragment();
                    }
                })
                .setNegativeButton("No", null)
                .create();
        alertDialog.show();
    }

    private void bookTrip(int seatsQty, boolean isWaitList) {
        String argTripHopOnStop = tripsModel.getArgTripHopOnStop();
        Integer hopOnStopId = trip.getTripStopByDescription(argTripHopOnStop).getId();
        try {
            reservationsModel.createReservationForTrip(trip, seatsQty, hopOnStopId, username, isWaitList);
            subscribeToTripTopic(isWaitList);
        } catch (BackendException be) {
            showErrorDialog(getActivity(), be.getErrorMsg());
        } catch (BackendConnectionException bce) {
            showErrorDialog(getActivity(), bce.getMessage());
        }
    }

    private void subscribeToTripTopic(boolean isInWaitList) {
        String tripTopic = "trip__" + String.valueOf(trip.get_id());
        String superTripTopic = "super_trip__" + String.valueOf(trip.getTripSuperId());

        FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
        firebaseMessaging.subscribeToTopic(tripTopic);
        firebaseMessaging.subscribeToTopic(superTripTopic);

        if (isInWaitList) {
            try {
                Integer userId = UsersViewModel.getInstance().getActualUserId();
                String topicPrefix = String.format("user_%d_wait_list_trip__", userId)
                        .toLowerCase().replaceAll("@", "");
                String tripWaitListTopic = topicPrefix + String.valueOf(trip.get_id());
                firebaseMessaging.subscribeToTopic(tripWaitListTopic);
            } catch (BackendException be) {
                showErrorDialog(getActivity(), be.getErrorMsg());
            } catch (BackendConnectionException bce) {
                showErrorDialog(getActivity(), bce.getMessage());
            }
        }
    }

    private void setNextFragment() {
        Fragment newFragment;
        if (hasReturnSearch && !isReturnSearch) {
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

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = null;
        if (activity != null) {
            actionBar = activity.getSupportActionBar();
        }
        if (actionBar != null) {
            actionBar.setTitle(R.string.trip_detail);
        }
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
