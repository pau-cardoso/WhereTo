package com.example.whereto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    public static final String TAG = "SignupActivity";
    public static final int USERNAME_TAKEN = 202; // Error code for when the username is already taken
    public static final int USER_EMAIL_TAKEN = 203; // Error code for when the email is already taken

    EditText etUsernameS;
    EditText etPasswordS;
    EditText etEmailS;
    Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Finding view components of layout
        etUsernameS = findViewById(R.id.etUsernameS);
        etPasswordS = findViewById(R.id.etPasswordS);
        etEmailS = findViewById(R.id.etEmailS);
        btnSignup = findViewById(R.id.btnSignup);

        // When user clicks on Sign up
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick signup button");
                String username = etUsernameS.getText().toString();
                String password = etPasswordS.getText().toString();
                String email = etEmailS.getText().toString();

                // Create the ParseUser
                ParseUser user = new ParseUser();
                if (username != "" && password != "") {
                    // Set core properties
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setEmail(email);
                } else {
                    Toast.makeText(SignupActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                }

                // Invoke signUpInBackground
                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            // Sign up succeeded
                            goMainActivity();
                            finish();
                        } else {
                            // Sign up didn't succeed
                            Log.e("TAG", "Signup failed. Code: " + String.valueOf(e.getCode()), e); // Error message
                            if (e.getCode() == USERNAME_TAKEN) {
                                // When the username is already taken in the database
                                Toast.makeText(SignupActivity.this, "Username is already taken", Toast.LENGTH_SHORT).show();
                            }
                            else if (e.getCode() == USER_EMAIL_TAKEN) {
                                // When the email is already taken in the database
                                Toast.makeText(SignupActivity.this, "Email is already taken", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(SignupActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }

    // Function for opening the main view when logged in
    private void goMainActivity() {
        Log.i(TAG, "Entered goMainActivity");
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}