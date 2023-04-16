package com.aurux.rockstat.data.database;

import androidx.room.RoomDatabase;
import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;

import com.aurux.rockstat.data.models.ClimbLogEntry;
import com.aurux.rockstat.data.dao.ClimbLogEntryDao;

@Database(entities = {ClimbLogEntry.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DB_NAME = "climb_log_db";
    private static AppDatabase instance;

    public abstract ClimbLogEntryDao climbLogEntryDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
