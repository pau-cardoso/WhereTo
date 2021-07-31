package com.example.whereto.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whereto.Models.Recommendation;
import com.example.whereto.R;
import com.example.whereto.Adapters.RecommendationAdapter;
import com.nabilmh.lottieswiperefreshlayout.LottieSwipeRefreshLayout;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class VisitFragment extends Fragment {

    private static final String TAG = "VisitFragment";

    RecommendationAdapter adapter;
    List<Recommendation> visitRecommendations;
    RecyclerView rvVisit;
    LottieSwipeRefreshLayout visitSwipeContainer;

    public VisitFragment() {
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_visit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvVisit = view.findViewById(R.id.rvVisit);
        visitSwipeContainer = view.findViewById(R.id.visitSwipeContainer);

        // initialize the array that will hold posts and create a PostsAdapter
        visitRecommendations = new ArrayList<>();
        adapter = new RecommendationAdapter(getContext(), visitRecommendations);
        // set the adapter on the recycler view
        rvVisit.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvVisit.setLayoutManager(new LinearLayoutManager(getContext()));
        // query recommendations from Parse
        queryStayRecommendations();

        visitSwipeContainer.setOnRefreshListener(() -> {
            Log.d(TAG, "refreshing");
            queryStayRecommendations();
            return null;
        });
    }

    protected void queryStayRecommendations() {
        // specify what type of data we want to query - Recommendation.class
        ParseQuery<Recommendation> query = ParseQuery.getQuery(Recommendation.class);
        // include data referred by user key
        query.include(Recommendation.KEY_USER);
        // Finds objects whose eating category is equal to "true"
        query.whereEqualTo(Recommendation.KEY_VISIT, true);
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
            visitRecommendations.addAll(recommendations);
            // TODO refresh
            adapter.clear();
            adapter.addAll(recommendations);
            adapter.notifyDataSetChanged();
            visitSwipeContainer.setRefreshing(false);
        });
    }
}
