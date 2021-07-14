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
    public static final int USERNAME_TAKEN = 202;
    public static final int USER_EMAIL_TAKEN = 203;

    EditText etUsernameS;
    EditText etPasswordS;
    Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUsernameS = findViewById(R.id.etUsernameS);
        etPasswordS = findViewById(R.id.etPasswordS);
        btnSignup = findViewById(R.id.btnSignup);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick signup button");
                String username = etUsernameS.getText().toString();
                String password = etPasswordS.getText().toString();

                // Create the ParseUser
                ParseUser user = new ParseUser();
                if (username != "" && password != "") {
                    // Set core properties
                    user.setUsername(username);
                    user.setPassword(password);
                } else {
                    Toast.makeText(SignupActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                }

                // Invoke signUpInBackground
                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            goFeedActivity();
                        } else {
                            // Sign up didn't succeed
                            Log.e("TAG", "Signup failed. Code: " + String.valueOf(e.getCode()), e); // Error message
                            if (e.getCode() == USERNAME_TAKEN) { // When the username is already taken in the database
                                Toast.makeText(SignupActivity.this, "Username is already taken", Toast.LENGTH_SHORT).show();
                            }
                            else if (e.getCode() == USER_EMAIL_TAKEN) { // When the email is already taken in the database
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

    private void goFeedActivity() {
        Log.i(TAG, "Entered goFeedActivity");
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}