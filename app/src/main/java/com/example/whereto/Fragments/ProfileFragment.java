package com.example.whereto.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whereto.Activities.LoginActivity;
import com.example.whereto.Activities.SplashScreenActivity;
import com.example.whereto.Models.Followers;
import com.example.whereto.Models.Recommendation;
import com.example.whereto.Models.User;
import com.example.whereto.R;
import com.example.whereto.Adapters.RecommendationAdapter;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    
    public static final String TAG = "ProfileFragment";
    private static final String KEY_PROFILE_PICTURE = "profilePicture";
    protected ParseUser currentUser = ParseUser.getCurrentUser();

    Toolbar tbProfile;
    ImageView ivProfilePic;
    TextView tvName;
    TextView tvUsername;
    TextView tvNoFollowers;
    TextView tvNoFollowing;
    RecyclerView rvProfile;
    RecommendationAdapter adapter;
    List<Recommendation> ownRecommendations;
    RelativeLayout rlLoading;

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
                goSplashActivity();
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
        Log.d(TAG, "Entered ProfileFragment");

        /* Find components from view */
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        tvName = view.findViewById(R.id.tvNameP);
        tvUsername = view.findViewById(R.id.tvUsernameP);
        rvProfile = view.findViewById(R.id.rvProfile);
        tvNoFollowers = view.findViewById(R.id.tvNoFollowers);
        tvNoFollowing = view.findViewById(R.id.tvNoFollowing);
        rlLoading = view.findViewById(R.id.rlLoading);

        /* Toolbar setup for Action Bar */
        tbProfile = view.findViewById(R.id.tbProfile); // Finds the toolbar component
        ((AppCompatActivity) getActivity()).setSupportActionBar(tbProfile); // Sets the toolbar as action bar
        tbProfile.setTitle("");
        setHasOptionsMenu(true); // Sets the action bar for options on menu

        /* Setting the text views and image */
        tvName.setText(currentUser.getString("name"));
        tvUsername.setText("@" + currentUser.getUsername());
        Glide.with(this).load(currentUser.getParseFile(KEY_PROFILE_PICTURE).getUrl()).circleCrop().into(ivProfilePic);
        tvNoFollowers.setText(String.valueOf(getFollowers()));
        tvNoFollowing.setText(String.valueOf(getFollowing()));

        // initialize the array that will hold posts and create a PostsAdapter
        ownRecommendations = new ArrayList<>();
        adapter = new RecommendationAdapter(getContext(), ownRecommendations);
        // set the adapter on the recycler view
        rvProfile.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvProfile.setLayoutManager(new LinearLayoutManager(getContext()));
        // query recommendations of current user from Parse
        queryOwnRecommendations();
    }

    private void queryOwnRecommendations() {
        // specify what type of data we want to query - Recommendation.class
        ParseQuery<Recommendation> query = ParseQuery.getQuery(Recommendation.class);
        // include data referred by user key
        query.include(Recommendation.KEY_USER);
        // Finds recommendations posted by current user
        query.whereEqualTo(Recommendation.KEY_USER, currentUser);
        // limit query to latest 20 items
        query.setLimit(20);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground((recommendations, e) -> {
            // check for errors
            if (e != null) {
                Log.e(TAG, "Issue with getting recommendations", e);
                return;
            }

            // save received posts to list and notify adapter of new data
            ownRecommendations.addAll(recommendations);
            // TODO refresh
            adapter.clear();
            adapter.addAll(recommendations);
            adapter.notifyDataSetChanged();
            //swipeContainer.setRefreshing(false);
            rlLoading.setVisibility(View.GONE);
        });
    }

    private void goSplashActivity() {
        Log.i(TAG, "Entered goSplashActivity");
        Intent i = new Intent(getContext(), SplashScreenActivity.class);
        startActivity(i);
        getActivity().finish();
    }

    private int getFollowers() {
        ParseQuery<Followers> query = ParseQuery.getQuery("Followers");
        query.whereEqualTo(Followers.KEY_FOLLOWING, currentUser);

        //Fetches count synchronously,this will block the main thread
        try {
            int count  =  query.count();
            return count;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getFollowing() {
        ParseQuery<Followers> query = ParseQuery.getQuery("Followers");
        query.whereEqualTo(Followers.KEY_FOLLOWER, currentUser);

        //Fetches count synchronously
        try {
            int count  =  query.count();
            return count;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}