package utn.proy2k18.vantrack.initAndAccManagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;

import mainFunctionality.CentralActivity;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.VanTrackApplication;
import utn.proy2k18.vantrack.exceptions.FailedToCreateUserException;
import utn.proy2k18.vantrack.models.User;
import utn.proy2k18.vantrack.viewModels.UsersViewModel;

public class InitActivity extends ProgressBarActivity {

    private static final String TAG = "GoogleActivity";
    private static final String ARG_PARAM1 = "avoid_automatic_login";
    private static final String ARG_PARAM2 = "sign_out";
    private static final String ARG_PARAM3 = "revoke_access";
    private static final int RC_SIGN_IN = 2;

    private Activity activity = this;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleSignInClient mGoogleSignInClient;
    private Boolean avoidAutomaticLogin = false;
    private Boolean signOut = false;
    private Boolean revokeAccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_activity);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            avoidAutomaticLogin = b.getBoolean(ARG_PARAM1);
            signOut = b.getBoolean(ARG_PARAM2);
            revokeAccess = b.getBoolean(ARG_PARAM3);
        }

        this.init();
        this.googleSignInOnCreate();
        if (signOut) {
            signOut();
        } else if (revokeAccess) {
            revokeAccess();
        } else {
            Log.d("Init", "Init Done");
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        this.googleSignInOnStart();
    }

    private void googleSignInOnCreate() {
        SignInButton buttonGoogleSignIn = findViewById(R.id.button_google_sign_in);
        buttonGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser != null && !avoidAutomaticLogin) {
                    if (hasGoogleSignIn(firebaseUser)) {
                        signIn();
                    } else {
                        goToActivity(CentralActivity.class);
                    }
                    showProgress(true);
                }
            }
        };
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private boolean hasGoogleSignIn(FirebaseUser firebaseUser) {
        for (UserInfo userInfo : firebaseUser.getProviderData()) {
            if (userInfo.getProviderId().equals("google.com"))
                return true;
        }
        return false;
    }

    public void googleSignInOnStart(){
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        showProgress(true);
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut();
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                VanTrackApplication.setGoogleToken(account.getIdToken());
                if (mAuth.getCurrentUser() != null) {
                    goToActivity(CentralActivity.class);
                } else {
                    avoidAutomaticLogin = true;
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(InitActivity.this, "No se pudo autenticar. Compruebe " +
                        "su conexión WiFi e intente otra vez.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            try {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithCredential:success");
                                if (task.getResult().getAdditionalUserInfo().isNewUser())
                                    addUserToDB(account);
                                goToActivity(CentralActivity.class);
                            } catch (FailedToCreateUserException fcue) {
                                deleteFirebaseUser(task.getResult().getUser());
                                showErrorDialog(activity, fcue.getMessage());
                                showProgress(false);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(InitActivity.this, "Error de autenticación.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addUserToDB(final GoogleSignInAccount account) throws FailedToCreateUserException {
        UsersViewModel usersViewModel = UsersViewModel.getInstance();
        User userForDB = new User(account.getGivenName(), account.getFamilyName(),"-",
                account.getEmail(),"-");
        usersViewModel.registerUser(userForDB);
    }

    private void deleteFirebaseUser(FirebaseUser firebaseUser) {
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    revokeAccess();
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

    private void init(){
        final Button buttonLogIn = findViewById(R.id.button_log_in);
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToActivity(LoginActivity.class);
            }
        });
        final Button buttonForgotPassword = findViewById(R.id.button_forgot_password);
        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToActivity(ForgotPasswordActivity.class);
            }
        });
        final Button buttonSignUp = findViewById(R.id.button_sign_up);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToActivity(SignUpActivity.class);
            }
        });

        mFormView = findViewById(R.id.init_form);
        mProgressView = findViewById(R.id.init_progress);
    }

    private void goToActivity(Class<?> activityToOpen) {
        startActivity(new Intent(this, activityToOpen));
    }
}
