package mainFunctionality.reservations;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

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

import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.VanTrackApplication;
import utn.proy2k18.vantrack.utils.QueryBuilder;

public class PaymentActivity extends AppCompatActivity {
    private String PUBLIC_KEY="TEST-661496e3-25fc-46c5-a4c8-4d05f64f5936";
//    private String ACCESS_TOKEN="TEST-7052649783174585-082122-45bff91292a5ff2a966379591121df97-172493186";
    private String reservationId;
    private float reservationPrice;
    private QueryBuilder queryBuilder = new QueryBuilder();

    public PaymentActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        if(b != null)
            reservationId = b.getString("reservation_id");
            reservationPrice = b.getFloat("reservation_price");

        setContentView(R.layout.activity_payment);
    }

    public void submit(View view) {
        Map<String, Object> preferenceMap = new HashMap<>();
        preferenceMap.put("item_id", reservationId);
        preferenceMap.put("item_price", reservationPrice);
        preferenceMap.put("quantity", 1);
        preferenceMap.put("payer_email", ((VanTrackApplication) this.getApplication()).getUser().getEmail());
        preferenceMap.put("payer_name", ((VanTrackApplication) this.getApplication()).getUser().getDisplayName());

        final Activity activity = this;
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
                ((TextView) findViewById(R.id.mp_results)).setText("Resultado del pago: " + payment.getStatus());
                //Done!
            } else if (resultCode == RESULT_CANCELED) {
                if (data != null && data.getStringExtra("mercadoPagoError") != null) {
                    MercadoPagoError mercadoPagoError = JsonUtil.getInstance().fromJson(data.getStringExtra("mercadoPagoError"), MercadoPagoError.class);
                    ((TextView) findViewById(R.id.mp_results)).setText("Error: " +  mercadoPagoError.getMessage());
                    System.out.println("Error en el pago:");
                    System.out.println(mercadoPagoError.toString());
                    //Resolve error in checkout
                } else {
                    //Resolve canceled checkout
                }
            }
        }
    }
}
