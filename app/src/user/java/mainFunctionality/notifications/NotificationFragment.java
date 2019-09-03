package mainFunctionality.notifications;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

    private static final String ARG_PARAM1 = "forceFetching";
    private final static NotificationAdapter notificationAdapter = new NotificationAdapter();

    private OnFragmentInteractionListener mListener;
    private NotificationsViewModel notificationsModel;
    private TripsReservationsViewModel reservationsModel;
    private String username;
    private Boolean forceFetching;


    public NotificationFragment() {
        // Required empty public constructor
    }

    public static NotificationFragment newInstance(Boolean forceFetching) {
        NotificationFragment notificationFragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, forceFetching);
        notificationFragment.setArguments(args);
        return notificationFragment;
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
        forceFetching = getArguments().getBoolean(ARG_PARAM1, false);
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
             notificationsList = notificationsModel.getNotifications(username, forceFetching);
        } catch (BackendException be) {
            showErrorDialog(getActivity(), be.getErrorMsg());
        } catch (BackendConnectionException bce) {
            showErrorDialog(getActivity(), bce.getMessage());
        }
        notificationAdapter.setList(notificationsList);
        notificationAdapter.setOnItemClickListener(NotificationFragment.this);
        mRecyclerView.setAdapter(notificationAdapter);

        return view;
    }

    public void onItemClick(final int position) {
        Notification notification = notificationsModel.getNotificationAtPosition(username, position);
        if(!notification.getSeen()) {
            notificationsModel.readNotification(notification);
        }
        Reservation reservation = reservationsModel.getReservationByTripId(notification.getTripId(),
                username);
        if (reservation != null) {
            Intent intent = new Intent(getActivity(), ReservationActivity.class);
            intent.putExtra("reservation_id", reservation.get_id());
            startActivity(intent);
        } else {
            showErrorDialog(getActivity(), "La reserva ha caducado.");
            notificationsModel.readNotification(notification);
            notificationAdapter.notifyDataSetChanged();
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
        notificationAdapter.notifyDataSetChanged();
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
