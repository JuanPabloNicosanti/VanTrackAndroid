package mainFunctionality;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.exceptions.FailedToGetUserException;
import utn.proy2k18.vantrack.initAndAccManagement.UpdatePasswordFragment;
import utn.proy2k18.vantrack.models.User;
import utn.proy2k18.vantrack.viewModels.UsersViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {


    private OnFragmentInteractionListener mListener;

    public AccountFragment() {
        // Required empty public constructor
    }

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        AutoCompleteTextView name = view.findViewById(R.id.userFirstNameMyAccount);
        AutoCompleteTextView surname = view.findViewById(R.id.userLastNameMyAccount);
        TextView email = view.findViewById(R.id.userEmailMyAccount);
        Button modifyPassword = view.findViewById(R.id.btn_modify_password);
        Button removeAccount = view.findViewById(R.id.btn_remove_account);
        Button confirmModifs = view.findViewById(R.id.btn_confirm_user_modification);

        UsersViewModel usersModel = UsersViewModel.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        try {
            User dbUser = usersModel.getUser(user.getEmail());
            name.append(dbUser.getName());
            surname.append(dbUser.getSurname());
            email.append(user.getEmail());
        } catch (FailedToGetUserException fgue) {
            showErrorDialog(getActivity(), fgue.getMessage());
        }

        modifyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new UpdatePasswordFragment());
            }
        });

        confirmModifs.setVisibility(View.GONE);
        removeAccount.setVisibility(View.GONE);

        return view;
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
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
            actionBar.setTitle(R.string.my_account);
        }

    }
}
