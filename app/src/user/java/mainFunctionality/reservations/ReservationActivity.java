package mainFunctionality.reservations;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mercadopago.core.MercadoPagoCheckout;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.Payment;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import mainFunctionality.CentralActivity;
import mainFunctionality.localization.MapsActivityUser;
import mainFunctionality.viewsModels.TripsReservationsViewModel;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.exceptions.BackendConnectionException;
import utn.proy2k18.vantrack.exceptions.BackendException;
import utn.proy2k18.vantrack.exceptions.FailedToPayReservationException;
import utn.proy2k18.vantrack.exceptions.FailedToDeleteReservationException;
import utn.proy2k18.vantrack.exceptions.FailedToModifyReservationException;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.mainFunctionality.search.TripStop;
import utn.proy2k18.vantrack.models.Reservation;
import utn.proy2k18.vantrack.viewModels.UsersViewModel;


public class ReservationActivity extends AppCompatActivity {

    private static final String PUBLIC_KEY="TEST-661496e3-25fc-46c5-a4c8-4d05f64f5936";
    private static final String ARG_PARAM1 = "reservation_id";

    private int reservationId;
    private TripsReservationsViewModel model;
    private Reservation reservation;
    private Button btnPayReservation;
    final Activity activity = this;
    private DateTimeFormatter tf = DateTimeFormat.forPattern("HH:mm");
    private DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy");
    private int oldHopOnStopPos;
    private Spinner stopsSpinner;
    private TextView status;
    private FirebaseUser user;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            reservationId = b.getInt(ARG_PARAM1);
        }
        user = FirebaseAuth.getInstance().getCurrentUser();
        model = TripsReservationsViewModel.getInstance();
        reservation = model.getReservationById(reservationId, user.getEmail());

        TextView company = findViewById(R.id.reservation_fragment_company);
        TextView seatsQty = findViewById(R.id.seats_qty_text_view);
        TextView date = findViewById(R.id.reservation_fragment_date);
        TextView price = findViewById(R.id.reservation_price);
        status = findViewById(R.id.reservation_fragment_status);

        Button btnCancelReservation = findViewById(R.id.btn_cancel_booking);
        btnPayReservation = findViewById(R.id.btn_pay_booking);
        Button btnMapTrip = findViewById(R.id.btn_map_booking);
        Button btnScoreTrip = findViewById(R.id.btn_rate_booking);
        stopsSpinner = findViewById(R.id.hop_on_stop_spinner);

        final Trip bookedTrip =  reservation.getBookedTrip();
        final Activity activity = this;
        final ArrayList<String> tripStopsDesc = createStopsDescriptionArray(bookedTrip);
        ArrayAdapter<String> stopsAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_stop_item, tripStopsDesc);
        stopsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stopsSpinner.setAdapter(stopsAdapter);

        oldHopOnStopPos = stopsAdapter.getPosition(reservation.getHopOnStop().getDescription());
        stopsSpinner.setSelection(oldHopOnStopPos);

        stopsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1, final int spinnerPos,
                                       long id) {
                final String newHopOnStopDesc = parent.getItemAtPosition(spinnerPos).toString();
                if (!reservation.getHopOnStop().getDescription().equals(newHopOnStopDesc)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage("Desea cambiar el punto en el que subirá a la combi?")
                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int position1) {
                                    try {
                                        modifyReservationHopOnStop(spinnerPos, newHopOnStopDesc);
                                    } catch (FailedToModifyReservationException fmre) {
                                        stopsSpinner.setSelection(oldHopOnStopPos);
                                        dialog.dismiss();
                                        showErrorDialog(activity, fmre.getMessage());
                                    }
                                }
                            })
                            .setNegativeButton("No",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int position1) {
                                    stopsSpinner.setSelection(oldHopOnStopPos);
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        company.setText(bookedTrip.getCompanyName());
        seatsQty.setText(String.valueOf(reservation.getTravelersQty()));
        date.setText(bookedTrip.getDate().toString(dtf));
        price.setText(String.format(Locale.getDefault(), "$%.2f",
                reservation.getReservationPrice()));
        populateStopsLayout();

        //Visualization logic
        if (reservation.isPendingReservation()) {
            status.setText(getResources().getString(R.string.isPending));
            status.setTextColor(Color.RED);
        } else {
            if (reservation.isPaid()) {
                status.setText(getResources().getString(R.string.paid_reservation));
            } else {
                status.setText(getResources().getString(R.string.confirmed_reservation));
            }
            status.setTextColor(Color.GREEN);
        }

        Integer minutesForTripDeparture = bookedTrip.minutesForTripDeparture();
        if (bookedTrip.isFinished() || minutesForTripDeparture > 60 ||
                reservation.isPendingReservation())
            btnMapTrip.setVisibility(View.GONE);

        if (reservation.isPendingReservation() || reservation.isPaid()
                || minutesForTripDeparture < -10) {
            btnPayReservation.setVisibility(View.GONE);
        }

        if (minutesForTripDeparture < 60 && !reservation.isPendingReservation()) {
            btnCancelReservation.setVisibility(View.GONE);
        }

        if (minutesForTripDeparture <= 0) {
            stopsSpinner.setEnabled(false);
        }

        if (!bookedTrip.isFinished() || reservation.isPendingReservation())
            btnScoreTrip.setVisibility(View.GONE);

        btnCancelReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    cancelReservation();
                } catch (FailedToDeleteReservationException fdre) {
                    showErrorDialog(activity, fdre.getMessage());
                }
            }
        });

        btnPayReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payReservation();
            }
        });

        btnMapTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyGPSIsEnabledAndGetLocation();
            }
        });

        btnScoreTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReservationActivity.this,
                        ScoreActivity.class);
                intent.putExtra("reservationId", reservation.get_id());
                startActivity(intent);
            }
        });
    }

    private void cancelReservation() {
        final List<CancellationCause> cancellationCauses = model.getCancellationCauses();
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.cancel_reservation_dialog);
        RadioGroup radioGroup = dialog.findViewById(R.id.cancel_reservation_reasons_radio_group);
        Button confirmButton = dialog.findViewById(R.id.confirm_reservation_cancellation_button);
        Button cancelButton = dialog.findViewById(R.id.cancel_reservation_cancellation_button);

        for(CancellationCause cancellationCause : cancellationCauses){
            RadioButton rb = new RadioButton(this);
            rb.setText(capitalize(cancellationCause.getDescription()));
            radioGroup.addView(rb);
        }
        RadioButton firstRB = (RadioButton) radioGroup.getChildAt(0);
        firstRB.setChecked(true);

        confirmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                while (selectedId > radioGroup.getChildCount()) {
                    selectedId -= radioGroup.getChildCount();
                }
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(selectedId-1);
                CancellationCause cc = model.getCancellationCauseByDescription(
                        radioButton.getText().toString());
                unsubscribeFromTripTopic();
                model.deleteReservation(reservation, user.getEmail(), cc);
                Toast.makeText(activity, R.string.cancelled_reservation, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                Intent intent = new Intent(activity, CentralActivity.class);
                startActivity(intent);
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

    private String capitalize(String string) {
        return string.substring(0,1).toUpperCase() +
                string.substring(1).toLowerCase();
    }

    private void populateStopsLayout() {
        LinearLayout stopsLayout = findViewById(R.id.stops_layout);

        for (TripStop tripStop: reservation.getBookedTrip().getStops()) {
            LinearLayout stopLayout = (LinearLayout) getLayoutInflater().inflate(
                    R.layout.stop_layout,null);

            TextView stopDesc = (TextView) stopLayout.getChildAt(1);
            stopDesc.setText(String.format("\u2022 %s", tripStop.getDescription()));
            TextView stopTime = (TextView) stopLayout.getChildAt(2);
            stopTime.setText(tripStop.getHour().toString(tf));

            if (tripStop.getDescription().equalsIgnoreCase(reservation.getHopOnStop()
                    .getDescription())) {
                stopDesc.setTypeface(stopDesc.getTypeface(), Typeface.BOLD);
                stopTime.setTypeface(stopTime.getTypeface(), Typeface.BOLD);
            }
            stopsLayout.addView(stopLayout);
        }
    }

    private void modifyReservationHopOnStop(int spinnerPos, String newHopOnStopDesc) {
        String oldHopOnStopDesc = reservation.getHopOnStop().getDescription();
        TripStop newHopOnStop = reservation.getBookedTrip().getTripStopByDescription(
                newHopOnStopDesc);
        model.modifyReservationHopOnStop(reservation, newHopOnStop);
        oldHopOnStopPos = spinnerPos;
        updateRouteTextView(newHopOnStopDesc, oldHopOnStopDesc);
    }

    private void updateRouteTextView(String newStopDesc, String oldStopDesc) {
        LinearLayout stopsLayout = findViewById(R.id.stops_layout);
        for (int i=0; i < stopsLayout.getChildCount(); i++) {
            LinearLayout stopLayout = (LinearLayout) stopsLayout.getChildAt(i);
            TextView stopTv = (TextView) stopLayout.getChildAt(1);
            TextView stopTime = (TextView) stopLayout.getChildAt(2);
            String stopDesc = stopTv.getText().toString().replace("\u2022 ", "");
            if (stopDesc.equalsIgnoreCase(newStopDesc)) {
                stopTv.setTypeface(stopTv.getTypeface(), Typeface.BOLD);
                stopTime.setTypeface(stopTime.getTypeface(), Typeface.BOLD);
            }
            if (stopDesc.equalsIgnoreCase(oldStopDesc)) {
                stopTv.setTypeface(null, Typeface.NORMAL);
                stopTime.setTypeface(null, Typeface.NORMAL);
            }
        }
    }

    private ArrayList<String> createStopsDescriptionArray(Trip trip) {
        ArrayList<String> stopsDescriptions = new ArrayList<>();
        for (TripStop tripStop: trip.getStops()) {
            if (!tripStop.getDescription().equals(trip.getDestination())){
                stopsDescriptions.add(tripStop.getDescription());
            }
        }
        return stopsDescriptions;
    }

    public void showErrorDialog(Activity activity, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setMessage(message)
                .setNeutralButton("Aceptar",null)
                .create();
        alertDialog.show();
    }

    private void unsubscribeFromTripTopic() {
        Trip bookedTrip = reservation.getBookedTrip();
        String tripTopic = "trip__" + String.valueOf(bookedTrip.get_id());
        String superTripTopic = "super_trip__" + String.valueOf(bookedTrip.getTripSuperId());

        FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
        firebaseMessaging.unsubscribeFromTopic(tripTopic);
        firebaseMessaging.unsubscribeFromTopic(superTripTopic);

        if (reservation.isPendingReservation()) {
            Integer userId = UsersViewModel.getInstance().getActualUserId(user.getEmail());
            String topicPrefix = String.format("user_%d_wait_list_trip__", userId)
                    .toLowerCase().replaceAll("@", "");
            String tripWaitListTopic = topicPrefix + String.valueOf(bookedTrip.get_id());
            firebaseMessaging.unsubscribeFromTopic(tripWaitListTopic);
        }
    }

    private void verifyGPSIsEnabledAndGetLocation(){
        final LocationManager manager = (LocationManager) this.getSystemService(
                Context.LOCATION_SERVICE);
        Intent intent = new Intent(this, MapsActivityUser.class);
        Bundle bundle = new Bundle();
        bundle.putInt("reservation_id", reservation.get_id());
        intent.putExtras(bundle);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            this.showGPSDisabledAlertToUser();
        else
            startActivity(intent);
    }

    private void showGPSDisabledAlertToUser() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder
                .setTitle(R.string.location_alert_title)
                .setMessage(R.string.location_alert_content)
                .setCancelable(false)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener(){
                            public void onClick(final DialogInterface dialog, final int id) {
                                Intent intent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        })
                .setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                dialog.cancel();
                            }
                        })
                .show();
    }

    private void payReservation() {
        HashMap<String, Object> preferenceMap = createPreferenceMap();
        CheckoutPreference preference = null;
        try {
            preference = model.createCheckoutPreference(preferenceMap);
        } catch (FailedToPayReservationException fccpe) {
            showErrorDialog(activity, fccpe.getMessage());
        }
        LayoutUtil.showProgressLayout(activity);

        startMercadoPagoCheckout(preference);
        LayoutUtil.showRegularLayout(activity);
    }

    private HashMap<String, Object> createPreferenceMap() {
        Trip bookedTrip = reservation.getBookedTrip();
        HashMap<String, Object> preferenceMap = new HashMap<>();
        final String title = "Viaje de " + bookedTrip.getOrigin() + " a " +
                bookedTrip.getDestination() + " por " + bookedTrip.getCompanyName();
        preferenceMap.put("item_id", String.valueOf(reservation.get_id()));
        preferenceMap.put("item_price", reservation.getReservationPrice());
        preferenceMap.put("item_title", title);
        preferenceMap.put("quantity", 1);
        preferenceMap.put("payer_email", user.getEmail());
        preferenceMap.put("payer_name", user.getDisplayName());  //TODO: this comes empty

        return preferenceMap;
    }

    private void startMercadoPagoCheckout(CheckoutPreference checkoutPreference) {
        new MercadoPagoCheckout.Builder()
                .setActivity(this)
                .setPublicKey(PUBLIC_KEY)
                .setCheckoutPreference(checkoutPreference)
                .startForPayment();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MercadoPagoCheckout.CHECKOUT_REQUEST_CODE) {
            if (resultCode == MercadoPagoCheckout.PAYMENT_RESULT_CODE) {
                Payment payment = JsonUtil.getInstance().fromJson(data.getStringExtra("payment"),
                        Payment.class);
                if (payment.getStatus().equals("approved")) {
                    try {
                        model.payReservation(reservation, payment);
                    } catch (FailedToPayReservationException fpre) {
                        // TODO: BE CAREFUL! If this fails but MP payment was OK, it might be a problem.
                        showErrorDialog(activity, fpre.getMessage());
                    }
                }
                if (payment.getStatus().equals("approved") || payment.getStatus().equals("pending")
                        || payment.getStatus().equals("in_process")) {
                    btnPayReservation.setVisibility(View.GONE);
                    status.setText(getResources().getString(R.string.paid_reservation));
                }
            } else if (resultCode == RESULT_CANCELED) {
                if (data != null && data.getStringExtra("mercadoPagoError") != null) {
                    MercadoPagoError mercadoPagoError = JsonUtil.getInstance().fromJson(
                            data.getStringExtra("mercadoPagoError"), MercadoPagoError.class);
                    showErrorDialog(activity, "Error en el pago");
                    System.out.println("Error en el pago:");
                    System.out.println(mercadoPagoError.toString());
                } else {
                    //Resolve canceled checkout
                }
            }
        }
    }
}
