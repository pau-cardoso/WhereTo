package com.example.whereto;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whereto.Models.Recommendation;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.List;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.ViewHolder> {

    public static final String TAG = "RecommendationAdapter";
    public static final String KEY_PROFILE_PICTURE = "profilePicture";

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

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            ivPictureReview = itemView.findViewById(R.id.ivPictureReview);
            tvName = itemView.findViewById(R.id.tvName);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvPlace = itemView.findViewById(R.id.tvPlace);
            tvReview = itemView.findViewById(R.id.tvReview);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            rbStars = itemView.findViewById(R.id.rbStarsR);
            rbPrice = itemView.findViewById(R.id.rbPrice);
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
        }
    }
}
