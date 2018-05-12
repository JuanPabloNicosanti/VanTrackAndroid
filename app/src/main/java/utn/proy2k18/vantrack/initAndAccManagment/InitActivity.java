package utn.proy2k18.vantrack.initAndAccManagment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import utn.proy2k18.vantrack.mainFunctionality.CentralActivity;
import utn.proy2k18.vantrack.R;

public class InitActivity extends AppCompatActivity {
    private Button buttonLogIn;
    private Button buttonForgotPassword;
    private Button buttonSignUp;
    private Button buttonPrueba;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_activity);
        this.init();
        Log.d("Init", "Init Done");
    }

    public void init(){
        buttonLogIn = (Button) findViewById(R.id.button_log_in);
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goLogInActivity();
            }
        });
        buttonForgotPassword = (Button) findViewById(R.id.button_forgot_password);
        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotForgotPasswordActivity();
            }
        });
        buttonSignUp = (Button) findViewById(R.id.button_sign_up);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goSignUpActivity();
            }
        });
        buttonPrueba = (Button) findViewById(R.id.buttonPrueba);
        buttonPrueba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goCentralActivity();
            }
        });
    }
    public void goLogInActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
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
    public void goCentralActivity(){
        Intent intent = new Intent(this, CentralActivity.class);
        startActivity(intent);
    }
}
