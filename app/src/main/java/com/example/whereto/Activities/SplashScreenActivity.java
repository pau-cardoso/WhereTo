package com.example.whereto.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.whereto.R;
import com.parse.ParseUser;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreenActivity";

    TextView tvLoginBtn;
    TextView tvCreateAccountBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        tvLoginBtn = findViewById(R.id.tvLoginBtn);
        tvCreateAccountBtn = findViewById(R.id.tvCreateAccountBtn);

        // If the user is already logged in, show the main screen
        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        tvLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goLoginActivity();
            }
        });

        tvCreateAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSignupActivity();
            }
        });
    }

    private void goSignupActivity() {
        Log.i(TAG, "Entered goSignupActivity");
        Intent i = new Intent(SplashScreenActivity.this, SignupActivity.class);
        SplashScreenActivity.this.startActivity(i);
    }

    private void goLoginActivity() {
        Log.i(TAG, "Entered goLoginActivity");
        Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
        startActivity(i);
    }

    private void goMainActivity() {
        Log.i(TAG, "Entered goMainActivity");
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}