package com.example.whereto.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.whereto.CreateActivity;
import com.example.whereto.Models.Recommendation;
import com.example.whereto.R;
import com.example.whereto.Adapters.RecommendationAdapter;
import com.example.whereto.Adapters.ViewPagerAdapter;
import com.example.whereto.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

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
    FloatingActionButton btnAdd;
    View circle;

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
        @NonNull ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());

        // Find components from view
        tlTabs = view.findViewById(R.id.tlTabs);
        viewPager = view.findViewById(R.id.viewPager);
        btnAdd = view.findViewById(R.id.btnAdd);
        circle = view.findViewById(R.id.circle);

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.circle_explosion_anim);
        animation.setDuration(700);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent i = new Intent(getContext(), CreateActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);
                binding.getRoot().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.purple_500));
                circle.setVisibility(View.INVISIBLE);
                btnAdd.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAdd.setVisibility(View.INVISIBLE);
                circle.setVisibility(View.VISIBLE);
                circle.startAnimation(animation);
            }
        });

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