package com.example.whereto.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.whereto.Adapters.CustomWindowAdapter;
import com.example.whereto.CreateActivity;
import com.example.whereto.Models.Recommendation;
import com.example.whereto.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    // Constants
    private static final String TAG = "MapFragment";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15;

    // Components
    private GoogleMap mMap;
    private MapView mMapView;
    private View mView;
    private EditText etSearch;

    // Location variables
    public List<Recommendation> allRecommendations = new ArrayList<>();
    private boolean locationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    public Location currentLocation;

    Toolbar tbMap;
    FloatingActionButton btnAdd;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_map, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Finding view components
        //tbMap = view.findViewById(R.id.tbMap);
        btnAdd = view.findViewById(R.id.btnAdd);
        mMapView = (MapView) mView.findViewById(R.id.map);
        //etSearch = view.findViewById(R.id.etSearch);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        if (!Places.isInitialized()) {
            Places.initialize(getContext(), getString(R.string.google_maps_key));
        }
        PlacesClient placesClient = Places.createClient(getContext());

        Log.d(TAG, "onViewCreated");
        Log.d(TAG, "onViewCreated: AutocompleteSupportFragment: " + autocompleteFragment);
        getLocationPermission();
        //init();
        //initMap();
        Log.d(TAG, "Location: " + String.valueOf(currentLocation));

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getLatLng());
            }


            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), CreateActivity.class);
                i.putExtra("location", currentLocation);
                startActivity(i);
            }
        });
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    private void init() {
        Log.d(TAG, "init: initializing");
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.getAction() == KeyEvent.ACTION_DOWN
                    || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    // Method for searching
                    geoLocate();
                }
                return false;
            }
        });
    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = etSearch.getText().toString();
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: found a location: " + address.toString());
            //Toast.makeText(getContext(), address.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        mMap = googleMap;
        Toast.makeText(getContext(), "Map is ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");

        if (locationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }

        queryAllRecommendations();
        for (Recommendation recommendation : allRecommendations) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(recommendation.getLocation().getLatitude(), recommendation.getLocation().getLongitude()))
                    .title(recommendation.getPlace())
                    .snippet(recommendation.getReview())
                    .draggable(true));
        }
        mMap.setInfoWindowAdapter(new CustomWindowAdapter(getLayoutInflater()));
    }

    public void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        Log.d(TAG, "getLocationPermission: getting location permissions");

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Entered getLocationPermissions");
                if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Entered first if on getLocationPermissions");
                    locationPermissionGranted = true;
                    initMap();
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            permissions, LOCATION_PERMISSION_REQUEST_CODE);
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        locationPermissionGranted = false;
        Log.d(TAG, "onRequestPermissionResult: called");

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        locationPermissionGranted = false;
                        Log.d(TAG, "onRequestPermissionResult: permission failed");
                        return;
                    }
                    Log.d(TAG, "onRequestPermissionResult: permission granted");
                    locationPermissionGranted = true;
                    // initialize map
                    initMap();
                }
            }
        }
    }

    public void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the device's current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        try {
            if (locationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(getContext(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    public void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving the camara to lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    protected void queryAllRecommendations() {
        Log.i(TAG, "Entered queryAllRecommendations");
        // specify what type of data we want to query - Recommendation.class
        ParseQuery<Recommendation> query = ParseQuery.getQuery(Recommendation.class);
        // include data referred by user key
        query.include(Recommendation.KEY_USER);
        // limit query to latest 20 items
        query.setLimit(20);

        // Fetches data synchronously
        try {
            List<Recommendation> results = query.find();
            for (Recommendation result : results) {
                allRecommendations.add(result);
                System.out.println("Object found " + result.getObjectId());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}

