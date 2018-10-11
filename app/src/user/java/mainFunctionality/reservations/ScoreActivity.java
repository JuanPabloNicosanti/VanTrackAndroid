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

import retrofit2.http.HTTP;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.connector.HttpConnector;
import utn.proy2k18.vantrack.models.Rating;
import utn.proy2k18.vantrack.utils.JacksonSerializer;
import utn.proy2k18.vantrack.utils.QueryBuilder;

public class ScoreActivity extends AppCompatActivity {

    private int tripRating;
    private int driverRating;
    private int reservationId;
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
                HashMap<String, String> payload = new HashMap<>();
                payload.put("driver_rating", String.valueOf(driverRating));
                payload.put("service_rating", String.valueOf(tripRating));
                final HttpConnector HTTP_CONNECTOR = new HttpConnector();
                try{
                    String url = queryBuilder.getCreateRatingUri(String.valueOf(reservationId), payload);
                    String result = HTTP_CONNECTOR.execute(url, HTTP_POST, additionalComment.getText().toString()).get();
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
