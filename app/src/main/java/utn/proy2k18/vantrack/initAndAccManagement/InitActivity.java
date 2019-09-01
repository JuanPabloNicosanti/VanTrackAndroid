package utn.proy2k18.vantrack.initAndAccManagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import mainFunctionality.CentralActivity;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.VanTrackApplication;
import utn.proy2k18.vantrack.exceptions.BackendConnectionException;
import utn.proy2k18.vantrack.exceptions.BackendException;
import utn.proy2k18.vantrack.models.User;
import utn.proy2k18.vantrack.viewModels.UsersViewModel;

public class InitActivity extends AppCompatActivity {

    private static final String ARG_PARAM1 = "avoid_automatic_login";
    private static final int RC_SIGN_IN = 2;

    private SignInButton buttonGoogleSignIn;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private Activity activity = this;
    private Boolean avoidAutomaticLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_activity);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            avoidAutomaticLogin = b.getBoolean(ARG_PARAM1);
        }

        this.init();
        this.googleSignInOnCreate();
        Log.d("Init", "Init Done");
    }

    @Override
    protected void onStart(){
        super.onStart();
        this.googleSignInOnStart();
    }

    private void googleSignInOnCreate() {
        buttonGoogleSignIn = findViewById(R.id.button_google_sign_in);
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
                if(firebaseAuth.getCurrentUser() != null && !avoidAutomaticLogin) {
                    final FirebaseUser user = firebaseAuth.getCurrentUser();
                    goCentralActivity(user);
                }
            }
        };
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(InitActivity.this, "Se produjo un error",
                                Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void googleSignInOnStart(){
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                final GoogleSignInAccount account = result.getSignInAccount();
                VanTrackApplication.setGoogleToken(account.getIdToken());
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(InitActivity.this, "No se pudo autenticar. Compruebe " +
                        "su conexión WiFi e intente otra vez.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("GoogleActivity", "signInWithCredential:success");
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            if(isNew) {
                                UsersViewModel usersViewModel = UsersViewModel.getInstance();
                                User userForDB = new User(account.getGivenName(),
                                        account.getFamilyName(),"-", account.getEmail(),
                                        "-");
                                try {
                                    usersViewModel.registerUser(userForDB);
                                } catch (JsonProcessingException jpe) {
                                    Toast.makeText(activity, "Error en el login. " +
                                            "Inténtelo más tarde.", Toast.LENGTH_SHORT).show();
                                    goInitActivity();
                                } catch (BackendException | BackendConnectionException be) {
                                    Toast.makeText(activity, be.getMessage(), Toast.LENGTH_SHORT).show();
                                    goInitActivity();
                                }
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("GoogleActivity", "signInWithCredential:failure",
                                    task.getException());
                            Toast.makeText(InitActivity.this, "Error de autenticación.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
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

    public void init(){
        final Button buttonLogIn = (Button) findViewById(R.id.button_log_in);
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goLogInActivity();
            }
        });
        final Button buttonForgotPassword = (Button) findViewById(R.id.button_forgot_password);
        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotForgotPasswordActivity();
            }
        });
        final Button buttonSignUp = (Button) findViewById(R.id.button_sign_up);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goSignUpActivity();
            }
        });
    }

    public void goLogInActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void goInitActivity(){
        Intent intent = new Intent(this, InitActivity.class);
        intent.putExtra("avoid_automatic_login", true);
        startActivity(intent);
    }

    public void gotForgotPasswordActivity(){
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    public void goSignUpActivity(){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void goCentralActivity(FirebaseUser user){
        Intent intent = new Intent(this, CentralActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}
