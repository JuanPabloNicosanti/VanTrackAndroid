package mainFunctionality.reservations;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessaging;
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

import mainFunctionality.CentralActivity;
import mainFunctionality.viewsModels.TripsReservationsViewModel;
import mainFunctionality.localization.MapsActivityUser;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.VanTrackApplication;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.mainFunctionality.search.TripStop;
import utn.proy2k18.vantrack.models.Reservation;
import utn.proy2k18.vantrack.utils.QueryBuilder;

//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;

public class ReservationActivity extends AppCompatActivity {
    private String PUBLIC_KEY="TEST-661496e3-25fc-46c5-a4c8-4d05f64f5936";
    //    private String ACCESS_TOKEN="TEST-5222723668192320-090920-796f2538a130ff517ec2e1740e5d3e4d-353030546";

    private static final String ARG_PARAM1 = "position";
    private static final String ARG_PARAM2 = "paymentStatus";
    private int position;
    private String paymentStatus;
    private TripsReservationsViewModel model;
    private Reservation reservation;
    private QueryBuilder queryBuilder = new QueryBuilder();
    private Button btnPayReservation;
    final Activity activity = this;
    private DateTimeFormatter tf = DateTimeFormat.forPattern("HH:mm");
    private int oldHopOnStopPos;
    private String username = "lucas.lopez@gmail.com";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            position = b.getInt(ARG_PARAM1);
            paymentStatus = b.getString(ARG_PARAM2, "NO_STATUS");
        }
        model = TripsReservationsViewModel.getInstance();
        reservation = model.getReservationAtPosition(position);

        TextView origin = findViewById(R.id.reservation_fragment_origin);
        TextView destination = findViewById(R.id.reservation_fragment_destination);
        TextView company = findViewById(R.id.reservation_fragment_company);
        TextView seatsQty = findViewById(R.id.seats_qty_text_view);
        TextView date = findViewById(R.id.reservation_fragment_date);
        TextView time = findViewById(R.id.reservation_fragment_time);
        TextView price = findViewById(R.id.reservation_price);
        TextView stops = findViewById(R.id.trip_fragment_stops);

        Button btnCancelTrip = findViewById(R.id.btn_cancel_booking);
        btnPayReservation = findViewById(R.id.btn_pay_booking);
        Button btn_map_trip = findViewById(R.id.btn_map_booking);
        final Spinner stopsSpinner = findViewById(R.id.hop_on_stop_spinner);

        if (paymentStatus.equals("approved")) {
            reservation.payBooking();
            btnPayReservation.setVisibility(View.GONE);
        } else {
            if (reservation.isPaid()) {
                btnPayReservation.setVisibility(View.GONE);
            }
        }

        final Trip bookedTrip =  reservation.getBookedTrip();
        final Activity activity = this;
        final ArrayList<String> tripStopsDesc = createStopsDescriptionArray(bookedTrip);
        ArrayAdapter<String> stopsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, tripStopsDesc);
        stopsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stopsSpinner.setAdapter(stopsAdapter);

        oldHopOnStopPos = stopsAdapter.getPosition(reservation.getHopOnStop().getDescription());
        stopsSpinner.setSelection(oldHopOnStopPos);

        stopsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1, final int spinnerPos, long id) {
                final String newHopOnStopDesc = parent.getItemAtPosition(spinnerPos).toString();
                if (!reservation.getHopOnStop().getDescription().equals(newHopOnStopDesc)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage("Desea cambiar el punto en el que subirá a la combi?")
                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int position1) {
                                    modifyReservationHopOnStop(spinnerPos, newHopOnStopDesc);
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

        origin.setText(bookedTrip.getOrigin());
        destination.setText(bookedTrip.getDestination());
        company.setText(bookedTrip.getCompanyName());
        seatsQty.setText(String.valueOf(reservation.getTravelersQty()));
        date.setText(bookedTrip.getDate().toString());
        time.setText(bookedTrip.getTime().toString(tf));
        price.setText(String.valueOf(reservation.getReservationPrice()));
        stops.setText(bookedTrip.createStrStops());

        btnCancelTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Desea eliminar el Viaje?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position1) {
                                unsubscribeFromTripTopic(reservation.getBookedTrip());
                                model.deleteReservation(reservation, username);

                                // TODO: should pass VanTrackApplication user as param
                                Intent intent = new Intent(activity, CentralActivity.class);
                                startActivity(intent);
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
                payReservation();
            }
        });

        btn_map_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyGPSIsEnabledAndGetLocation(reservation.getBookedTrip());
            }
        });
    }

    private void modifyReservationHopOnStop(int spinnerPos, String newHopOnStopDesc) {
        TripStop newHopOnStop = reservation.getHopOnStopByDescription(newHopOnStopDesc);
        String result = model.modifyReservationHopOnStop(reservation.get_id(),
                newHopOnStop.getId());
        if (result.equals("200")) {
            reservation.setHopOnStop(newHopOnStop);
            oldHopOnStopPos = spinnerPos;
        } else {
            showErrorDialog(activity, "Error al realizar el cambio en la reserva");
        }
    }

    private ArrayList<String> createStopsDescriptionArray(Trip trip) {
        ArrayList<String> stopsDescriptions = new ArrayList<>();
        for (TripStop tripStop: trip.getStops()) {
            stopsDescriptions.add(tripStop.getDescription());
        }
        return stopsDescriptions;
    }

    public void showErrorDialog(Activity activity, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message)
                .setNeutralButton("Aceptar",null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void unsubscribeFromTripTopic(Trip trip) {
        // topic string should be the trip unique id declared in DB
        String topic = "trips__" + String.valueOf(trip.get_id());
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
    }

    private void verifyGPSIsEnabledAndGetLocation(Trip trip){
        final LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LatLng mOrigin = new LatLng(reservation.getHopOnStop().getLatitude(),reservation.getHopOnStop().getLongitude());
        LatLng mDestination = trip.getLatLngDestination(trip.getDestination());
        Intent intent = new Intent(this, MapsActivityUser.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("trip", trip);
        bundle.putParcelable("origin", mOrigin);
        bundle.putParcelable("destination", mDestination);
        intent.putExtras(bundle);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            this.showGPSDisabledAlertToUser();
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
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
                                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
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
        CheckoutPreference preference = model.createCheckoutPreference(preferenceMap);
        LayoutUtil.showProgressLayout(activity);

        if(preference != null) {
            startMercadoPagoCheckout(preference);
            LayoutUtil.showRegularLayout(activity);
        } else {
            System.out.println("Error en la creación de la preferencia de pago.");
            showErrorDialog(activity, "Error al realizar el pago. Inténtelo más tarde.");
        }
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
        preferenceMap.put("payer_email", ((VanTrackApplication) this.getApplication()).getUser().getEmail());
        preferenceMap.put("payer_name", ((VanTrackApplication) this.getApplication()).getUser().getDisplayName());

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
                Payment payment = JsonUtil.getInstance().fromJson(data.getStringExtra("payment"), Payment.class);
                if (payment.getStatus().equals("approved") || payment.getStatus().equals("pending")
                        || payment.getStatus().equals("in_process")) {
                    btnPayReservation.setVisibility(View.GONE);
                }
            } else if (resultCode == RESULT_CANCELED) {
                if (data != null && data.getStringExtra("mercadoPagoError") != null) {
                    MercadoPagoError mercadoPagoError = JsonUtil.getInstance().fromJson(data.getStringExtra("mercadoPagoError"), MercadoPagoError.class);
                    System.out.println("Error en el pago:");
                    System.out.println(mercadoPagoError.toString());
                } else {
                    //Resolve canceled checkout
                }
            }
        }
    }
}
