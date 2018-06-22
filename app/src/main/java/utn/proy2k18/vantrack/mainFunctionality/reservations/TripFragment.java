package utn.proy2k18.vantrack.mainFunctionality.reservations;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;


import org.w3c.dom.Text;

import utn.proy2k18.vantrack.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TripFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class TripFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "tripOrigin";
    private static final String ARG_PARAM2 = "tripDestination";
    private static final String ARG_PARAM3 = "tripCompany";
    private static final String ARG_PARAM4 = "tripDate";
    private static final String ARG_PARAM5 = "positionTrip";
    private int position;



    public static TripFragment newInstance() { return new TripFragment(); }

    private OnFragmentInteractionListener mListener;

    public TripFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String argTripOrigin = getArguments().getString(ARG_PARAM1);
        String argTripDestination = getArguments().getString(ARG_PARAM2);
        String argTripCompany = getArguments().getString(ARG_PARAM3);
        String argTripDate = getArguments().getString(ARG_PARAM4);
        position = getArguments().getInt(ARG_PARAM5);


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trip, container, false);


        TextView origin = view.findViewById(R.id.origin);
        TextView destination = view.findViewById(R.id.destination);
        TextView company = view.findViewById(R.id.company);
        TextView date = view.findViewById(R.id.date);
        Button btn_cancel_trip = view.findViewById(R.id.btn_cancel_trip);

        origin.setText(argTripOrigin);
        destination.setText(argTripDestination);
        company.setText(argTripCompany);
        date.setText(argTripDate);


        btn_cancel_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Desea eliminar el Viaje?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position1) {

                                MyTripsFragment tripFragment = new MyTripsFragment();
                                Bundle args = new Bundle();
                                args.putInt("positionMyTrip",position);
                                tripFragment.setArguments(args);
                                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_container,tripFragment);
                                fragmentTransaction.commit();

                            }


                        })
                        .setNegativeButton("Cancelar",null);

                AlertDialog alert = builder.create();
                alert.show();

            }
        });





        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
}
