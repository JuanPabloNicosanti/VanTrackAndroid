package initAndAccManagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import utn.proy2k18.vantrack.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button forgotPasswordButton;
    private FirebaseAuth mAuth;
    private AutoCompleteTextView mEmailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        this.firebaseAuthOnCreate();
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        forgotPasswordButton = findViewById(R.id.email_forgot_password);
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task<SignInMethodQueryResult> signInMethods = mAuth.fetchSignInMethodsForEmail(mEmailView.getText().toString());
                signInMethods.addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if(task.getResult().getSignInMethods().contains("password")){
                            try{
                                mAuth.sendPasswordResetEmail(mEmailView.getText().toString()) ;
                                Toast.makeText(ForgotPasswordActivity.this, "Se ha enviado un email para restaurar la contraseña", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ForgotPasswordActivity.this, InitActivity.class);
                                startActivity(intent);
                            }catch(Exception e){
                                Toast.makeText(ForgotPasswordActivity.this, "Error al enviar el mail", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(ForgotPasswordActivity.this, "No se ha podido enviar el mail", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
    private void firebaseAuthOnCreate() {
        mAuth = FirebaseAuth.getInstance();
    }

}
