package utn.proy2k18.vantrack.initAndAccManagement;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.List;

import utn.proy2k18.vantrack.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        this.firebaseAuthOnCreate();
        AutoCompleteTextView mEmailView = findViewById(R.id.email);
        Button forgotPasswordButton = findViewById(R.id.email_forgot_password);
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task<SignInMethodQueryResult> signInMethods = mAuth.fetchSignInMethodsForEmail(
                        mEmailView.getText().toString());
                signInMethods.addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        List<String> signInMethods = task.getResult().getSignInMethods();
                        if (signInMethods.contains("password")) {
                            try {
                                mAuth.sendPasswordResetEmail(mEmailView.getText().toString());
                                showDialogAndExitActivity(
                                        "Se ha enviado un email para restaurar la contraseña.");
                            } catch(Exception e) {
                                showDialogAndExitActivity("Error al enviar el email.");
                            }
                        } else if (signInMethods.contains("google.com")) {
                            showDialogAndExitActivity("Las contraseñas de las cuentas creadas " +
                                    "con Google no se pueden resetear de esta forma.");
                        } else {
                            showDialogAndExitActivity("No se puede enviar el email.");
                        }
                    }
                });
            }
        });
    }

    private void firebaseAuthOnCreate() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void showDialogAndExitActivity(String message) {
        Activity activity = ForgotPasswordActivity.this;
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setMessage(message)
                .setNeutralButton("Aceptar",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(activity, InitActivity.class));
                    }
                })
                .create();
        alertDialog.show();
    }
}
