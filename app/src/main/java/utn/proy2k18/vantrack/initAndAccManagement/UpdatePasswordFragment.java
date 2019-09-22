package utn.proy2k18.vantrack.initAndAccManagement;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import mainFunctionality.AccountFragment;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.VanTrackApplication;
import utn.proy2k18.vantrack.exceptions.FailedToModifyUserException;
import utn.proy2k18.vantrack.exceptions.InvalidPasswordException;
import utn.proy2k18.vantrack.viewModels.UsersViewModel;

public class UpdatePasswordFragment extends Fragment {

    private FirebaseUser currentUser;
    private UsersViewModel usersViewModel;
    private TextView originalPassword;
    private TextView newPassword;
    private TextView newPasswordCopy;
    private View mProgressView;
    private View mUpdatePasswordFormView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        usersViewModel = UsersViewModel.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_password, container, false);

        originalPassword = view.findViewById(R.id.update_pwd_original);
        newPassword = view.findViewById(R.id.update_pwd_new);
        newPasswordCopy = view.findViewById(R.id.update_pwd_new_copy);

        Button confirmUpdateButton = view.findViewById(R.id.confirm_update_pwd_button);
        confirmUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    checkInputs(newPassword, newPasswordCopy);
                    showProgress(true);
                    updateUserPassword(newPassword.getText().toString());
                } catch (FailedToModifyUserException | InvalidPasswordException e) {
                    showErrorDialog(getActivity(), e.getMessage());
                    showProgress(false);
                }
            }
        });

        mUpdatePasswordFormView = view.findViewById(R.id.update_pwd_form);
        mProgressView = view.findViewById(R.id.update_password_progress);

        return view;
    }

    private void checkInputs(TextView newPasswordTV, TextView newPasswordCopyTV)
            throws InvalidPasswordException {
        String newPassword = newPasswordTV.getText().toString();
        String newPasswordCopy = newPasswordCopyTV.getText().toString();
        String errorMsg = "";

        if (newPassword.equals(""))
            errorMsg = "Complete y repita la contraseña";

        if (newPassword.contains(" "))
            errorMsg = "La contraseña no puede contener espacios en blanco";

        if (!newPassword.equals(newPasswordCopy))
            errorMsg = "Las contraseñas no coinciden";

        if (newPassword.length() < 5)
            errorMsg = "Las contraseña es muy corta";

        if (!errorMsg.equals(""))
            throw new InvalidPasswordException(errorMsg);
    }

    private void updateUserPassword(String newPassword) {
        String originalPwd = originalPassword.getText().toString();
        if (!usersViewModel.isValidPassword(currentUser.getEmail(), originalPwd)) {
            throw new InvalidPasswordException(getString(R.string.wrong_password));
        }
        String origPwdHash = usersViewModel.hashUserPassword(originalPwd);

        //You need to get here the token you saved at logging-in time.
        String token = VanTrackApplication.getGoogleToken();
        AuthCredential credential;

        //This means you didn't have the token because user used like Facebook Sign-in method.
        if (token == null) {
            credential = EmailAuthProvider.getCredential(currentUser.getEmail(), origPwdHash);
        } else {
            // Doesn't matter if it was Facebook Sign-in or others. It will always work using
            // GoogleAuthProvider for whatever the provider.
            credential = GoogleAuthProvider.getCredential(token, null);
        }

        // We have to reauthenticate user because we don't know how long it was the sign-in.
        // Calling reauthenticate, will update the user login and prevent FirebaseException
        // (CREDENTIAL_TOO_OLD_LOGIN_AGAIN) on user.updatePassword()
        currentUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            String newPwdHash = usersViewModel.hashUserPassword(newPassword);
                            updateFirebasePassword(newPwdHash);
                        } else {
                            showErrorDialog(getActivity(),
                                    new FailedToModifyUserException().getMessage());
                        }
                    }
                });
    }

    private void updateFirebasePassword(String newPwdHash) {
        currentUser.updatePassword(newPwdHash)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("UpdatePassword:success", "User password updated.");
                            usersViewModel.modifyUserPassword(currentUser.getEmail(), newPwdHash);
                            Toast.makeText(getActivity(), "Contraseña actualizada.",
                                    Toast.LENGTH_SHORT).show();
                            setFragment(new AccountFragment());
                            showProgress(false);
                        } else {
                            Log.d("UpdatePassword:fail", "User password update fail.");
                        }
                    }
                });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    public void showErrorDialog(Activity activity, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setMessage(message)
                .setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setFragment(new AccountFragment());
                    }
                })
                .create();
        alertDialog.show();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mUpdatePasswordFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mUpdatePasswordFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mUpdatePasswordFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mUpdatePasswordFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
