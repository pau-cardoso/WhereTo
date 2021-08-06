package com.example.whereto.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    }

    private void goMainActivity() {
        Log.i(TAG, "Entered goMainActivity");
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}