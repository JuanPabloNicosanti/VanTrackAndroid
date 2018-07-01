package mainFunctionality.notifications;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mainFunctionality.viewsModels.NotificationsViewModel;
import utn.proy2k18.vantrack.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotificationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends Fragment implements NotificationAdapter.OnItemClickListener {

    private static final String ARG_PARAM1 = "newNotificationTitle";
    private static final String ARG_PARAM2 = "newNotificationMessage";

    private OnFragmentInteractionListener mListener;
    private NotificationsViewModel notificationsViewModel;


    public NotificationFragment() {
        // Required empty public constructor
    }

    public static NotificationFragment newInstance(String newNotifTitle, String newNotifMessage) {
        NotificationFragment fragment = new NotificationFragment();

        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, newNotifTitle);
        args.putString(ARG_PARAM2, newNotifMessage);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationsViewModel = ViewModelProviders.of(getActivity()).get(NotificationsViewModel.class);

        final String newNotifTitle = getArguments().getString(ARG_PARAM1);
        final String newNotifMessage = getArguments().getString(ARG_PARAM2);

        if (!newNotifTitle.equals("NO_NOTIFICATION")) {
            Notification newNotification = new Notification(newNotifTitle, newNotifMessage);
            notificationsViewModel.addNotification(newNotification);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        final RecyclerView mRecyclerView = view.findViewById(R.id.notifications_view);

        final RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(),
                1, GridLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        List<Notification> notifications_list = notificationsViewModel.getNotifications();

        final NotificationAdapter resAdapter = new NotificationAdapter(notifications_list);
        resAdapter.setOnItemClickListener(NotificationFragment.this);
        mRecyclerView.setAdapter(resAdapter);

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


    public void onItemClick(final int position) {

        Toast.makeText(getContext(),"NOTIF", Toast.LENGTH_LONG).show();
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
