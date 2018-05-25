package utn.proy2k18.vantrack.mainFunctionality.moreOptions;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.ArrayList;

import utn.proy2k18.vantrack.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MoreOptionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MoreOptionsFragment #newInstance} factory method to
 * create an instance of this fragment.
 */

public class MoreOptionsFragment extends Fragment {

    private ListView moListView;
    private MoreOptionsAdapter moAdapter;
    private OnFragmentInteractionListener mListener;

    public MoreOptionsFragment() {
        // Required empty public constructor
    }

    public static MoreOptionsFragment newInstance() {
        return new MoreOptionsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_more_options, container, false);
        moListView = (ListView) view.findViewById(R.id.more_options_list_view);

        ArrayList<Option> MoreOptionsArray = new ArrayList<Option>();
        MoreOptionsArray.add(new Option("Notificaciones", R.drawable.ic_add_notif));
        MoreOptionsArray.add(new Option("Mi Cuenta", R.drawable.ic_add_user));
        MoreOptionsArray.add(new Option("Track My Van", R.drawable.ic_add4));
        MoreOptionsArray.add(new Option("Ayuda", R.drawable.ic_add_help));
        MoreOptionsArray.add(new Option("Cerrar sesión", R.drawable.ic_add_close_session));

        // specify an adapter (see also next example)
        moAdapter = new MoreOptionsAdapter(this.getContext(), MoreOptionsArray);
        moListView.setAdapter(moAdapter);

        // Inflate the layout for this fragment
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
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

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = null;
        if (activity != null) {
            actionBar = activity.getSupportActionBar();
        }
        if (actionBar != null) {
            actionBar.setTitle(R.string.more_options);
        }
    }
}

