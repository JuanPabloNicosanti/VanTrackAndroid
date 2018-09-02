package mainFunctionality.driverTrips;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import mainFunctionality.localization.MapsActivityDriver;
import mainFunctionality.viewsModels.TripsViewModel;
import utn.proy2k18.vantrack.R;
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

    private static final String ARG_PARAM1 = "tripPosition";
    private static final String ARG_PARAM2 = "needsConfirmation";

    private int position;
    private boolean needsConfirmation;
    private Trip trip;
    private TextView tripDate;
    private TextView tripTime;
    private TripsViewModel tripsModel;
    private OnFragmentInteractionListener mListener;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mReference;

    public TripFragment() {
        // Required empty public constructor
    }

    public static TripFragment newInstance(int tripPosition, boolean needsConfirmation) {
        TripFragment tripFragment = new TripFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, tripPosition);
        args.putBoolean(ARG_PARAM2, needsConfirmation);
        tripFragment.setArguments(args);
        return tripFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tripsModel = ViewModelProviders.of(getActivity()).get(TripsViewModel.class);
        tripsModel.init();
        position = getArguments().getInt(ARG_PARAM1);
        needsConfirmation = getArguments().getBoolean(ARG_PARAM2, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip, container, false);

        TextView origin = view.findViewById(R.id.trip_fragment_origin);
        TextView destination = view.findViewById(R.id.trip_fragment_destination);
        TextView company = view.findViewById(R.id.trip_fragment_company);
        TextView price = view.findViewById(R.id.trip_price);
        tripDate = view.findViewById(R.id.trip_fragment_date);
        tripTime = view.findViewById(R.id.trip_fragment_time);

        final LinearLayout trip_actions = view.findViewById(R.id.trip_actions);
        final LinearLayout trip_modifications = view.findViewById(R.id.trip_modifications);

        final Button btnConfirmTrip = view.findViewById(R.id.btn_confirm_trip);
        final Button btnStartTrip = view.findViewById(R.id.btn_start_trip);
        final Button btnCancelTrip = view.findViewById(R.id.btn_cancel_trip);
        final Button btnModifyTrip = view.findViewById(R.id.btn_modify_trip);
        final Button btnConfirmModification = view.findViewById(R.id.btn_modify_confirmation_trip);
        final Button btnModifDate = view.findViewById(R.id.btn_date);
        final Button btnModifTime = view.findViewById(R.id.btn_time);
        final Button btnCancelModifs = view.findViewById(R.id.btn_cancel_modification);

        final DateTimePicker dateTimePicker = new DateTimePicker(getActivity());

        if (needsConfirmation) {
            btnConfirmTrip.setVisibility(View.VISIBLE);
            trip = tripsModel.getTripToConfirmAtPosition(position);
        } else {
            trip_actions.setVisibility(View.VISIBLE);
            trip_modifications.setVisibility(View.GONE);
            trip = tripsModel.getDriverTripAtPosition(position);
        }

        origin.setText(trip.getOrigin());
        destination.setText(trip.getDestination());
        company.setText(trip.getCompanyName());
        price.setText(String.valueOf(trip.getPrice()));
        tripDate.setText(trip.getCalendarDate());
        tripTime.setText(trip.getStrTime());

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

        btnConfirmTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Desea confirmar el Viaje?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position1) {
                                sendMessage("confirmado", getTripTopic());
                                tripsModel.addTripToDriverTrips(trip);
                                setFragment(new MyTripsFragment());
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
                                tripsModel.deleteTripAtPosition(position);
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
                                trip.setDateTime(tripDate.getText().toString() +
                                        tripTime.getText().toString());
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
                                tripDate.setText(trip.getCalendarDate());
                                tripTime.setText(trip.getStrTime());

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
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        HttpsURLConnection connection = null;
        try {
            URL url = new URL(API_URL_FCM);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "key=" + AUTH_KEY_FCM);

            JSONObject message = new JSONObject();
            JSONObject notification = new JSONObject();
            notification.put("title", String.format("Viaje %s!", status));
            notification.put("message", String.format("Su viaje de %s a %s ha sido %s.",
                    trip.getOrigin(), trip.getDestination(), status));
            message.put("data", notification);
            message.put("to", "/topics/" + topic);

            byte[] outputBytes = message.toString().getBytes("UTF-8");
            OutputStream os = connection.getOutputStream();
            os.write(outputBytes);
            os.flush();
            os.close();
            connection.getInputStream(); //do not remove this line. request will not work without it

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) connection.disconnect();
        }
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
            postStartedTripInfo(trip);
            Intent intent = new Intent(getContext(),MapsActivityDriver.class);
            //Passing Trip id to handle locations in Firebase
            Bundle parameters = new Bundle();
            parameters.putString("tripId", trip.get_id()); //Your id
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

    private void postStartedTripInfo(Trip trip){
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        trip.setDriverId(mCurrentUser.getUid());
    }
}
