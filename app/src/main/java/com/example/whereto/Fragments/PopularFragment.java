package com.example.whereto.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.whereto.Adapters.RecommendationAdapter;
import com.example.whereto.Models.Recommendation;
import com.example.whereto.R;
import com.nabilmh.lottieswiperefreshlayout.LottieSwipeRefreshLayout;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PopularFragment extends Fragment {

    private static final String TAG = "PopularFragment";

    RecommendationAdapter adapter;
    List<Recommendation> popularRecommendations;
    RecyclerView rvPopular;
    LottieSwipeRefreshLayout popularSwipeContainer;
    RelativeLayout rlLoading;

    public PopularFragment() {
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_popular, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPopular = view.findViewById(R.id.rvPopular);
        popularSwipeContainer = view.findViewById(R.id.popularSwipeContainer);
        rlLoading = view.findViewById(R.id.rlLoading);

        // initialize the array that will hold posts and create a PostsAdapter
        popularRecommendations = new ArrayList<>();
        adapter = new RecommendationAdapter(getContext(), popularRecommendations);
        // set the adapter on the recycler view
        rvPopular.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvPopular.setLayoutManager(new LinearLayoutManager(getContext()));
        // query recommendations from Parse
        queryPopularRecommendations();

        popularSwipeContainer.setOnRefreshListener(() -> {
            Log.d(TAG, "Refreshing...");
            queryPopularRecommendations();
            return null;
        });
    }

    protected void queryPopularRecommendations() {
        // specify what type of data we want to query - Recommendation.class
        ParseQuery<Recommendation> query = ParseQuery.getQuery("Recommendation");
        // include data referred by user key
        query.include(Recommendation.KEY_USER);
        // limit query to latest 20 items
        query.setLimit(20);
        // order posts by creation date (newest first)
        query.orderByDescending(Recommendation.KEY_LIKE_COUNT);
        // start an asynchronous call for posts
        query.findInBackground((recommendations, e) -> {
            // check for errors
            if (e != null) {
                Log.e(TAG, "Issue with getting recommendations", e);
                return;
            }

            // for debugging purposes let's print every post description to logcat
            for (Recommendation recommendation : recommendations) {
                //Log.i(TAG, "Place: " + recommendation.getPlace() + ", username: " + recommendation.getUser().getUsername());
            }

            // save received posts to list and notify adapter of new data
            popularRecommendations.addAll(recommendations);
            adapter.clear();
            adapter.addAll(recommendations);
            adapter.notifyDataSetChanged();
            popularSwipeContainer.setRefreshing(false);
            rlLoading.setVisibility(View.GONE);
        });
    }
}