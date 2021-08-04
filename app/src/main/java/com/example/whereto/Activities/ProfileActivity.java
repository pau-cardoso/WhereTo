package com.example.whereto.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.whereto.Adapters.RecommendationAdapter;
import com.example.whereto.Models.Recommendation;
import com.example.whereto.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    public static final String TAG = "DetailRecommendationActivity";
    public static final String KEY_PROFILE_PICTURE = "profilePicture";

    ParseUser user;
    Toolbar tbProfile;
    ImageView ivProfilePic;
    TextView tvName;
    RecyclerView rvProfile;
    RecommendationAdapter adapter;
    List<Recommendation> ownRecommendations;
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = Parcels.unwrap(getIntent().getParcelableExtra("user"));

        /* Find components from view */
        ivProfilePic = findViewById(R.id.ivProfilePic);
        tvName = findViewById(R.id.tvNameP);
        rvProfile = findViewById(R.id.rvProfile);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);

        /* Toolbar setup for Action Bar */
        tbProfile = findViewById(R.id.tbUsrProfile); // Finds the toolbar component
        setSupportActionBar(tbProfile); // Sets the toolbar as action bar
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Disables the showing of the title
        getSupportActionBar().setTitle(user.getUsername()); // Changes action bar's title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* Setting the text views and image */
        tvName.setText(user.getString("name"));
        Glide.with(this).load(user.getParseFile(KEY_PROFILE_PICTURE).getUrl()).circleCrop().into(ivProfilePic);

        // initialize the array that will hold posts and create a PostsAdapter
        ownRecommendations = new ArrayList<>();
        adapter = new RecommendationAdapter(this, ownRecommendations);
        // set the adapter on the recycler view
        rvProfile.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvProfile.setLayoutManager(new LinearLayoutManager(this));
        // query recommendations of current user from Parse
        queryUserRecommendations();
    }

    // this event will enable the back
    // function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void queryUserRecommendations() {
        // specify what type of data we want to query - Recommendation.class
        ParseQuery<Recommendation> query = ParseQuery.getQuery(Recommendation.class);
        // include data referred by user key
        query.include(Recommendation.KEY_USER);
        // Finds recommendations posted by current user
        query.whereEqualTo(Recommendation.KEY_USER, user);
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

            // for debugging purposes let's print every post description to logcat
            for (Recommendation recommendation : recommendations) {
                Log.i(TAG, "Place: " + recommendation.getPlace() + ", username: " + recommendation.getUser().getUsername());
            }

            // save received posts to list and notify adapter of new data
            ownRecommendations.addAll(recommendations);
            // TODO refresh
            adapter.clear();
            adapter.addAll(recommendations);
            adapter.notifyDataSetChanged();
            //swipeContainer.setRefreshing(false);
        });
    }
}