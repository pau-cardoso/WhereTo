package com.example.whereto.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whereto.Models.Recommendation;
import com.example.whereto.R;
import com.example.whereto.RecommendationAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecommendationsFragment} factory method to
 * create an instance of this fragment.
 */
public class RecommendationsFragment extends Fragment {

    private static final String TAG = "RecommendationsFragment";

    RecyclerView rvRecommendation;
    RecommendationAdapter adapter;
    List<Recommendation> allRecommendations;

    public RecommendationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recommendations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvRecommendation = view.findViewById(R.id.rvRecommendations);

        // initialize the array that will hold posts and create a PostsAdapter
        allRecommendations = new ArrayList<>();
        adapter = new RecommendationAdapter(getContext(), allRecommendations);

        // set the adapter on the recycler view
        rvRecommendation.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvRecommendation.setLayoutManager(new LinearLayoutManager(getContext()));
        // query posts from Parse
        queryPosts();
    }

    protected void queryPosts() {
        // specify what type of data we want to query - Post.class
        ParseQuery<Recommendation> query = ParseQuery.getQuery(Recommendation.class);
        Log.i(TAG, String.valueOf(query));
        // include data referred by user key
        query.include(Recommendation.KEY_USER);
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
            allRecommendations.addAll(recommendations);
            // TODO refresh
            //adapter.clear();
            //adapter.addAll(recommendations);
            adapter.notifyDataSetChanged();
            //swipeContainer.setRefreshing(false);
        });
    }
}