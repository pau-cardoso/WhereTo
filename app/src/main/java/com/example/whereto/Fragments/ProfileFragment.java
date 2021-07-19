package com.example.whereto.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.whereto.LoginActivity;
import com.example.whereto.R;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    
    public static final String TAG = "ProfileFragment";
    
    Button btnLogout;
    Toolbar tbProfile;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar.
        inflater.inflate(R.menu.profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnLogout1:
                Log.i(TAG, "onClick logout button");
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
                Log.i(TAG, "currentUser: " + currentUser);
                goLoginActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* Toolbar setup for Action Bar */
        tbProfile = view.findViewById(R.id.tbProfile); // Finds the toolbar component
        ((AppCompatActivity) getActivity()).setSupportActionBar(tbProfile); // Sets the toolbar as action bar
        setHasOptionsMenu(true); // Sets the action bar for options on menu
        //((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false); // Disables the showing of the title
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Profile"); // Changes action bar's title
    }

    private void goLoginActivity() {
        Log.i(TAG, "Entered goLoginActivity");
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
        getActivity().finish();
    }
}