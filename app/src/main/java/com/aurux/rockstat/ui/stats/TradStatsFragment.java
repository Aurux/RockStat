package com.aurux.rockstat.ui.stats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.aurux.rockstat.R;
import com.aurux.rockstat.data.dao.ClimbLogEntryDao;
import com.aurux.rockstat.data.database.AppDatabase;
import com.aurux.rockstat.data.models.ClimbLogEntry;
import com.aurux.rockstat.databinding.FragmentTradStatsBinding;
import com.aurux.rockstat.utils.ChartUtils;

import java.util.List;

public class TradStatsFragment extends Fragment {

    private StatsViewModel statsViewModel;

    private FragmentTradStatsBinding binding;

    private TextView highestGrade;
    private TextView totalClimbs;
    public TradStatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        statsViewModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                AppDatabase db = AppDatabase.getInstance(requireContext());
                ClimbLogEntryDao climbLogEntryDao = db.climbLogEntryDao();
                return (T) new StatsViewModel(climbLogEntryDao);
            }
        }).get(StatsViewModel.class);

        binding = FragmentTradStatsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        highestGrade = root.findViewById(R.id.highest_grade_circle);
        totalClimbs = root.findViewById(R.id.total_climbs_circle);

        statsViewModel.getTradClimbs().observe(getViewLifecycleOwner(), new Observer<List<ClimbLogEntry>>() {
            @Override
            public void onChanged(List<ClimbLogEntry> climbs) {
                totalClimbs.setText(String.valueOf(climbs.size()));
                highestGrade.setText(ChartUtils.getHighestGrade(climbs, "Trad"));
                ChartUtils.updateClimbsDonePerWeekChart(requireContext(), binding.climbsPerWeekChart, climbs);
                ChartUtils.updateAvgAttemptsPerGradeChart(requireContext(), binding.avgAttemptsPerGradeChart, climbs, "Trad");
            }
        });
        return root;
    }
}