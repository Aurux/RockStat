package com.aurux.rockstat.ui.stats;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.aurux.rockstat.data.dao.ClimbLogEntryDao;
import com.aurux.rockstat.data.database.AppDatabase;
import com.aurux.rockstat.data.models.ClimbLogEntry;

import java.util.List;

public class ClimbRepository {
    private ClimbLogEntryDao climbLogEntryDao;
    private LiveData<List<ClimbLogEntry>> boulderingClimbs;
    private LiveData<List<ClimbLogEntry>> sportClimbs;
    private LiveData<List<ClimbLogEntry>> tradClimbs;

    public ClimbRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        climbLogEntryDao = db.climbLogEntryDao();
        boulderingClimbs = climbLogEntryDao.getBoulderingClimbs();
        sportClimbs = climbLogEntryDao.getSportClimbs();
        tradClimbs = climbLogEntryDao.getTradClimbs();
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
