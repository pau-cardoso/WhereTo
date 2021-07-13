package com.example.whereto;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.whereto.Fragments.MapFragment;
import com.example.whereto.Fragments.ProfileFragment;
import com.example.whereto.Fragments.RecommendationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FragmentManager fragmentManager = getSupportFragmentManager();

        bottomNavigation = findViewById(R.id.bottomNavigation);
        // handle navigation selection
        bottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment fragment;
                        switch (item.getItemId()) {
                            case R.id.navigation_map:
                                Toast.makeText(MainActivity.this, "Home!", Toast.LENGTH_SHORT).show();
                                fragment = new MapFragment();
                                break;
                            case R.id.navigation_recommendations:
                                Toast.makeText(MainActivity.this, "New post!", Toast.LENGTH_SHORT).show();
                                fragment = new RecommendationsFragment();
                                break;
                            case R.id.navigation_profile:
                            default:
                                Toast.makeText(MainActivity.this, "Profile!", Toast.LENGTH_SHORT).show();
                                fragment = new ProfileFragment();
                                break;
                        }
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                        return true;
                    }
                });
        // Set default selection
        bottomNavigation.setSelectedItemId(R.id.navigation_map);
    }

}