package mainFunctionality.driverTrips;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mainFunctionality.localization.MapsActivityDriver;
import mainFunctionality.viewsModels.TripsViewModel;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.connector.HttpConnector;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.mainFunctionality.search.TripStop;
import utn.proy2k18.vantrack.models.Notification;
import utn.proy2k18.vantrack.utils.DateTimePicker;
import utn.proy2k18.vantrack.viewModels.NotificationsViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TripFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class TripFragment extends Fragment {

    private final String AUTH_KEY_FCM = "AAAA42QlzDQ:APA91bFoW8mrwrUxePhyLhZVURCt-bV6KZrVemfuLep7-7smRPq_AiIbKwgATAj6g5yeFG9EQcT0yEuDtTOsOp3O-xdMek928a7n5F7UcOIFWGN9W8itZlwIWUjhWUmbZoxZ4x4m6_X8";
    private final String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";

    private static final String ARG_PARAM1 = "trip";

    private Trip trip;
    private TextView tripDate;
    private TextView tripTime;
    private TripsViewModel tripsModel;
    private NotificationsViewModel notificationsModel;
    private DateTimeFormatter tf = DateTimeFormat.forPattern("HH:mm");
    private OnFragmentInteractionListener mListener;
    private String newStopDesc;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mReference;

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
        tripsModel = ViewModelProviders.of(getActivity()).get(TripsViewModel.class);
        notificationsModel = ViewModelProviders.of(getActivity()).get(NotificationsViewModel.class);
        trip = getArguments().getParcelable(ARG_PARAM1);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip, container, false);

        final TextView origin = view.findViewById(R.id.trip_fragment_origin);
        final TextView destination = view.findViewById(R.id.trip_fragment_destination);
        TextView company = view.findViewById(R.id.trip_fragment_company);
        tripDate = view.findViewById(R.id.trip_fragment_date);
        tripTime = view.findViewById(R.id.trip_fragment_time);
        TextView price = view.findViewById(R.id.trip_price);
        TextView stops = view.findViewById(R.id.trip_fragment_stops);

        final LinearLayout trip_actions = view.findViewById(R.id.trip_actions);
        final LinearLayout trip_modifications = view.findViewById(R.id.trip_modifications);

        final Button btnStartTrip = view.findViewById(R.id.btn_start_trip);
        final Button btnCancelTrip = view.findViewById(R.id.btn_cancel_trip);
        final Button btnModifyTrip = view.findViewById(R.id.btn_modify_trip);
        final Button btnConfirmModification = view.findViewById(R.id.btn_modify_confirmation_trip);
        final Button btnModifOrigin = view.findViewById(R.id.btn_origin);
        final Button btnModifDest = view.findViewById(R.id.btn_destination);
        final Button btnModifDate = view.findViewById(R.id.btn_date);
        final Button btnModifTime = view.findViewById(R.id.btn_time);
        final Button btnCancelModifs = view.findViewById(R.id.btn_cancel_modification);

        final DateTimePicker dateTimePicker = new DateTimePicker(getActivity());

        trip_actions.setVisibility(View.VISIBLE);
        trip_modifications.setVisibility(View.GONE);

        origin.setText(trip.getOrigin());
        destination.setText(trip.getDestination());
        company.setText(trip.getCompanyName());
        tripDate.setText(trip.getFormattedDate());
        tripTime.setText(trip.getTime().toString(tf));
        price.setText(String.valueOf(trip.getPrice()));
        stops.setText(trip.createStrStops());

        btnStartTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Desea comenzar el Viaje?")
                        .setMessage("Los usuarios suscritos podrán ver su ubicación")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position1) {
                                Toast.makeText(getContext(),"Viaje Comenzado",
                                        Toast.LENGTH_LONG).show();
                                //TODO: Hacer que el viaje sólo tenga un botón de finalizar
                                //TODO: Hacer que deje de emitir su ubicación
                                verifyGPSIsEnabledAndGetLocation(trip);
                            }
                        })
                        .setNegativeButton("Cancelar",null);

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        btnCancelTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Desea eliminar el Viaje?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position1) {
                                createNotification(NotificationsViewModel.CANCELATION_ID);
                                tripsModel.deleteTrip(trip);
                                setFragment(new MyTripsFragment());
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
                btnModifOrigin.setVisibility(View.VISIBLE);
                btnModifDest.setVisibility(View.VISIBLE);
                btnModifDate.setVisibility(View.VISIBLE);
                btnModifTime.setVisibility(View.VISIBLE);
            }
        });

        btnModifOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseStop(origin.getText().toString());
                if (!newStopDesc.equals(origin.getText().toString())) {
                    origin.setText(newStopDesc);
                }
            }
        });

        btnModifDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimePicker.pickDate(tripDate);
            }
        });
        btnModifTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimePicker.pickTime(tripTime);
            }
        });

        btnConfirmModification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Desea modificar el Viaje?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position1) {
                                applyModification(origin.getText().toString(),
                                        destination.getText().toString());
                                setFragment(new MyTripsFragment());
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
                            public void onClick(DialogInterface dialog, int position1) {
                                tripDate.setText(trip.getFormattedDate());
                                tripTime.setText(trip.getTime().toString(tf));

                                trip_actions.setVisibility(View.VISIBLE);
                                trip_modifications.setVisibility(View.GONE);
                                btnModifDate.setVisibility(View.GONE);
                                btnModifTime.setVisibility(View.GONE);
                            }
                        })
                        .setNegativeButton("Cancelar",null);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        return view;
    }

    private void chooseStop(String currentStopDesc) {
        newStopDesc = currentStopDesc;
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.choose_stop_dialog);
        dialog.setCancelable(true);

        final Spinner spinner = dialog.findViewById(R.id.choose_stops_spinner);
        final ArrayList<String> tripStopsDesc = createStopsDescriptionArray();
        ArrayAdapter<String> stopsAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, tripStopsDesc);
        stopsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(stopsAdapter);

        final int oldStopPos = stopsAdapter.getPosition(currentStopDesc);
        spinner.setSelection(oldStopPos);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id) {
                newStopDesc = parent.getItemAtPosition(position).toString();
                dialog.dismiss();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private ArrayList<String> createStopsDescriptionArray() {
        ArrayList<String> stopsDescriptions = new ArrayList<>();
        for (TripStop tripStop: trip.getStops()) {
            stopsDescriptions.add(tripStop.getDescription());
        }
        return stopsDescriptions;
    }

    private void applyModification(String newOrigin, String newDestination) {
        // TODO: all modifications should be moved to view model
        if (!trip.getFormattedDate().equals(tripDate.getText().toString())) {
            LocalDate newDate = LocalDate.parse(tripDate.getText().toString(), trip.getDtf());
            trip.setDate(newDate);
            createNotification(NotificationsViewModel.DATE_MODIF_ID);
        }
        if (!trip.getTime().toString().equals(tripTime.getText().toString())) {
            LocalTime newTime = LocalTime.parse(tripTime.getText().toString());
            trip.setTime(newTime);
            createNotification(NotificationsViewModel.TIME_MODIF_ID);
        }
        if (!trip.getOrigin().equals(newOrigin)) {
            trip.setOrigin(newOrigin);
            createNotification(NotificationsViewModel.ORIGIN_MODIF_ID);
        }
        if (!trip.getDestination().equals(newDestination)) {
            trip.setDestination(newDestination);
            createNotification(NotificationsViewModel.DESTINATION_MODIF_ID);
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    private String getTripTopic() {
        return "trips__" + String.valueOf(trip.get_id());
    }

    private void createNotification(Integer notifMessageId) {
        // TODO: remove hardcoded username
        final String username = "luciano.lopez@gmail.com";
        Notification notification = new Notification(username, trip.get_id(), notifMessageId);
        this.notificationsModel.createNotification(notification);
        sendMessage(getTripTopic(), notifMessageId);
    }

    private void sendMessage(String topic, Integer messageId) {
        JSONObject message = new JSONObject();
        JSONObject notification = new JSONObject();

        try {
            notification.put("title", "Actualizacion de alguna de sus reservas");
            notification.put("message", notificationsModel.getMessage(messageId));
            message.put("data", notification);
            message.put("to", "/topics/" + topic);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final HttpConnector httpConnector = HttpConnector.getInstance();
        httpConnector.execute(API_URL_FCM, "POST", message.toString(), AUTH_KEY_FCM);
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void verifyGPSIsEnabledAndGetLocation(Trip trip){
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            this.showGPSDisabledAlertToUser();
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
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
                                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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
