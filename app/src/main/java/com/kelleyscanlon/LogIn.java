package com.kelleyscanlon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class LogIn extends Activity {

    final Context context = this;
    private Intent intent;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private String email;
    private String password;
    private UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // Create User Session Manager
        session = new UserSessionManager(getApplicationContext());

        //grab reference to LoginButton and EditTexts
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        Button loginButton = (Button) findViewById(R.id.buttonLogin);

        // edit text listeners
        editTextEmail.setOnClickListener(clearHintListener);
        editTextPassword.setOnClickListener(clearHintListener);

        // login button listener
        loginButton.setOnClickListener(loginListener);

    }

    OnClickListener clearHintListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {

                case R.id.editTextEmail:
                    // clear email hint text
                    editTextEmail.setText("");
                    break;
                case R.id.editTextPassword:
                    // clear password hint text
                    editTextPassword.setText("");
                    break;
                default:
                    // nothing....
                    break;
            }
        }
    };

    OnClickListener loginListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            // get email and password values
            email = editTextEmail.getText().toString();
            password = editTextPassword.getText().toString();

            // if email and password are valid, start Main Activity
            if(isEmailValid(email) && isPasswordValid(password) ) {

                // set user login session
                session.createUserLoginSession(email, password);

                // start Main activity
                startActivity(new Intent(LogIn.this, MainActivity.class));
            }

            // if invalid email, set background to red
            if(!isEmailValid(email)) {
                editTextEmail.setBackgroundColor(getResources().getColor(R.color.opaque_red));
            }
            // if invalid password, set background to red
            if(!isPasswordValid(password)) {
                editTextPassword.setBackgroundColor(getResources().getColor(R.color.opaque_red));
            }
        }
    };

    private boolean isEmailValid(String email){
        // if email is empty, null, or equal to hint text
        if(email == null || email.trim().equals("") || email.equals("email...")) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isPasswordValid(String password){
        // if password is empty, null, or equal to hint text
        if(password == null || password.trim().equals("") || password.equals("password...")) {
            return false;
        } else {
            return true;
        }
    }

}
