package com.example.whereto.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whereto.Models.Recommendation;
import com.example.whereto.R;
import com.example.whereto.RecommendationAdapter;
import com.example.whereto.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
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

    RecommendationAdapter adapter;
    List<Recommendation> allRecommendations;
    TabLayout tlTabs;
    ViewPager2 viewPager;
    ViewPagerAdapter vpAdapter;

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

        // Find components from view
        tlTabs = view.findViewById(R.id.tlTabs);
        viewPager = view.findViewById(R.id.viewPager);

        // Setting the adapter for tab view by category
        FragmentManager fm = getActivity().getSupportFragmentManager();
        vpAdapter = new ViewPagerAdapter(fm, getLifecycle());
        viewPager.setAdapter(vpAdapter);

        tlTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tlTabs.selectTab(tlTabs.getTabAt(position));
            }
        });

    }
}