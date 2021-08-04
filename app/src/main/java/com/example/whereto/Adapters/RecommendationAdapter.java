package com.example.whereto.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whereto.Activities.ProfileActivity;
import com.example.whereto.Fragments.ProfileFragment;
import com.example.whereto.Models.LikeRecommendation;
import com.example.whereto.Models.Recommendation;
import com.example.whereto.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.ViewHolder> {

    public static final String TAG = "RecommendationAdapter";
    public static final String KEY_PROFILE_PICTURE = "profilePicture";
    private static final String KEY_RECOMMENDATION = "recommendationId";
    private static final String KEY_USER = "userId";

    private Context context;
    private List<Recommendation> recommendations;

    // Pass in the context and list of tweets
    public RecommendationAdapter(Context context, List<Recommendation> recommendations) {
        this.context = context;
        this.recommendations = recommendations;
    }

    // Inflating a layout from XML and returning the holder
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recommendation, parent, false);
        return new ViewHolder(view);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        // Get the data at the position
        Recommendation recommendation = recommendations.get(position);
        // Bind the tweet with view holder
        holder.bind(recommendation);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return recommendations.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        recommendations.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Recommendation> list) {
        recommendations.addAll(list);
        notifyDataSetChanged();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfileImage;
        ImageView ivPictureReview;
        TextView tvName;
        TextView tvUsername;
        TextView tvPlace;
        TextView tvReview;
        TextView tvCreatedAt;
        RatingBar rbStars;
        RatingBar rbPrice;
        ImageButton ibtnLike;
        TextView tvLikeCount;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            ivPictureReview = itemView.findViewById(R.id.ivPictureReview);
            tvName = itemView.findViewById(R.id.tvNameP);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvPlace = itemView.findViewById(R.id.tvPlace);
            tvReview = itemView.findViewById(R.id.tvReview);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            rbStars = itemView.findViewById(R.id.rbStarsR);
            rbPrice = itemView.findViewById(R.id.rbPrice);
            ibtnLike = itemView.findViewById(R.id.ibtnLike);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
        }

        public void bind(Recommendation recommendation) {
            // Setting the text views
            tvName.setText(recommendation.getUser().getString("name"));
            tvUsername.setText(recommendation.getUser().getUsername());
            tvPlace.setText(recommendation.getPlace());
            tvReview.setText(recommendation.getReview());
            tvCreatedAt.setText(recommendation.calculateTimeAgo(recommendation.getCreatedAt()));

            // Rating bars (Star and price)
            rbStars.setRating(recommendation.getRate());
            rbPrice.setRating(recommendation.getPriceRate());

            // Loading the images into de view
            Glide.with(context).load(recommendation.getUser().getParseFile(KEY_PROFILE_PICTURE).getUrl()).circleCrop().into(ivProfileImage); // TODO Check if doing a User model
            Glide.with(context).load(recommendation.getPicture().getUrl()).into(ivPictureReview);

            // Getting number of likes
            int likeCount = countLikes(recommendation);
            tvLikeCount.setText(String.valueOf(likeCount));

            // Setting like button activated depending on post
            if (userLiked(recommendation)) {
                ibtnLike.setActivated(true);
            } else {
                ibtnLike.setActivated(false);
            }

            // When user clicks on like button
            ibtnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userLiked(recommendation)) {
                        ibtnLike.setActivated(false);
                        tvLikeCount.setText(String.valueOf(likeCount-1));
                        try {
                            dislike(recommendation);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ibtnLike.setActivated(true);
                        tvLikeCount.setText(String.valueOf(likeCount+1));
                        createLike(recommendation);
                    }
                }
            });

            // When user clicks on profile picture
            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseUser user = recommendation.getUser();
                    Intent i = new Intent(context, ProfileActivity.class);
                    i.putExtra("user", Parcels.wrap(user)); // serialize the user using parceler, use its short name as a key
                    context.startActivity(i); // show the activity
                }
            });
        }
    }

    private void dislike(Recommendation recommendation) throws ParseException {
        Log.i(TAG, "dislike: entered dislike method");
        LikeRecommendation likeRecommendation = userLike(recommendation);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("LikeRecommendation");
        // Retrieve the object by id
        query.getInBackground(likeRecommendation.getObjectId(), (object, e) -> {
            if (e == null) {
                //Object was fetched
                //Deletes the fetched ParseObject from the database
                object.deleteInBackground(e2 -> {
                    if(e2==null){
                        Log.d(TAG, "Delete successful");
                        Toast.makeText(context.getApplicationContext(), "Delete Successful", Toast.LENGTH_SHORT).show();
                        updateLikeCount(recommendation, -1);
                    }else{
                        //Something went wrong while deleting the Object
                        Log.d(TAG, "Delete unsuccessful: " + e.getMessage());
                        Toast.makeText(context.getApplicationContext(), "Error: "+e2.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                //Something went wrong
                Log.d(TAG, "Error: " + e.getMessage());
                Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createLike(Recommendation recommendation) {

        LikeRecommendation likeRecommendation = new LikeRecommendation();
        likeRecommendation.setUser(ParseUser.getCurrentUser());
        likeRecommendation.setRecommendation(recommendation);

        // Saves the new object.
        // Notice that the SaveCallback is totally optional!
        likeRecommendation.saveInBackground(e -> {
            if (e==null){
                //Save was done
                Log.d(TAG, "Liked saved");
                updateLikeCount(recommendation, 1);
            }else{
                //Something went wrong
                Log.d(TAG, "Something went wrong: " + e.getMessage());
                Toast.makeText(context.getApplicationContext(), "Something went wrong. Try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateLikeCount(Recommendation recommendation, int like) {
        ParseQuery<Recommendation> query = ParseQuery.getQuery("Recommendation");

        // Retrieve the object by id
        query.getInBackground(recommendation.getObjectId(), (recomm, e) -> {
            if (e == null) {
                // Update the fields we want to
                //object.put("myCustomKey2Name", 999);
                recomm.setLikeCount(recommendation.getLikeCount() + like);
                // All other fields will remain the same
                recomm.saveInBackground();
            } else {
                // something went wrong
                Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error updating the like: " + e.getMessage());
            }
        });
    }

    public int countLikes(Recommendation recommendation) {
        // specify what type of data we want to query - Recommendation.class
        ParseQuery<ParseObject> query = ParseQuery.getQuery("LikeRecommendation");
        //ParseQuery<LikeRecommendation> query = ParseQuery.getQuery(LikeRecommendation.class);
        // include data referred by user key
        query.whereEqualTo(KEY_RECOMMENDATION, ParseObject.createWithoutData("Recommendation", recommendation.getObjectId()));
        // Count likes
        try {
            int count = query.count();
            return count;
        } catch (ParseException e) {
            Log.d(TAG, "countLikes: something went wrong: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    public boolean userLiked(Recommendation recommendation) {
        // specify what type of data we want to query - Recommendation.class
        ParseQuery<LikeRecommendation> query = ParseQuery.getQuery("LikeRecommendation");
        // include data referred by user key
        query.whereEqualTo(KEY_RECOMMENDATION, ParseObject.createWithoutData("Recommendation", recommendation.getObjectId()));
        query.whereEqualTo(KEY_USER, ParseUser.getCurrentUser());

        // Fetches data synchronously
        try {
            List<LikeRecommendation> results = query.find();
            if (results.size() > 0) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            Toast.makeText(context.getApplicationContext(), "Something went wrong. Try again.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return false;
    }

    public LikeRecommendation userLike(Recommendation recommendation) {
        // specify what type of data we want to query - Recommendation.class
        ParseQuery<LikeRecommendation> query = ParseQuery.getQuery("LikeRecommendation");
        // include data referred by user key
        query.whereEqualTo(KEY_RECOMMENDATION, ParseObject.createWithoutData("Recommendation", recommendation.getObjectId()));
        query.whereEqualTo(KEY_USER, ParseUser.getCurrentUser());

        // Fetches data synchronously
        try {
            List<LikeRecommendation> results = query.find();
            if (results.size() > 0) {
                return results.get(0);
            } else {
                return null;
            }
        } catch (ParseException e) {
            Toast.makeText(context.getApplicationContext(), "Something went wrong. Try again.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return null;
    }
}
