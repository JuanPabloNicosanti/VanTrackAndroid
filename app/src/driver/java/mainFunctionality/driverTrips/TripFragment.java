package mainFunctionality.driverTrips;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.Calendar;
import java.util.Locale;

import utn.proy2k18.vantrack.R;
import mainFunctionality.viewsModels.TripsViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TripFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class TripFragment extends Fragment {

    private static final String ARG_PARAM1 = "tripPosition";

    private int position;
    private TripsViewModel tripsModel;
    private OnFragmentInteractionListener mListener;


    public TripFragment() {
        // Required empty public constructor
    }

    public static TripFragment newInstance(int tripPosition) {
        TripFragment tripFragment = new TripFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, tripPosition);
        tripFragment.setArguments(args);
        return tripFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tripsModel = ViewModelProviders.of(getActivity()).get(TripsViewModel.class);
        position = getArguments().getInt(ARG_PARAM1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservations_driver, container, false);

        TextView origin = view.findViewById(R.id.trip_fragment_origin);
        TextView destination = view.findViewById(R.id.trip_fragment_destination);
        TextView company = view.findViewById(R.id.trip_fragment_company);
        final Button btn_cancel_trip = view.findViewById(R.id.btn_cancel_trip);
        final Button btn_modify_trip = view.findViewById(R.id.btn_modify_trip);
        final Button btn_modify_confirmation = view.findViewById(R.id.btn_modify_confirmation_trip);
        final Button btn_date = view.findViewById(R.id.btn_date);

        final Trip trip = tripsModel.getTripAtPosition(position);

        origin.setText(trip.getOrigin());
        destination.setText(trip.getDestination());
        company.setText(trip.getCompanyName());
        btn_date.setText(trip.getFormattedDate());

        btn_cancel_trip.setOnClickListener(new View.OnClickListener() {
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

        btn_modify_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_modify_confirmation.setVisibility(View.VISIBLE);
                btn_cancel_trip.setVisibility(View.GONE);
                btn_modify_trip.setVisibility(View.GONE);
                btn_date.setEnabled(true);
            }
        });

        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDatePicker(btn_date);

            }
        });

        btn_modify_confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Desea modificar el Viaje?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position1) {
                                tripsModel.getTripAtPosition(position).setDate(btn_date.getText().toString());

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

    public void goToDatePicker(final Button button){
        int day, month,year;
        final Calendar c = Calendar.getInstance();
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int yearSelected, int monthOfYearSelected, int dayOfMonthSelected) {
                button.setText(String.format(Locale.ENGLISH,"%02d/%02d/%02d",
                        dayOfMonthSelected, monthOfYearSelected + 1, yearSelected));
            }
        }, day, month, year);
        datePickerDialog.updateDate(year, month, day);
        datePickerDialog.show();
    }

}
