package mainFunctionality;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.exceptions.BackendConnectionException;
import utn.proy2k18.vantrack.exceptions.BackendException;
import utn.proy2k18.vantrack.exceptions.FailedToDeleteUsernameException;
import utn.proy2k18.vantrack.exceptions.FailedToModifyUsernameException;
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
        View view = inflater.inflate(R.layout.fragment_account_user, container, false);

        AutoCompleteTextView name = view.findViewById(R.id.userFirstNameMyAccount);
        AutoCompleteTextView surname = view.findViewById(R.id.userLastNameMyAccount);
        TextView email = view.findViewById(R.id.userEmailMyAccount);
        Button modifyAccount = view.findViewById(R.id.btn_modify_account);
        Button removeAccount = view.findViewById(R.id.btn_remove_account);

        try {
            User user = usersModel.getUser();
            name.append(user.getName());
            surname.append(user.getSurname());
            email.append(user.getEmail());
        } catch (BackendException be) {
            showErrorDialog(getActivity(), be.getErrorMsg());
        } catch (BackendConnectionException bce) {
            showErrorDialog(getActivity(), bce.getMessage());
        }

        modifyAccount.setOnClickListener(new View.OnClickListener() {
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
                                } catch (BackendException be) {
                                    dialog.dismiss();
                                    showErrorDialog(getActivity(), be.getErrorMsg());
                                } catch (BackendConnectionException |
                                        FailedToModifyUsernameException e) {
                                    dialog.dismiss();
                                    showErrorDialog(getActivity(), e.getMessage());
                                }

                                Intent intent = new Intent(getContext(), CentralActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancelar",null);

                AlertDialog alert = builder.create();
                alert.show();
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

    private void showDeleteUserAlert() {
        final AlertDialog.Builder alertDialogBuilder = getAlertDialogBuilder(
                R.string.delete_account_alert_msg);
        alertDialogBuilder
                .setMessage(R.string.delete_account_warning)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        try {
                            usersModel.deleteUser();
                            mAuth.signOut();
                        } catch (BackendException be) {
                            showErrorDialog(getActivity(), be.getErrorMsg());
                        } catch (FailedToDeleteUsernameException | BackendConnectionException e) {
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
