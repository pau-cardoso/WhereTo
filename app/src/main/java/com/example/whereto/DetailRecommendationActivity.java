package com.example.whereto;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.whereto.Models.Recommendation;

public class DetailRecommendationActivity extends AppCompatActivity {

    public static final String TAG = "DetailRecommendationActivity";
    public static final String KEY_PROFILE_PICTURE = "profilePicture";

    ImageView ivProfileImage;
    ImageView ivPictureReview;
    TextView tvName;
    TextView tvUsername;
    TextView tvPlace;
    TextView tvReview;
    TextView tvCreatedAt;
    RatingBar rbStars;
    RatingBar rbPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_recommendation);

        ivProfileImage = findViewById(R.id.ivProfileImage);
        ivPictureReview = findViewById(R.id.ivPictureReview);
        tvName = findViewById(R.id.tvNameP);
        tvUsername = findViewById(R.id.tvUsername);
        tvPlace = findViewById(R.id.tvPlace);
        tvReview = findViewById(R.id.tvReview);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        rbStars = findViewById(R.id.rbStarsR);
        rbPrice = findViewById(R.id.rbPrice);

        // Getting the recommendation object information clicked
        Recommendation recommendation = getIntent().getParcelableExtra("recommendation");

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
        Glide.with(this).load(recommendation.getUser().getParseFile(KEY_PROFILE_PICTURE).getUrl()).circleCrop().into(ivProfileImage);
        Glide.with(this).load(recommendation.getPicture().getUrl()).into(ivPictureReview);
    }
}