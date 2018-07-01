package mainFunctionality.driverTrips;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Locale;
import mainFunctionality.localization.MapsActivityDriver;
import mainFunctionality.viewsModels.TripsViewModel;
import utn.proy2k18.vantrack.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TripFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class TripFragment extends Fragment {

    private static final String ARG_PARAM1 = "tripPosition";
    private static final String ARG_PARAM2 = "needsConfirmation";

    private int position;
    private boolean needsConfirmation;
    private Trip trip;
    private TextView tripDate;
    private TripsViewModel tripsModel;
    private OnFragmentInteractionListener mListener;


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
        position = getArguments().getInt(ARG_PARAM1);
        needsConfirmation = getArguments().getBoolean(ARG_PARAM2, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservations_driver, container, false);

        TextView origin = view.findViewById(R.id.trip_fragment_origin);
        TextView destination = view.findViewById(R.id.trip_fragment_destination);
        TextView company = view.findViewById(R.id.trip_fragment_company);
        tripDate = view.findViewById(R.id.trip_fragment_date);
        final Button btnConfirmTrip = view.findViewById(R.id.btn_confirm_trip);
        final Button btnStartTrip = view.findViewById(R.id.btn_start_trip);
        final Button btnCancelTrip = view.findViewById(R.id.btn_cancel_trip);
        final Button btnModifyTrip = view.findViewById(R.id.btn_modify_trip);
        final Button btnConfirmModification = view.findViewById(R.id.btn_modify_confirmation_trip);
        final Button btnModifDate = view.findViewById(R.id.btn_date);
        final Button btnCancelModifs = view.findViewById(R.id.btn_cancel_modification);

        if (needsConfirmation) {
            btnStartTrip.setVisibility(View.INVISIBLE);
            btnCancelTrip.setVisibility(View.INVISIBLE);
            btnModifyTrip.setVisibility(View.INVISIBLE);
            btnConfirmTrip.setVisibility(View.VISIBLE);
            trip = tripsModel.getTripToConfirmAtPosition(position);
        } else {
            btnStartTrip.setVisibility(View.VISIBLE);
            btnCancelTrip.setVisibility(View.VISIBLE);
            btnModifyTrip.setVisibility(View.VISIBLE);
            btnConfirmTrip.setVisibility(View.INVISIBLE);
            trip = tripsModel.getDriverTripAtPosition(position);
        }

        origin.setText(trip.getOrigin());
        destination.setText(trip.getDestination());
        company.setText(trip.getCompanyName());
        tripDate.setText(trip.getFormattedDate());

        btnStartTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Desea comenzar el Viaje?")
                        .setMessage("Los usuarios suscritos podrán ver su ubicación")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position1) {
                                Toast.makeText(getContext(),"VIAJE_COMENZADO", Toast.LENGTH_LONG).show();
                                //TODO: Hacer que empiece a emitir su ubicacion
                                //TODO: Hacer que el viaje sólo tenga un botón de finalizar
                                //TODO: Hacer que deje de emitir su ubicación
                                verifyGPSIsEnabledAndGetLocation();
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
                                tripsModel.addTripToDriverTrips(trip);

                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.replace(R.id.fragment_container, new MyTripsFragment());
                                ft.commit();
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
                                tripsModel.deleteTripAtPosition(position);

                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.replace(R.id.fragment_container, new MyTripsFragment());
                                ft.commit();
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
                btnConfirmModification.setVisibility(View.VISIBLE);
                btnStartTrip.setVisibility(View.GONE);
                btnCancelTrip.setVisibility(View.GONE);
                btnModifyTrip.setVisibility(View.GONE);
                btnModifDate.setVisibility(View.VISIBLE);
                btnCancelModifs.setVisibility(View.VISIBLE);
            }
        });

        btnModifDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDatePicker();
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
                                tripsModel.getDriverTripAtPosition(position).setDate(tripDate.getText().toString());

                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.replace(R.id.fragment_container, new MyTripsFragment());
                                ft.commit();
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

                                btnModifDate.setVisibility(View.INVISIBLE);
                                btnCancelModifs.setVisibility(View.INVISIBLE);
                                btnStartTrip.setVisibility(View.VISIBLE);
                                btnCancelTrip.setVisibility(View.VISIBLE);
                                btnConfirmModification.setVisibility(View.INVISIBLE);
                                btnModifyTrip.setVisibility(View.VISIBLE);
                            }
                        })
                        .setNegativeButton("Cancelar",null);

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        return view;
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

    public void goToDatePicker(){
        int day, month,year;
        final Calendar c = Calendar.getInstance();
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int yearSelected, int monthOfYearSelected, int dayOfMonthSelected) {
                tripDate.setText(String.format(Locale.ENGLISH,"%02d/%02d/%02d",
                        dayOfMonthSelected, monthOfYearSelected + 1, yearSelected));
            }
        }, day, month, year);
        datePickerDialog.updateDate(year, month, day);
        datePickerDialog.show();
    }

    private void verifyGPSIsEnabledAndGetLocation(){
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            this.showGPSDisabledAlertToUser();
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            startActivity(new Intent(getContext(), MapsActivityDriver.class));
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
