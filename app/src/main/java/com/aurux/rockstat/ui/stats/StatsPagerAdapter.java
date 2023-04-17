package com.aurux.rockstat.ui.stats;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class StatsPagerAdapter extends FragmentStateAdapter {

    public StatsPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new BoulderingStatsFragment();
            case 1:
                return new SportStatsFragment();
            case 2:
                return new TradStatsFragment();
            default:
                throw new IllegalStateException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
