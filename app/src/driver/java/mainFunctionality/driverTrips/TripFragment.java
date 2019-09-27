package mainFunctionality.driverTrips;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mainFunctionality.localization.MapsActivityDriver;
import mainFunctionality.viewsModels.TripsViewModel;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.exceptions.FailedToModifyTripException;
import utn.proy2k18.vantrack.exceptions.InvalidStopsException;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.mainFunctionality.search.TripStop;
import utn.proy2k18.vantrack.utils.DateTimePicker;
import utn.proy2k18.vantrack.utils.DeepCopy;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TripFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class TripFragment extends Fragment {

    private static final String ARG_PARAM1 = "originalTrip";

    private FirebaseUser user;
    private Trip originalTrip;
    private Trip modifiedTrip;
    private TextView tripDate;
    private TripsViewModel tripsModel;
    private List<Button> modificationsButtons = new ArrayList<>();
    private DateTimeFormatter tf = DateTimeFormat.forPattern("HH:mm");
    private DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy");
    private DateTimePicker dateTimePicker;
    private OnFragmentInteractionListener mListener;


    public TripFragment() {
        // Required empty public constructor
    }

    public static TripFragment newInstance(Trip trip) {
        TripFragment tripFragment = new TripFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, trip);
        tripFragment.setArguments(args);
        return tripFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tripsModel = TripsViewModel.getInstance();
        originalTrip = getArguments().getParcelable(ARG_PARAM1);
        modifiedTrip = (Trip) DeepCopy.copy(originalTrip);
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip, container, false);
        dateTimePicker = new DateTimePicker(getActivity());

        tripDate = view.findViewById(R.id.trip_fragment_date);
        final TextView company = view.findViewById(R.id.trip_fragment_company);
        final TextView price = view.findViewById(R.id.trip_price);
        final LinearLayout trip_actions = view.findViewById(R.id.trip_actions);
        final LinearLayout trip_modifications = view.findViewById(R.id.trip_modifications);
        final Button btnStartTrip = view.findViewById(R.id.btn_start_trip);
        final Button btnModifyTrip = view.findViewById(R.id.btn_modify_trip);
        final Button btnConfirmModification = view.findViewById(R.id.btn_modify_confirmation_trip);
        final Button btnModifDate = view.findViewById(R.id.btn_date);
        final Button btnCancelModifs = view.findViewById(R.id.btn_cancel_modification);
        modificationsButtons.add(btnModifDate);

        trip_actions.setVisibility(View.VISIBLE);
        trip_modifications.setVisibility(View.GONE);
        Integer minutesForTripDeparture = originalTrip.minutesForTripDeparture();
        if (minutesForTripDeparture > 10) {
            btnStartTrip.setVisibility(View.GONE);
        }
        if (minutesForTripDeparture < 60) {
            btnModifyTrip.setVisibility(View.GONE);
        }

        populateStopsLayout(inflater, container, view, originalTrip);
        company.setText(originalTrip.getCompanyName());
        price.setText(String.format(Locale.getDefault(), "$%.2f", originalTrip.getPrice()));
        tripDate.setText(originalTrip.getDate().toString(dtf));
        tripDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                modifiedTrip.setDate(dtf.parseLocalDate(s.toString()));
            }
        });

        updateModificationsButtonsVisibility(View.INVISIBLE);

        btnStartTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Desea comenzar el Viaje?")
                        .setMessage("Los usuarios suscritos podrán ver su ubicación")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {
                                verifyGPSIsEnabledAndGetLocation(originalTrip);
                            }
                        })
                        .setNegativeButton("Cancelar",null);

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        btnModifyTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trip_actions.setVisibility(View.GONE);
                trip_modifications.setVisibility(View.VISIBLE);
                updateModificationsButtonsVisibility(View.VISIBLE);
            }
        });

        btnModifDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimePicker.pickDate(tripDate);
            }
        });

        btnConfirmModification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Desea modificar el Viaje?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {
                                if (!validateQtyOfStops()) {
                                    showErrorDialog(getActivity(),
                                            getString(R.string.min_qty_stops));
                                    setFragment(TripFragment.newInstance(originalTrip));
                                } else if (originalTrip.equals(modifiedTrip)) {
                                    trip_actions.setVisibility(View.VISIBLE);
                                    trip_modifications.setVisibility(View.GONE);
                                    updateModificationsButtonsVisibility(View.INVISIBLE);
                                } else {
                                    updateTrip(dialog);
                                }
                            }
                        })
                        .setNegativeButton("Cancelar",null);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        btnCancelModifs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Desea cancelar las modificaciones realizadas?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {
                                setFragment(TripFragment.newInstance(originalTrip));
                            }
                        })
                        .setNegativeButton("Cancelar",null);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        return view;
    }

    private void populateStopsLayout(LayoutInflater inflater, ViewGroup container, View view,
                                     Trip trip) {
        LinearLayout stopsLayout = view.findViewById(R.id.stops_layout);

        for (TripStop tripStop: trip.getStops()) {
            LinearLayout stopLayout = (LinearLayout) inflater.inflate(R.layout.stop_layout,
                    container,false);

            TextView stopDesc = (TextView) stopLayout.getChildAt(1);
            stopDesc.setText(String.format("\u2022 %s", tripStop.getDescription()));
            TextView stopTime = (TextView) stopLayout.getChildAt(2);
            stopTime.setText(tripStop.getHour().toString(tf));
            stopTime.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) { }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String tripStopDesc = getStopDescFromTextView(stopDesc);
                    tripsModel.modifyTripStopTime(modifiedTrip, tripStopDesc,
                            tf.parseLocalTime(s.toString()));
                    stopsLayout.removeAllViews();
                    populateStopsLayout(inflater, container, view, modifiedTrip);
                    updateModificationsButtonsVisibility(View.VISIBLE);
                }
            });

            Button modifyStop = (Button) stopLayout.getChildAt(3);
            modifyStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dateTimePicker.pickTime(stopTime);
                }
            });

            Button deleteStop = (Button) stopLayout.getChildAt(0);
            deleteStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tripStopDesc = getStopDescFromTextView(stopDesc);
                    tripsModel.deleteStopFromTrip(modifiedTrip, tripStopDesc);
                    stopsLayout.removeView(stopLayout);
                }
            });

            modificationsButtons.add(modifyStop);
            modificationsButtons.add(deleteStop);
            stopsLayout.addView(stopLayout);
        }
    }

    private String getStopDescFromTextView(TextView tv) {
        return tv.getText().toString().replaceAll("\u2022 ", "");
    }

    private void updateModificationsButtonsVisibility(Integer visibility) {
        for (Button button: modificationsButtons) {
            button.setVisibility(visibility);
        }
    }

    private boolean validateQtyOfStops() {
        return modifiedTrip.getStops().size() >= 2;
    }

    private void updateTrip(DialogInterface dialogInterface) {
        try {
            tripsModel.modifyTrip(user.getEmail(), modifiedTrip);
            Toast.makeText(getActivity(), R.string.trip_modified, Toast.LENGTH_SHORT).show();
            setFragment(TripFragment.newInstance(modifiedTrip));
        } catch (FailedToModifyTripException | InvalidStopsException e) {
            dialogInterface.dismiss();
            showErrorDialog(getActivity(), e.getMessage());
            setFragment(TripFragment.newInstance(originalTrip));
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    public void showErrorDialog(Activity activity, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setMessage(message)
                .setNeutralButton("Aceptar",null)
                .create();
        alertDialog.show();
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

    private void verifyGPSIsEnabledAndGetLocation(Trip trip){
        final LocationManager manager = (LocationManager) getActivity().getSystemService(
                Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            this.showGPSDisabledAlertToUser();
        else {
            Intent intent = new Intent(getContext(), MapsActivityDriver.class);
            //Passing Trip id to handle locations in Firebase
            Bundle parameters = new Bundle();
            parameters.putInt("tripId", trip.get_id()); //Your id
            intent.putExtras(parameters);
            startActivity(intent);
        }
    }

    private void showGPSDisabledAlertToUser() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder
                .setTitle(R.string.location_alert_title)
                .setMessage(R.string.location_alert_content)
                .setCancelable(false)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener(){
                            public void onClick(final DialogInterface dialog, final int id) {
                                startActivity(new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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
}
