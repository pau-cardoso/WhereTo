package com.example.whereto.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
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
import com.example.whereto.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.chip.ChipGroup;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CreateActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "CreateActivity";
    private static final int EAT_CHIP = R.id.chipEat;
    private static final int STAY_CHIP = R.id.chipStay;
    private static final int VISIT_CHIP = R.id.chipVisit;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 253;
    public static final int PICK_PHOTO_REQUEST_CODE = 1046;
    public static final int NEW_LOCATION_ACTIVITY_REQUEST_CODE = 942;
    public String photoFileName = "photo.jpg";

    File photoFile;
    EditText etPlace;
    EditText etReview;
    RatingBar rbStars;
    RatingBar rbPrice;
    ChipGroup chipGroup;
    Button btnCapture;
    Button btnSubmit;
    Button btnSelect;
    ImageView ivMap;

    GoogleMap mMap;
    MapView mapCreate;
    MapFragment mapFragment;
    Location currentLocation;
    LatLng latLngCurrentLocation;

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
        btnSelect = findViewById(R.id.btnSelect);
        mapCreate = findViewById(R.id.mapCreate);

        currentLocation = getIntent().getParcelableExtra("location");
        latLngCurrentLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        initMap();

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(v);
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
                ParseGeoPoint location = new ParseGeoPoint(latLngCurrentLocation.latitude, latLngCurrentLocation.longitude);

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
                //FloatingActionButton btnAdd = findViewById(R.id.btnAdd);
                //btnAdd.setVisibility(View.VISIBLE);
                Intent intent = new Intent();
                intent.putExtra("recommendation", Parcels.wrap(recommendation));
                setResult(RESULT_OK, intent); // set result code and bundle data for response
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
        Uri fileProvider = FileProvider.getUriForFile(CreateActivity.this, "com.codepathx.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // As long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_REQUEST_CODE);
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

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handling data when images get captured
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

        // Handling data when an image is picked
        if ((data != null) && requestCode == PICK_PHOTO_REQUEST_CODE) {
            Uri photoUri = data.getData();
            File mediaDir = new File(photoUri.getPath(), TAG);
            // Return the file target for the photo based on filename
            photoFile = new File(mediaDir.getPath() + File.separator + photoFileName);
            // Load the image located at photoUri into selectedImage
            Bitmap selectedImage = loadFromUri(photoUri);
            // Load the selected image into a preview
            ImageView ivPreview = (ImageView) findViewById(R.id.ivPicture);
            ivPreview.setImageBitmap(selectedImage);
        }

        // Handling data when a new location is saved
        if (requestCode == NEW_LOCATION_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            latLngCurrentLocation = (LatLng) data.getExtras().get("latLngLocation");
            Log.d(TAG, "onActivityResult: the newLocation is: " + latLngCurrentLocation);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCurrentLocation, 18));
        }
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        if (mapCreate != null) {
            Log.d(TAG, "initMap: entered if statement");
            mapCreate.onCreate(null);
            mapCreate.onResume();
            mapCreate.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        mMap = googleMap;
        Log.d(TAG, "onMapReady: entered onMapReady");

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        /*mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Your location"));*/
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull @NotNull LatLng latLng) {
                Log.d(TAG, "onMapClick: clicked on map!");

                Intent intent = new Intent(CreateActivity.this, NewLocationActivity.class);
                intent.putExtra("currentLocation", currentLocation);
                startActivityForResult(intent, NEW_LOCATION_ACTIVITY_REQUEST_CODE);
            }
        });
    }
}
