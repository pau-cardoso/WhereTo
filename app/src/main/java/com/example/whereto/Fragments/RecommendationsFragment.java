package com.example.whereto.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.whereto.Adapters.RecommendationAdapter;
import com.example.whereto.Adapters.ViewPagerAdapter;
import com.example.whereto.Activities.CreateActivity;
import com.example.whereto.Models.Recommendation;
import com.example.whereto.R;
import com.example.whereto.databinding.ActivityMainBinding;
import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecommendationsFragment} factory method to
 * create an instance of this fragment.
 */
public class RecommendationsFragment extends Fragment {

    private static final String TAG = "RecommendationsFragment";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 215;

    // Variables
    private boolean locationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    public Location currentLocation;

    RecommendationAdapter adapter;
    List<Recommendation> allRecommendations;
    TabLayout tlTabs;
    ViewPager viewPager;
    ViewPagerAdapter vpAdapter;
    FloatingActionButton btnAdd;
    View circle;
    NavigationTabStrip navigationTabStrip;

    public RecommendationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recommendations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        @NonNull ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());

        // Find components from view
        //tlTabs = view.findViewById(R.id.tlTabs);
        viewPager = view.findViewById(R.id.viewPager);
        btnAdd = view.findViewById(R.id.btnAdd);
        circle = view.findViewById(R.id.circle);
        navigationTabStrip = view.findViewById(R.id.navigationTabStrip);

        // Getting location permission and starting the map
        getLocationPermission();

        if (locationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.circle_explosion_anim);
        animation.setDuration(700);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent i = new Intent(getContext(), CreateActivity.class);
                i.putExtra("location", currentLocation);
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);
                binding.getRoot().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.purple_500));
                circle.setVisibility(View.INVISIBLE);
                btnAdd.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAdd.setVisibility(View.INVISIBLE);
                circle.setVisibility(View.VISIBLE);
                circle.startAnimation(animation);
            }
        });

        // Setting the adapter for tab view by category
        setUI();
    }

    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
    public void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    // Checks user's result of permission of location and if give, saves true in variable
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
                }
            }
        }
    }

    // Once given permissions, gets location of user's device and saves it in variable
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

    private void setUI() {
        viewPager.setAdapter(new ViewPagerAdapter(getActivity().getSupportFragmentManager()));
        navigationTabStrip.setViewPager(viewPager);
    }
}