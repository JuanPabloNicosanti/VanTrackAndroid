package mainFunctionality.reservations;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.connector.HttpConnector;
import utn.proy2k18.vantrack.models.Rating;
import utn.proy2k18.vantrack.utils.JacksonSerializer;
import utn.proy2k18.vantrack.utils.QueryBuilder;

public class ScoreActivity extends AppCompatActivity {

    private float tripRating;
    private float driverRating;
    private int reservationId;
    private QueryBuilder queryBuilder = new QueryBuilder();
    private static final ObjectMapper objectMapper = JacksonSerializer.getObjectMapper();
    private static final String HTTP_PUT = "PUT";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        setTitle("Calificar");

        Bundle b = getIntent().getExtras();
        if (b != null) {
            reservationId = b.getInt("reservationId");
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
                tripRating = rating*2;
                int roundRating = Math.round(rating);
                switch (roundRating) {
                    case 0: txtTripRating.setText(R.string.very_bad_score);
                    break;
                    case 1: txtTripRating.setText(R.string.bad_score);
                    break;
                    case 2: txtTripRating.setText(R.string.regular_score);
                    break;
                    case 3: txtTripRating.setText(R.string.good_score);
                    break;
                    case 4: txtTripRating.setText(R.string.very_good_score);
                    break;
                    case 5: txtTripRating.setText(R.string.excellent_score);
                    break;
                    default: txtTripRating.setText("");
                }
            }
        });

        driverRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                driverRating = rating*2;
                int roundRating = Math.round(rating);
                switch(roundRating) {
                    case 0: txtDriverRating.setText(R.string.very_bad_score);
                        break;
                    case 1: txtDriverRating.setText(R.string.bad_score);
                        break;
                    case 2: txtDriverRating.setText(R.string.regular_score);
                        break;
                    case 3: txtDriverRating.setText(R.string.good_score);
                        break;
                    case 4: txtDriverRating.setText(R.string.very_good_score);
                        break;
                    case 5: txtDriverRating.setText(R.string.excellent_score);
                        break;
                    default: txtDriverRating.setText("");
                }
            }
        });
    }

    public void addListenerOnButton() {

        Button btnSubmit = findViewById(R.id.btn_submit_score);

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText additionalComment = findViewById(R.id.et_additional_comment);
                HashMap<String, String> payload = new HashMap<>();
                payload.put("reservation_id", String.valueOf(reservationId));
                payload.put("trip_rating", String.valueOf(tripRating));
                payload.put("driver_rating", String.valueOf(driverRating));
                payload.put("comment", additionalComment.getText().toString());

                final HttpConnector HTTP_CONNECTOR = new HttpConnector();
                try{
                    String url = queryBuilder.getCreateRatingUri(payload);
                    String result = HTTP_CONNECTOR.execute(url, HTTP_PUT).get();
                    // TODO: add exception handling when failing to create rating
                    TypeReference resType = new TypeReference<Rating>(){};
                    Rating newRating = objectMapper.readValue(result, resType);
                } catch (ExecutionException ee){
                    ee.printStackTrace();
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
