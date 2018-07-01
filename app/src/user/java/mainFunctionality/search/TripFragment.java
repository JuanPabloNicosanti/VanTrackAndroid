package mainFunctionality.search;

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
import android.widget.TextView;

import mainFunctionality.reservations.MyReservationsFragment;
import mainFunctionality.viewsModels.TripsReservationsViewModel;
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
    private static final String ARG_PARAM2 = "returnDate";

    private int position;
    private String returnDate;

    private TripsViewModel tripsModel;
    private TripsReservationsViewModel reservationsModel;
    private Trip trip;

    private OnFragmentInteractionListener mListener;


    public TripFragment() {
        // Required empty public constructor
    }

    public static TripFragment newInstance(int tripPosition, String returnDate) {
        TripFragment tripFragment = new TripFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, tripPosition);
        args.putString(ARG_PARAM2, returnDate);
        tripFragment.setArguments(args);

        return tripFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tripsModel = ViewModelProviders.of(getActivity()).get(TripsViewModel.class);
        reservationsModel = ViewModelProviders.of(getActivity()).get(TripsReservationsViewModel.class);
        position = getArguments().getInt(ARG_PARAM1);
        returnDate = getArguments().getString(ARG_PARAM2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip, container, false);

        TextView origin = view.findViewById(R.id.trip_fragment_origin);
        TextView destination = view.findViewById(R.id.trip_fragment_destination);
        TextView company = view.findViewById(R.id.trip_fragment_company);
        TextView date = view.findViewById(R.id.trip_fragment_date);
        Button btnBookTrip = view.findViewById(R.id.btn_book_trip);
        Button btnBookTripSearchReturn = view.findViewById(R.id.btn_book_trip_search_return);

        if (returnDate.equals(getResources().getString(R.string.no_return_date))) {
            btnBookTrip.setVisibility(View.VISIBLE);
            btnBookTripSearchReturn.setVisibility(View.INVISIBLE);
        } else {
            btnBookTrip.setVisibility(View.INVISIBLE);
            btnBookTripSearchReturn.setVisibility(View.VISIBLE);
        }

        trip = tripsModel.getFilteredTripAtPosition(position);

        origin.setText(trip.getOrigin());
        destination.setText(trip.getDestination());
        company.setText(trip.getCompanyName());
        date.setText(trip.getFormattedDate());

        btnBookTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Desea reservar el Viaje?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position1) {

                                reservationsModel.addReservationForTrip(trip);

                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.replace(R.id.fragment_container, new MyReservationsFragment());
                                ft.commit();
                            }


                        })
                        .setNegativeButton("Cancelar",null);

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        btnBookTripSearchReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Desea reservar el Viaje?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position1) {

                                reservationsModel.addReservationForTrip(trip);

                                SearchResultsFragment searchResultsFragment = SearchResultsFragment.newInstance(
                                        trip.getDestination(), trip.getOrigin(), returnDate,
                                        getResources().getString(R.string.no_return_date));

                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.replace(R.id.fragment_container, searchResultsFragment);
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
}
