package mainFunctionality.notifications;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import mainFunctionality.reservations.ReservationActivity;
import mainFunctionality.viewsModels.TripsReservationsViewModel;

import utn.proy2k18.vantrack.exceptions.BackendConnectionException;
import utn.proy2k18.vantrack.exceptions.BackendException;
import utn.proy2k18.vantrack.models.Notification;
import utn.proy2k18.vantrack.models.Reservation;
import utn.proy2k18.vantrack.viewModels.NotificationsViewModel;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.viewModels.UsersViewModel;


public class NotificationFragment extends Fragment implements NotificationAdapter.OnItemClickListener {

    private OnFragmentInteractionListener mListener;
    private NotificationsViewModel notificationsModel;
    private TripsReservationsViewModel reservationsModel;
    private String username;


    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationsModel = NotificationsViewModel.getInstance();
        reservationsModel = TripsReservationsViewModel.getInstance();
        try {
            username = UsersViewModel.getInstance().getActualUserEmail();
        } catch (BackendException be) {
            showErrorDialog(getActivity(), be.getErrorMsg());
        } catch (BackendConnectionException bce) {
            showErrorDialog(getActivity(), bce.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        final RecyclerView mRecyclerView = view.findViewById(R.id.notifications_view);
        final RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(),
                1, GridLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        List<Notification> notificationsList = new ArrayList<>();
        try {
             notificationsList = notificationsModel.getNotifications(username);
        } catch (BackendException be) {
            showErrorDialog(getActivity(), be.getErrorMsg());
        } catch (BackendConnectionException bce) {
            showErrorDialog(getActivity(), bce.getMessage());
        }
        final NotificationAdapter resAdapter = new NotificationAdapter(notificationsList);
        resAdapter.setOnItemClickListener(NotificationFragment.this);
        mRecyclerView.setAdapter(resAdapter);

        return view;
    }

    public void onItemClick(final int position) {
        Notification notification = notificationsModel.getNotificationAtPosition(username, position);
        if(!notification.isSeen()) {
            notification.setSeen(true);
            notificationsModel.readNotification(notification.getNotificationId().toString());
        }
        Reservation reservation = reservationsModel.getReservationByTripId(notification.getTripId(),
                username);
        if (reservation != null) {
            Intent intent = new Intent(getActivity(), ReservationActivity.class);
            intent.putExtra("reservation_id", reservation.get_id());
            startActivity(intent);
        } else {
            showErrorDialog(getActivity(), "La reserva ha caducado.");
        }
    }

    public void showErrorDialog(Activity activity, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setMessage(message)
                .setNeutralButton("Aceptar",null)
                .create();
        alertDialog.show();
    }

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
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = null;
        if (activity != null) {
            actionBar = activity.getSupportActionBar();
        }
        if (actionBar != null) {
            actionBar.setTitle(R.string.notificacions);
        }

    }
}
