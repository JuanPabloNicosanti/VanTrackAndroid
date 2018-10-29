package utn.proy2k18.vantrack.initAndAccManagement;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.models.User;
import utn.proy2k18.vantrack.viewModels.UsersViewModel;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView name;
    private TextView surname;
    private TextView dni;
    private TextView email;
    private TextView emailCopy;
    private TextView password;
    private TextView passwordCopy;
    private Button signUpButton;
    private String errorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        this.init();
        this.firebaseAuthOnCreate();
    }

    private void init() {

        name = findViewById(R.id.userFirstName);
        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
          @Override
          public void onFocusChange(View v, boolean hasFocus) {
              if (name.getText().toString().equals(""))
                  name.setError("Complete el nombre");
              else name.setError(null);
          }
    });
        surname = findViewById(R.id.userLastName);
        surname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (surname.getText().toString().equals(""))
                    surname.setError("Complete el apellido");
                else surname.setError(null);
            }
        });
        dni = findViewById(R.id.userDNI);
        dni.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (dni.getText().length() != 7 && dni.getText().length() != 8)
                    dni.setError("El DNI debe contener 7 u 8 números");
                else dni.setError(null);
            }
    });

        email = findViewById(R.id.email_original);
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!emailCopy.getText().toString().equals("") &&
                        !emailCopy.getText().toString().equals(email.getText().toString()) && !hasFocus)
                    emailCopy.setError("Los emails no coinciden");
                if(emailCopy.getText().toString().equals(email.getText().toString()) && !hasFocus)
                    emailCopy.setError(null);
            }
        });
        emailCopy = findViewById(R.id.email_copy);
        emailCopy.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!email.getText().toString().equals(emailCopy.getText().toString()) && !hasFocus)
                    emailCopy.setError("Los emails no coinciden");
                if(emailCopy.getText().toString().equals(email.getText().toString()) && !hasFocus)
                    emailCopy.setError(null);
            }
        });
        password = findViewById(R.id.password_original);
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!passwordCopy.getText().toString().equals("") &&
                        !passwordCopy.getText().toString().equals(password.getText().toString()) && !hasFocus)
                    passwordCopy.setError("Las contraseñas no coinciden");
                if(passwordCopy.getText().toString().equals(password.getText().toString()) && !hasFocus)
                    passwordCopy.setError(null);
            }
        });
        passwordCopy = findViewById(R.id.password_copy);
        passwordCopy.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!password.getText().toString().equals(passwordCopy.getText().toString()) && !hasFocus)
                    passwordCopy.setError("Las contraseñas no coinciden");
                if(passwordCopy.getText().toString().equals(password.getText().toString()) && !hasFocus)
                    passwordCopy.setError(null);
            }
        });
        signUpButton = findViewById(R.id.email_sign_in_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputsAreValid()) {
                    User user = new User(name.getText().toString(), surname.getText().toString(), dni.getText().toString(), email.getText().toString(), password.getText().toString());
                    signUp(user);
                }
                else
                    Toast.makeText(SignUpActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void firebaseAuthOnCreate() {
        mAuth = FirebaseAuth.getInstance();
    }


    private Boolean inputsAreValid(){
        if(name.getText().toString().equals("") || surname.getText().toString().equals("") || dni.getText().toString().equals("") || email.getText().toString().equals("") || emailCopy.getText().toString().equals("")){
            errorMsg = "Complete los datos faltantes";
            return false;
        }
        if(password.getText().toString().equals("") || passwordCopy.getText().toString().equals("")){
            errorMsg = "Complete y repita la contraseña";
            return false;
        }
        if(!email.getText().toString().matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")){
            errorMsg = "Formato de email inválido";
            return false;
        }
        if(emailCopy.getError() != null){
            errorMsg = "Los emails no coinciden";
            return false;
        }
        if(passwordCopy.getError() != null){
            errorMsg = "Las contraseñas no coinciden";
            return false;
        }
        if(!email.getText().toString().equals(emailCopy.getText().toString())){
            errorMsg = "Los emails no coinciden";
            return false;
        }
        if(!password.getText().toString().equals(passwordCopy.getText().toString())){
            errorMsg = "Las contraseñas no coinciden";
            return false;
        }
        if(password.getText().toString().length() < 5){
            errorMsg = "Las contraseña es muy corta";
            return false;
        }

        return true;
    }

    private void signUp(final User user){
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            UsersViewModel usersViewModel = UsersViewModel.getInstance();
                            usersViewModel.registerUser(user);
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SignUp", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SignUp", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "No se pudo crear el nuevo usuario.", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }
}
