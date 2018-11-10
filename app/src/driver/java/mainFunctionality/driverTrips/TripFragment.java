package mainFunctionality.driverTrips;

import android.app.Activity;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mainFunctionality.localization.MapsActivityDriver;
import mainFunctionality.viewsModels.TripsViewModel;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.exceptions.BackendConnectionException;
import utn.proy2k18.vantrack.exceptions.BackendException;
import utn.proy2k18.vantrack.exceptions.InvalidStopException;
import utn.proy2k18.vantrack.exceptions.InvalidStopsException;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.mainFunctionality.search.TripStop;
import utn.proy2k18.vantrack.utils.DateTimePicker;
import utn.proy2k18.vantrack.viewModels.NotificationsViewModel;
import utn.proy2k18.vantrack.viewModels.UsersViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TripFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class TripFragment extends Fragment {

    private static final String ARG_PARAM1 = "trip";

    private String username;
    private Trip trip;
    private TextView tripDate;
    private TextView tripTime;
    private TripsViewModel tripsModel;
    private NotificationsViewModel notificationsModel;
    private DateTimeFormatter tf = DateTimeFormat.forPattern("HH:mm");
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
        notificationsModel = ViewModelProviders.of(getActivity()).get(NotificationsViewModel.class);
        trip = getArguments().getParcelable(ARG_PARAM1);
        try {
            username = UsersViewModel.getInstance().getActualUserEmail();
        } catch (BackendException be) {
            showErrorDialog(getActivity(), be.getErrorMsg());
        } catch (BackendConnectionException bce) {
            showErrorDialog(getActivity(), bce.getMessage());
        }
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
        final EditText stops = view.findViewById(R.id.trip_fragment_stops);

        final LinearLayout trip_actions = view.findViewById(R.id.trip_actions);
        final LinearLayout trip_modifications = view.findViewById(R.id.trip_modifications);

        final Button btnStartTrip = view.findViewById(R.id.btn_start_trip);
        final Button btnModifyTrip = view.findViewById(R.id.btn_modify_trip);
        final Button btnConfirmModification = view.findViewById(R.id.btn_modify_confirmation_trip);
        final Button btnModifOrigin = view.findViewById(R.id.btn_origin);
        final Button btnModifDest = view.findViewById(R.id.btn_destination);
        final Button btnModifStops = view.findViewById(R.id.btn_stops);
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
                            public void onClick(DialogInterface dialog, int position) {
                                Toast.makeText(getContext(),"Viaje Comenzado",
                                        Toast.LENGTH_LONG).show();
                                verifyGPSIsEnabledAndGetLocation(trip);
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
                btnModifStops.setVisibility(View.VISIBLE);
                btnModifDate.setVisibility(View.VISIBLE);
                btnModifTime.setVisibility(View.VISIBLE);
            }
        });

        btnModifOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseStop(origin);
            }
        });

        btnModifDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseStop(destination);
            }
        });

        btnModifStops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stops.setEnabled(true);
                stops.setBackgroundResource(android.R.drawable.edit_text);
                stops.setTextColor(Color.GRAY);
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
                            public void onClick(DialogInterface dialog, int position) {
                                String newOrigin = origin.getText().toString();
                                String newDest = destination.getText().toString();
                                String newStops = stops.getText().toString();

                                try {
                                    validateStops(newOrigin, newDest, newStops);
                                    Trip newTrip = applyModification(newDest, newOrigin, newStops);
                                    setFragment(TripFragment.newInstance(newTrip));
                                } catch (BackendException be) {
                                    showErrorDialog(getActivity(), be.getErrorMsg());
                                    setFragment(TripFragment.newInstance(trip));
                                } catch (BackendConnectionException | InvalidStopsException e) {
                                    showErrorDialog(getActivity(), e.getMessage());
                                    setFragment(TripFragment.newInstance(trip));
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
                                setFragment(TripFragment.newInstance(trip));
                            }
                        })
                        .setNegativeButton("Cancelar",null);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        return view;
    }

    private void chooseStop(final TextView stopTextView) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.choose_stop_dialog);
        dialog.setCancelable(true);

        final Spinner spinner = dialog.findViewById(R.id.choose_stops_spinner);
        final ArrayList<String> tripStopsDesc = createStopsDescriptionArray();
        ArrayAdapter<String> stopsAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, tripStopsDesc);
        stopsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(stopsAdapter);

        final int oldStopPos = stopsAdapter.getPosition(stopTextView.getText().toString());
        spinner.setSelection(oldStopPos);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id) {
                String newStopDesc = parent.getItemAtPosition(position).toString();
                if (!newStopDesc.equals(stopTextView.getText().toString())) {
                    stopTextView.setText(newStopDesc);
                    dialog.dismiss();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        dialog.show();
    }

    private ArrayList<String> createStopsDescriptionArray() {
        ArrayList<String> stopsDescriptions = new ArrayList<>();
        for (TripStop tripStop: trip.getStops()) {
            stopsDescriptions.add(tripStop.getDescription());
        }
        return stopsDescriptions;
    }

    private List<TripStop> getStopsFromStringDescription(String stopsDesc) {
        ArrayList<TripStop> tripStops = new ArrayList<>();
        List<String> stopsList = removeWhiteSpacesFromList(Arrays.asList(stopsDesc.split(";")));
        for (String stopDesc: stopsList) {
            TripStop ts = trip.getTripStopByDescription(stopDesc);
            if (ts != null) {
                tripStops.add(ts);
            } else {
                throw new InvalidStopException(stopDesc);
            }
        }
        return tripStops;
    }

    private Trip applyModification(String newDestination, String newOrigin, String newStops) {
        Trip newTrip = new Trip(trip);

        String tripStrStops = removeWhiteSpaces(trip.createStrStops());
        if (!tripStrStops.equalsIgnoreCase(removeWhiteSpaces(newStops))) {
            List<TripStop> newTripStops = getStopsFromStringDescription(newStops);
            newTrip.setStops(newTripStops);
        }
        if (!trip.getFormattedDate().equals(tripDate.getText().toString())) {
            LocalDate newDate = LocalDate.parse(tripDate.getText().toString(), trip.getDtf());
            newTrip.setDate(newDate);
        }
        if (!trip.getTime().toString(tf).equals(tripTime.getText().toString())) {
            LocalTime newTime = LocalTime.parse(tripTime.getText().toString());
            newTrip.setTime(newTime);
        }
        if (!trip.getOrigin().equals(newOrigin)) {
            newTrip.setOrigin(newOrigin);
        }
        if (!trip.getDestination().equals(newDestination)) {
            newTrip.setDestination(newDestination);
        }
        if (!newTrip.equals(trip)) {
            try {
                tripsModel.modifyTrip(username, newTrip);
            } catch (JsonProcessingException jpe) {
                showErrorDialog(getActivity(), "Error al modificar el viaje. " +
                        "Inténtelo nuevamente más tarde.");
                setFragment(TripFragment.newInstance(trip));
            }
        }
        return newTrip;
    }

    private void validateStops(String newOrigin, String newDestination, String newStops) {
        List<String> stopsList = removeWhiteSpacesFromList(Arrays.asList(newStops.split(";")));
        if (newOrigin.equalsIgnoreCase(newDestination) ||
                newOrigin.equalsIgnoreCase(trip.getDestination()) ||
                newDestination.equalsIgnoreCase(trip.getOrigin()) || stopsList.size() == 0 ||
                !(stopsList.get(0).equalsIgnoreCase(removeWhiteSpaces(newOrigin)) &&
                        stopsList.get(stopsList.size()-1).equalsIgnoreCase(removeWhiteSpaces(newDestination)))) {
            throw new InvalidStopsException();
        }
    }

    private List<String> removeWhiteSpacesFromList(List<String> stringList) {
        List<String> newStringList = new ArrayList<>();
        for(int i = 0; i < stringList.size(); i++) {
            String string = removeWhiteSpaces(stringList.get(i));
            newStringList.add(string);
        }

        return newStringList;
    }

    private String removeWhiteSpaces(String string) {
        return string.replaceAll("\\s+","");
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack(null);
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
