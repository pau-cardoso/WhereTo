package com.example.whereto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.whereto.Models.Recommendation;
import com.google.android.material.chip.ChipGroup;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class CreateActivity extends AppCompatActivity {

    private static final String TAG = "CreateActivity";
    private static final int EAT_CHIP = R.id.chipEat;
    private static final int STAY_CHIP = R.id.chipStay;
    private static final int VISIT_CHIP = R.id.chipVisit;

    EditText etPlace;
    EditText etReview;
    RatingBar rbStars;
    RatingBar rbPrice;
    ChipGroup chipGroup;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        // Find components in view
        etPlace = findViewById(R.id.etPlace);
        etReview = findViewById(R.id.etReview);
        rbStars = findViewById(R.id.rbStars);
        rbPrice = findViewById(R.id.rbPrice);
        chipGroup = findViewById(R.id.chipGroup);
        btnSubmit = findViewById(R.id.btnSignup);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String place = etPlace.getText().toString();
                final String review = etReview.getText().toString();
                final float ratingStars = rbStars.getRating();
                final float ratingPrice = rbPrice.getRating();
                final List<Integer> chipsSelected = chipGroup.getCheckedChipIds();
                ParseUser currentUser = ParseUser.getCurrentUser();
                ParseGeoPoint location = new ParseGeoPoint(40.0, -30.0);

                Recommendation recommendation = new Recommendation();

                for (int i = 0; i < chipsSelected.size(); i++) {
                    Log.i(TAG, "ID Chip: " + chipsSelected.get(i));
                    switch (chipsSelected.get(i)) {
                        case EAT_CHIP:
                            recommendation.setEat(true);
                            continue;
                        case STAY_CHIP:
                            recommendation.setStay(true);
                            continue;
                        case VISIT_CHIP:
                            recommendation.setVisit(true);
                    }
                }

                saveRecommendation(recommendation, place, review, ratingStars, ratingPrice, null, location, currentUser);

                // TODO conditionals for when fields are empty

                /*if (tweetContent.isEmpty()) {
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }*/
            }
        });
    }

    private void saveRecommendation(Recommendation recommendation, String place, String review, Number rate, Number priceRate, ParseFile picture, ParseGeoPoint location, ParseUser currentUser) {
        recommendation.setPlace(place);
        recommendation.setReview(review);
        recommendation.setRate(rate);
        recommendation.setPriceRate(priceRate);
        recommendation.setUser(currentUser);
        recommendation.setLocation(location);
        // TODO recommendation.setPicture(picture);

        recommendation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(CreateActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Post was successful!");
                //etDescription.setText("");
            }
        });
    }
}
