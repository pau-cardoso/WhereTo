package com.example.whereto.Adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.whereto.Fragments.EatFragment;
import com.example.whereto.Fragments.StayFragment;
import com.example.whereto.Fragments.VisitFragment;

import org.jetbrains.annotations.NotNull;

// Fragment Adapter for tabs in Recommendations, filtered by the 3 main categories (eat, stay and visit)
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public static final int count = 3;

    public ViewPagerAdapter(@NonNull @NotNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1 :
                return new StayFragment();
            case 2 :
                return new VisitFragment();
        }
        return new EatFragment();
    }

    @Override
    public int getCount() {
        return count;
    }
}
