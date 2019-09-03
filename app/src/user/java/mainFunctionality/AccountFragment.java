package mainFunctionality;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.VanTrackApplication;
import utn.proy2k18.vantrack.exceptions.BackendConnectionException;
import utn.proy2k18.vantrack.exceptions.BackendException;
import utn.proy2k18.vantrack.exceptions.FailedToDeleteUserException;
import utn.proy2k18.vantrack.exceptions.FailedToModifyUserException;
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
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private UsersViewModel usersModel = UsersViewModel.getInstance();

    public AccountFragment() {
        // Required empty public constructor
    }

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        AutoCompleteTextView name = view.findViewById(R.id.userFirstNameMyAccount);
        AutoCompleteTextView surname = view.findViewById(R.id.userLastNameMyAccount);
        TextView email = view.findViewById(R.id.userEmailMyAccount);
        Button modifyAccount = view.findViewById(R.id.btn_modify_account);
        Button removeAccount = view.findViewById(R.id.btn_remove_account);
        Button confirmModifs = view.findViewById(R.id.btn_confirm_user_modification);
        Button cancelModifs = view.findViewById(R.id.btn_cancel_user_modification);
        LinearLayout accountActions = view.findViewById(R.id.account_actions);
        LinearLayout accountModifs = view.findViewById(R.id.account_modifications);

        try {
            User user = usersModel.getUser();
            name.append(user.getName());
            surname.append(user.getSurname());
            email.append(user.getEmail().toLowerCase());
        } catch (BackendException | BackendConnectionException be) {
            showErrorDialog(getActivity(), be.getMessage());
        }

        modifyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountActions.setVisibility(View.GONE);
                accountModifs.setVisibility(View.VISIBLE);
                name.setEnabled(true);
                surname.setEnabled(true);
            }
        });

        confirmModifs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Desea modificar el usuario?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position1) {
                                try {
                                    usersModel.modifyUser(name.getText().toString(),
                                            surname.getText().toString());
                                } catch (BackendException | BackendConnectionException |
                                        FailedToModifyUserException e) {
                                    dialog.dismiss();
                                    showErrorDialog(getActivity(), e.getMessage());
                                }

                                setFragment(new AccountFragment());
                            }
                        })
                        .setNegativeButton("Cancelar",null);

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        cancelModifs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new AccountFragment());
            }
        });

        removeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteUserAlert();
            }
        });

        return view;
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    private void showDeleteUserAlert() {
        final AlertDialog.Builder alertDialogBuilder = getAlertDialogBuilder(
                R.string.delete_account_alert_msg);
        alertDialogBuilder
                .setMessage(R.string.delete_account_warning)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        try {
                            deleteUser();
                        } catch (BackendException | FailedToDeleteUserException | BackendConnectionException e) {
                            dialog.dismiss();
                            showErrorDialog(getActivity(), e.getMessage());
                        }
                    }
                }).show();
    }

    private AlertDialog.Builder getAlertDialogBuilder(Integer titleId) {
        return new AlertDialog.Builder(this.getContext())
                .setTitle(titleId)
                .setCancelable(false)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
    }

    private void deleteUser() {
        //Getting the user instance.
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            //You need to get here the token you saved at logging-in time.
            String token = VanTrackApplication.getGoogleToken();
            //You need to get here the password you saved at logging-in time.
            String password = "userSavedPassword";

            AuthCredential credential;

            //This means you didn't have the token because user used like Facebook Sign-in method.
            if (VanTrackApplication.getGoogleToken() == null) {
                credential = EmailAuthProvider.getCredential(user.getEmail(), password);
            } else {
                // Doesn't matter if it was Facebook Sign-in or others. It will always work using
                // GoogleAuthProvider for whatever the provider.
                credential = GoogleAuthProvider.getCredential(token, null);
            }

            // We have to reauthenticate user because we don't know how long it was the sign-in.
            // Calling reauthenticate, will update the user login and prevent FirebaseException
            // (CREDENTIAL_TOO_OLD_LOGIN_AGAIN) on user.delete()
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //Calling delete to remove the user and wait for a result.
                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        usersModel.deleteUser();
                                        mAuth.signOut();
                                    } else {
                                        showErrorDialog(getActivity(),
                                                new FailedToDeleteUserException().getMessage());
                                    }
                                }
                            });
                        }
                    });
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
            actionBar.setTitle(R.string.my_account);
        }

    }
}
