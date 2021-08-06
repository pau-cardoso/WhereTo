package com.example.whereto.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.example.whereto.Models.Recommendation;
import com.example.whereto.R;
import com.example.whereto.Adapters.RecommendationAdapter;
import com.nabilmh.lottieswiperefreshlayout.LottieSwipeRefreshLayout;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class EatFragment extends Fragment {

    private static final String TAG = "EatFragment";

    RecommendationAdapter adapter;
    List<Recommendation> eatRecommendations;
    RecyclerView rvEat;
    LottieSwipeRefreshLayout eatSwipeContainer;
    RelativeLayout rlLoading;

    public EatFragment() {
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_eat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvEat = view.findViewById(R.id.rvEat);
        eatSwipeContainer = view.findViewById(R.id.eatSwipeContainer);
        rlLoading = view.findViewById(R.id.rlLoading);

        // initialize the array that will hold posts and create a PostsAdapter
        eatRecommendations = new ArrayList<>();
        adapter = new RecommendationAdapter(getContext(), eatRecommendations);
        // set the adapter on the recycler view
        rvEat.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvEat.setLayoutManager(new LinearLayoutManager(getContext()));
        // query recommendations from Parse
        queryEatRecommendations();

        eatSwipeContainer.setOnRefreshListener(() -> {
            Log.d(TAG, "refreshing");
            queryEatRecommendations();
            return null;
        });
    }

    protected void queryEatRecommendations() {
        // specify what type of data we want to query - Recommendation.class
        ParseQuery<Recommendation> query = ParseQuery.getQuery(Recommendation.class);
        // include data referred by user key
        query.include(Recommendation.KEY_USER);
        // Finds objects whose eating category is equal to "true"
        query.whereEqualTo(Recommendation.KEY_EAT, true);
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
            eatRecommendations.addAll(recommendations);
            adapter.clear();
            adapter.addAll(recommendations);
            adapter.notifyDataSetChanged();
            eatSwipeContainer.setRefreshing(false);
            rlLoading.setVisibility(View.GONE);
        });
    }
}
