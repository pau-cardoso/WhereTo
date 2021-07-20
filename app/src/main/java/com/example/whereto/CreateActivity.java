package com.example.whereto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.whereto.Models.Recommendation;
import com.google.android.material.chip.ChipGroup;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class CreateActivity extends AppCompatActivity {

    private static final String TAG = "CreateActivity";
    private static final int EAT_CHIP = R.id.chipEat;
    private static final int STAY_CHIP = R.id.chipStay;
    private static final int VISIT_CHIP = R.id.chipVisit;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 253;
    public String photoFileName = "photo.jpg";

    File photoFile;
    EditText etPlace;
    EditText etReview;
    RatingBar rbStars;
    RatingBar rbPrice;
    ChipGroup chipGroup;
    Button btnCapture;
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
        btnSubmit = findViewById(R.id.btnSubmit);
        btnCapture = findViewById(R.id.btnCapture);

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

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

                saveRecommendation(recommendation, place, review, ratingStars, ratingPrice, photoFile, location, currentUser);

                // TODO conditionals for when fields are empty

                /*if (tweetContent.isEmpty()) {
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }*/
            }
        });
    }

    private void saveRecommendation(Recommendation recommendation, String place, String review, Number rate, Number priceRate, File picture, ParseGeoPoint location, ParseUser currentUser) {
        recommendation.setPlace(place);
        recommendation.setReview(review);
        recommendation.setRate(rate);
        recommendation.setPriceRate(priceRate);
        recommendation.setUser(currentUser);
        recommendation.setLocation(location);
        recommendation.setPicture(new ParseFile(picture));

        recommendation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(CreateActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Post was successful!");
                finish(); // Closes window for new recommendation and back to main screen
            }
        });
    }

    public void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(CreateActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // As long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ImageView ivPreview = (ImageView) findViewById(R.id.ivPicture);
                ivPreview.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
