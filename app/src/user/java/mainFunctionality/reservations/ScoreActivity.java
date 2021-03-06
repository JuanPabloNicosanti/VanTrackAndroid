package mainFunctionality.reservations;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import mainFunctionality.CentralActivity;
import mainFunctionality.viewsModels.TripsReservationsViewModel;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.exceptions.FailedToRateTripException;
import utn.proy2k18.vantrack.models.Rating;


public class ScoreActivity extends AppCompatActivity {

    private int tripRating;
    private int driverRating;
    private int reservationId;
    final Activity activity = this;
    private TripsReservationsViewModel model = TripsReservationsViewModel.getInstance();
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        setTitle(R.string.rate_reservation);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            reservationId = b.getInt("reservationId");
        }
        user = FirebaseAuth.getInstance().getCurrentUser();

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
                Rating score = new Rating(tripRating, driverRating,
                        additionalComment.getText().toString());
                try {
                    model.addRating(reservationId, score, user.getEmail());
                    Intent intent = new Intent(ScoreActivity.this,
                            CentralActivity.class);
                    startActivity(intent);
                    Toast.makeText(activity, R.string.reservation_rated, Toast.LENGTH_SHORT).show();
                } catch (FailedToRateTripException frte) {
                    showErrorDialog(activity, frte.getMessage());
                }
        }
    });
    }

    private void showErrorDialog(Activity activity, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setMessage(message)
                .setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position1) {
                        Intent intent = new Intent(ScoreActivity.this,
                                ReservationActivity.class);
                        intent.putExtra("reservation_id", reservationId);
                        startActivity(intent);
                    }
                })
                .create();
        alertDialog.show();
    }
}
