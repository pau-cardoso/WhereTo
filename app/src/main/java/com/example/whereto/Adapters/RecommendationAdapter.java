package com.example.whereto.Adapters;

import android.content.Context;
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
import com.example.whereto.Models.Recommendation;
import com.example.whereto.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

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

            int likeCount = countLikes(recommendation);
            tvLikeCount.setText(String.valueOf(likeCount));

            if (likeCount > 0) {
                Log.d(TAG, "There are " + likeCount + " likes");
                ibtnLike.setActivated(true);
            }

            ibtnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (likeCount > 0) { // If user liked the post before, it dislikes it now
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
        }

        private void dislike(Recommendation recommendation) throws ParseException {
            Log.i(TAG, "dislike: entered dislike method");
            // Retrieve the object by id
            ParseQuery<ParseObject> query = ParseQuery.getQuery("LikeRecommendation");
            query.whereEqualTo(KEY_RECOMMENDATION, ParseObject.createWithoutData("Recommendation", recommendation.getObjectId()));
            query.whereEqualTo(KEY_USER, recommendation.getUser());
            // TODO Fix disliking
            query.findInBackground((objects, e) -> {
                if(e == null){
                    for (ParseObject result : objects) {
                        Log.d("Object found ",result.getObjectId());
                        result.deleteInBackground(e2 -> {
                            if(e2==null){
                                Log.d(TAG, "Delete successful");
                                //Toast.makeText(this, "Delete Successful", Toast.LENGTH_SHORT).show();
                            }else{
                                //Something went wrong while deleting the Object
                                Log.d(TAG, "Delete unsuccessful: " + e.getMessage());
                                //Toast.makeText(this, "Error: "+e2.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else{
                    Toast.makeText(context.getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void createLike(Recommendation recommendation) {
            ParseObject entity = new ParseObject("LikeRecommendation");

            entity.put("recommendationId", ParseObject.createWithoutData("Recommendation", recommendation.getObjectId()));
            entity.put("userId", ParseUser.getCurrentUser());

            // Saves the new object.
            // Notice that the SaveCallback is totally optional!
            entity.saveInBackground(e -> {
                if (e==null){
                    //Save was done
                    Log.d(TAG, "Liked saved");
                }else{
                    //Something went wrong
                    Log.d(TAG, "Something went wrong: " + e.getMessage());
                    Toast.makeText(context.getApplicationContext(), "Something went wrong. Try again later.", Toast.LENGTH_SHORT).show();
                }
            });
        }
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
}
