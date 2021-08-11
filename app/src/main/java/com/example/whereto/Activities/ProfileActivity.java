package com.example.whereto.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whereto.Adapters.RecommendationAdapter;
import com.example.whereto.Models.Followers;
import com.example.whereto.Models.LikeRecommendation;
import com.example.whereto.Models.Recommendation;
import com.example.whereto.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    public static final String TAG = "ProfileActivity";
    public static final String KEY_PROFILE_PICTURE = "profilePicture";
    private static final String KEY_FOLLOWING = "following";
    private static final String KEY_FOLLOWERS = "followers";

    ParseUser user;
    Toolbar tbProfile;
    ImageView ivProfilePic;
    TextView tvName;
    TextView tvUsername;
    Button btnFollow;
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
        tvUsername = findViewById(R.id.tvUsernameP);
        btnFollow = findViewById(R.id.btnFollow);
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
        tvUsername.setText(user.getUsername());
        Glide.with(this).load(user.getParseFile(KEY_PROFILE_PICTURE).getUrl()).circleCrop().into(ivProfilePic);

        if (userFollows()) {
            btnFollow.setActivated(true);
            btnFollow.setText("Unfollow");
            btnFollow.setTextColor(Color.BLACK);
        } else {
            btnFollow.setActivated(false);
        }

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userFollows()) {
                    btnFollow.setActivated(false);
                    unfollow(user);
                } else {
                    btnFollow.setActivated(true);
                    btnFollow.setText("Unfollow");
                    btnFollow.setTextColor(Color.BLACK);
                    follow(user);
                }
            }
        });

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

    private void follow(ParseUser usr) {
        Followers followers = new Followers();
        followers.setFollower(ParseUser.getCurrentUser());
        followers.setFollowing(usr);

        // Saves the new object.
        // Notice that the SaveCallback is totally optional!
        followers.saveInBackground(e -> {
            if (e==null){
                //Save was done
                Log.d(TAG, "Follower saved");
                updateFollowerCount(usr, 1);
                updateFollowingCount(ParseUser.getCurrentUser(), 1);
            }else{
                //Something went wrong
                Log.d(TAG, "Something went wrong: " + e.getMessage());
                Toast.makeText(this.getApplicationContext(), "Something went wrong. Try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unfollow(ParseUser usr) {
        Log.i(TAG, "dislike: entered dislike method");

        Followers followers = followerFollowing(usr);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Followers");

        // Retrieve the object by id
        query.getInBackground(followers.getObjectId(), (object, e) -> {
            if (e == null) {
                //Object was fetched
                //Deletes the fetched ParseObject from the database
                object.deleteInBackground(e2 -> {
                    if(e2==null){
                        Log.d(TAG, "Follower/following delete successful");
                        updateFollowerCount(usr, -1);
                        updateFollowingCount(ParseUser.getCurrentUser(), -1);
                    }else{
                        //Something went wrong while deleting the Object
                        Log.d(TAG, "Follower/following delete unsuccessful: " + e.getMessage());
                        Toast.makeText(this.getApplicationContext(), "Error: "+e2.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                //Something went wrong
                Log.d(TAG, "Error: " + e.getMessage());
                Toast.makeText(this.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateFollowingCount(ParseUser currentUser, int count) {
        currentUser.put(KEY_FOLLOWING, count);
        ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");

        // Retrieve the object by id
        query.getInBackground(currentUser.getObjectId(), (usr, e) -> {
            if (e == null) {
                // Update the fields we want to modify
                int following = usr.getInt(KEY_FOLLOWING);
                usr.put(KEY_FOLLOWING, following + count);
                // All other fields will remain the same
                usr.saveInBackground();
            } else {
                // something went wrong
                Toast.makeText(this.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error updating the follower count: " + e.getMessage());
            }
        });
    }

    private void updateFollowerCount(ParseUser userFollowed, int count) {
        ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");

        // Retrieve the object by id
        query.getInBackground(userFollowed.getObjectId(), (usr, e) -> {
            if (e == null) {
                // Update the fields we want to modify
                int follower = usr.getInt(KEY_FOLLOWERS);
                Log.d(TAG, "Follower count: " + follower + " ....adding: " + count);
                usr.put(KEY_FOLLOWERS, follower + count);
                // All other fields will remain the same
                usr.saveInBackground();
            } else {
                // something went wrong
                Toast.makeText(this.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error updating the follower count: " + e.getMessage());
            }
        });
    }

    private boolean userFollows() {
        // specify what type of data we want to query - Recommendation.class
        ParseQuery<Followers> query = ParseQuery.getQuery("Followers");
        // include data referred by user key
        query.include(Recommendation.KEY_USER);
        query.whereEqualTo(Followers.KEY_FOLLOWER, ParseUser.getCurrentUser());
        query.whereEqualTo(Followers.KEY_FOLLOWING, user);

        // Fetches data synchronously
        try {
            List<Followers> results = query.find();
            if (results.size() > 0) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            Toast.makeText(this.getApplicationContext(), "Something went wrong. Try again.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return false;
    }

    public Followers followerFollowing(ParseUser user) {
        // specify what type of data we want to query - Recommendation.class
        ParseQuery<Followers> query = ParseQuery.getQuery("Followers");
        // include data referred by user key
        query.include(Recommendation.KEY_USER);
        query.whereEqualTo(Followers.KEY_FOLLOWER, ParseUser.getCurrentUser());
        query.whereEqualTo(Followers.KEY_FOLLOWING, user);

        // Fetches data synchronously
        try {
            List<Followers> results = query.find();
            if (results.size() > 0) {
                return results.get(0);
            } else {
                return null;
            }
        } catch (ParseException e) {
            Toast.makeText(this.getApplicationContext(), "Something went wrong. Try again.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return null;
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