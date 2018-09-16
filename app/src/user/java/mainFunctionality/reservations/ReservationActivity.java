package mainFunctionality.reservations;

import android.app.Activity;
//import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.core.CustomServer;
import com.mercadopago.core.MercadoPagoCheckout;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Payment;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;

import java.util.HashMap;
import java.util.Map;

//import mainFunctionality.viewsModels.TripsReservationsViewModel;
import mainFunctionality.localization.MapsActivityUser;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.VanTrackApplication;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.reservations.Reservation;
import utn.proy2k18.vantrack.utils.QueryBuilder;

public class ReservationActivity extends AppCompatActivity {
    private String PUBLIC_KEY="TEST-661496e3-25fc-46c5-a4c8-4d05f64f5936";
    //    private String ACCESS_TOKEN="TEST-5222723668192320-090920-796f2538a130ff517ec2e1740e5d3e4d-353030546";

    private static final String ARG_PARAM1 = "reservation";
    private static final String ARG_PARAM2 = "paymentStatus";
    private String paymentStatus;
//    private TripsReservationsViewModel model;
    private Reservation reservation;
    private QueryBuilder queryBuilder = new QueryBuilder();
    private Button btnPayReservation;
    final Activity activity = this;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            reservation = b.getParcelable(ARG_PARAM1);
            paymentStatus = b.getString(ARG_PARAM2, "NO_STATUS");
        }
//        model = ViewModelProviders.of(getActivity()).get(TripsReservationsViewModel.class);

        TextView origin = findViewById(R.id.reservation_fragment_origin);
        TextView destination = findViewById(R.id.reservation_fragment_destination);
        TextView company = findViewById(R.id.reservation_fragment_company);
        TextView date = findViewById(R.id.reservation_fragment_date);
        TextView time = findViewById(R.id.reservation_fragment_time);
        TextView price = findViewById(R.id.reservation_price);
        Button btnCancelTrip = findViewById(R.id.btn_cancel_booking);
        btnPayReservation = findViewById(R.id.btn_pay_booking);
        Button btn_map_trip = findViewById(R.id.btn_map_booking);

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

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Desea eliminar el Viaje?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position1) {
                                unsubscribeFromTripTopic(reservation.getBookedTrip());
//                                model.deleteReservation(reservation.get_id());

//                                FragmentManager fm = getSupportFragmentManager();
//                                FragmentTransaction ft = fm.beginTransaction();
//                                ft.replace(R.id.fragment_container, new MyReservationsFragment());
//                                ft.commit();
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
                submit();
            }
        });

        btn_map_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyGPSIsEnabledAndGetLocation(reservation.getBookedTrip().get_id());
            }
        });
    }

    private void unsubscribeFromTripTopic(Trip trip) {
        // topic string should be the trip unique id declared in DB
        String topic = "trips__" + trip.get_id();
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
    }

    private void verifyGPSIsEnabledAndGetLocation(String tripId){
        final LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Intent intent = new Intent(this ,MapsActivityUser.class);
        Bundle bundle = new Bundle();
        bundle.putString("tripId", tripId);
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

    public void submit() {
        Map<String, Object> preferenceMap = new HashMap<>();
        final String title = "Viaje de " + reservation.getTripOrigin() + " a " +
                reservation.getTripDestination() + " por " + reservation.getTripCompanyName();
        preferenceMap.put("item_id", reservation.get_id());
        preferenceMap.put("item_price", reservation.getPrice());
        preferenceMap.put("item_title", title);
        preferenceMap.put("quantity", 1);
        preferenceMap.put("payer_email", ((VanTrackApplication) this.getApplication()).getUser().getEmail());
        preferenceMap.put("payer_name", ((VanTrackApplication) this.getApplication()).getUser().getDisplayName());

        LayoutUtil.showProgressLayout(activity);
        CustomServer.createCheckoutPreference(activity, queryBuilder.getBaseUrl(),
                queryBuilder.getPaymentsUri(), preferenceMap, new Callback<CheckoutPreference>() {
                    @Override
                    public void success(CheckoutPreference checkoutPreference) {
                        startMercadoPagoCheckout(checkoutPreference);
                        LayoutUtil.showRegularLayout(activity);
                    }

                    @Override
                    public void failure(ApiException apiException) {
                        System.out.println("Fallo al realizar el pago:");
                        System.out.println(apiException.getMessage());
                    }
                });
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
