package com.aurux.rockstat.ui.stats;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aurux.rockstat.data.dao.ClimbLogEntryDao;
import com.aurux.rockstat.data.models.ClimbLogEntry;

import java.util.List;

public class StatsViewModel extends ViewModel {
    private LiveData<List<ClimbLogEntry>> boulderingClimbs;
    private LiveData<List<ClimbLogEntry>> sportClimbs;
    private LiveData<List<ClimbLogEntry>> tradClimbs;
    private ClimbLogEntryDao climbLogEntryDao;

    public StatsViewModel(ClimbLogEntryDao climbLogEntryDao) {
        this.climbLogEntryDao = climbLogEntryDao;
        boulderingClimbs = climbLogEntryDao.getBoulderingClimbs();
        sportClimbs = climbLogEntryDao.getSportClimbs();
        tradClimbs = climbLogEntryDao.getTradClimbs();
    }

    public StatsViewModel() {
        // Required empty public constructor
    }



    public LiveData<List<ClimbLogEntry>> getBoulderingClimbs() {
        return boulderingClimbs;
    }

    public LiveData<List<ClimbLogEntry>> getSportClimbs() {
        return sportClimbs;
    }

    public LiveData<List<ClimbLogEntry>> getTradClimbs() {
        return tradClimbs;
    }
}
