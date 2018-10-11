package mainFunctionality.driverTrips;

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
import android.widget.Button;
import android.widget.LinearLayout;
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

import mainFunctionality.localization.MapsActivityDriver;
import mainFunctionality.viewsModels.TripsViewModel;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.connector.HttpConnector;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.utils.DateTimePicker;

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


    private int position;
    private Trip trip;
    private TextView tripDate;
    private TextView tripTime;
    private TripsViewModel tripsModel;
    private DateTimeFormatter tf = DateTimeFormat.forPattern("HH:mm");
    private OnFragmentInteractionListener mListener;

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
        trip = getArguments().getParcelable(ARG_PARAM1);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip, container, false);

        TextView origin = view.findViewById(R.id.trip_fragment_origin);
        TextView destination = view.findViewById(R.id.trip_fragment_destination);
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
        final Button btnModifDate = view.findViewById(R.id.btn_date);
        final Button btnModifTime = view.findViewById(R.id.btn_time);
        final Button btnCancelModifs = view.findViewById(R.id.btn_cancel_modification);

        final DateTimePicker dateTimePicker = new DateTimePicker(getActivity());

        trip_actions.setVisibility(View.VISIBLE);
        trip_modifications.setVisibility(View.GONE);

        origin.setText(trip.getOrigin());
        destination.setText(trip.getDestination());
        company.setText(trip.getCompanyName());
        tripDate.setText(trip.getDate().toString());
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
                                Toast.makeText(getContext(),"Viaje Comenzado", Toast.LENGTH_LONG).show();
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
                                sendMessage("cancelado", getTripTopic());
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
                btnModifDate.setVisibility(View.VISIBLE);
                btnModifTime.setVisibility(View.VISIBLE);
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
                                LocalDate newDate = LocalDate.parse(tripDate.getText().toString());
                                LocalTime newTime = LocalTime.parse(tripTime.getText().toString());
                                trip.setDate(newDate);
                                trip.setTime(newTime);
                                sendMessage("modificado", getTripTopic());
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
                                tripDate.setText(trip.getDate().toString());
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

    private void setFragment(Fragment fragment) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    private String getTripTopic() {
        return "trips__" + trip.get_id();
    }

    private void sendMessage(String status, String topic) {
        JSONObject message = new JSONObject();
        JSONObject notification = new JSONObject();

        try {
            notification.put("title", String.format("Viaje %s!", status));
            notification.put("message", String.format("Su viaje de %s a %s ha sido %s.",
                    trip.getOrigin(), trip.getDestination(), status));
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
