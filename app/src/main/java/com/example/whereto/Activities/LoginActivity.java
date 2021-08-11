package com.example.whereto.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whereto.R;
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

        // If the user is already logged in, show the main screen
        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        // Finding the views from the layout
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // When user clicks login, verifies login credentials on database
        btnLogin.setOnClickListener(new View.OnClickListener() { // Login button
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick login button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password); // Attempting to log in the user
            }
        });
    }

    // Function to verify the user credentials when trying to log in
    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user " + username + password);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                // Problem with login
                if (e != null) {
                    Log.e(TAG, "Issue with login ", e);
                    Toast.makeText(LoginActivity.this, "Issue with login", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Login successful
                goMainActivity();
                Toast.makeText(LoginActivity.this, "Success!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goMainActivity() {
        Log.i(TAG, "Entered goMainActivity");
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finishAffinity();
    }
}