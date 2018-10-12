package mainFunctionality.reservations;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import mainFunctionality.CentralActivity;
import mainFunctionality.viewsModels.TripsReservationsViewModel;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.connector.HttpConnector;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.models.Rating;
import utn.proy2k18.vantrack.models.Reservation;
import utn.proy2k18.vantrack.utils.JacksonSerializer;
import utn.proy2k18.vantrack.utils.QueryBuilder;

public class ScoreActivity extends AppCompatActivity {

    private int tripRating;
    private int driverRating;
    private Reservation reservation;
    private QueryBuilder queryBuilder = new QueryBuilder();
    private static final ObjectMapper objectMapper = JacksonSerializer.getObjectMapper();
    private static final String HTTP_POST = "POST";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        setTitle("Calificar");

        Bundle b = getIntent().getExtras();
        if (b != null) {
            reservation = b.getParcelable("reservation");
        }

        addListenerOnRatingBar();
        addListenerOnButton();
    }

    public void addListenerOnRatingBar() {

        RatingBar tripRatingBar = findViewById(R.id.score_trip_stars);
        RatingBar driverRatingBar = findViewById(R.id.score_driver_stars);
        final TextView txtTripRating =  findViewById(R.id.txtTripRatingBar);
        final TextView txtDriverRating =  findViewById(R.id.txtDriverRatingBar);

        //if rating value is changed,
        //display the current rating value in the result automatically
        tripRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                assignRateAndChangeText(rating, txtTripRating, 0);
            }
        });

        driverRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
               assignRateAndChangeText(rating, txtDriverRating, 1);
            }
        });
    }

    public void assignRateAndChangeText(float realRating, TextView textView, int flag){
        if(flag == 0)
            tripRating = Math.round(realRating*2);
        else if(flag ==1)
            driverRating = Math.round(realRating *2);

        int roundRating = Math.round(realRating);
        switch(roundRating) {
            case 0: textView.setText(R.string.very_bad_score);
                break;
            case 1: textView.setText(R.string.bad_score);
                break;
            case 2: textView.setText(R.string.regular_score);
                break;
            case 3: textView.setText(R.string.good_score);
                break;
            case 4: textView.setText(R.string.very_good_score);
                break;
            case 5: textView.setText(R.string.excellent_score);
                break;
            default: textView.setText("");
        }
    }
    public void addListenerOnButton() {

        Button btnSubmit = findViewById(R.id.btn_submit_score);

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText additionalComment = findViewById(R.id.et_additional_comment);
                final HttpConnector HTTP_CONNECTOR = new HttpConnector();
                try{
                    Rating score = new Rating(tripRating, driverRating, additionalComment.getText().toString());
                    String body = objectMapper.writeValueAsString(score);
                    String url = queryBuilder.getCreateRatingUri(String.valueOf(reservation.get_id()));
                    String result = HTTP_CONNECTOR.execute(url, HTTP_POST, body).get();
                    // TODO: add exception handling when failing to create rating
                    TypeReference resType = new TypeReference<Rating>(){};
                    Intent intent = new Intent(ScoreActivity.this, CentralActivity.class);
                    startActivity(intent);
                } catch (ExecutionException ee){
                    ee.printStackTrace();
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
        }

    });

    }

    private void unsubscribeFromTripTopic(Trip trip) {
        // topic string should be the trip unique id declared in DB
        String topic = "trips__" + String.valueOf(trip.get_id());
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
    }
}
