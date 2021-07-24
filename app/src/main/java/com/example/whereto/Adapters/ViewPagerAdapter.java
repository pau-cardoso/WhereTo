package com.example.whereto.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.whereto.Fragments.EatFragment;
import com.example.whereto.Fragments.StayFragment;
import com.example.whereto.Fragments.VisitFragment;

import org.jetbrains.annotations.NotNull;

// Fragment Adapter for tabs in Recommendations, filtered by the 3 main categories (eat, stay and visit)
public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    // Creating the fragments depending on the tab
    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1 :
                return new StayFragment();
            case 2 :
                return new VisitFragment();
        }
        return new EatFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
