package com.aurux.rockstat.data.dao;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.lifecycle.LiveData;
import java.util.List;

import com.aurux.rockstat.data.models.ClimbLogEntry;

@Dao
public interface ClimbLogEntryDao {

    @Insert
    long insert(ClimbLogEntry climbLogEntry);

    @Query("SELECT * FROM climb_log_entries")
    LiveData<List<ClimbLogEntry>> getAllClimbLogEntries();

    @Delete
    void delete(ClimbLogEntry climbLogEntry);

    @Query("SELECT * FROM climb_log_entries WHERE climbing_type = 'Bouldering'")
    LiveData<List<ClimbLogEntry>> getBoulderingClimbs();

    @Query("SELECT * FROM climb_log_entries WHERE climbing_type = 'Sport'")
    LiveData<List<ClimbLogEntry>> getSportClimbs();

    @Query("SELECT * FROM climb_log_entries WHERE climbing_type = 'Trad'")
    LiveData<List<ClimbLogEntry>> getTradClimbs();
}
