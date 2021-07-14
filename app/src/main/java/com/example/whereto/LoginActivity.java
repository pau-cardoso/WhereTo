package com.example.whereto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    EditText etUsername;
    EditText etPassword;
    Button btnLogin;
    TextView tvSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (ParseUser.getCurrentUser() != null) {
            //goMainActivity();
            goMainActivity();
        }

        // Finding the views from the layout
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);

        btnLogin.setOnClickListener(new View.OnClickListener() { // Login button
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick login button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password); // Attempting to log in the user
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                LoginActivity.this.startActivity(i);
            }
        });
    }

    private void goFeedActivity() {
        Log.i(TAG, "Entered goFeedActivity");
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user " + username + password);

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) { // Problem with login
                    Log.e(TAG, "Issue with login ", e);
                    Toast.makeText(LoginActivity.this, "Issue with login", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Login successful
                goMainActivity();
                Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goMainActivity() {
        Log.i(TAG, "Entered goMainActivity");
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}