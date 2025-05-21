package com.drwich.sleepzen.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.drwich.sleepzen.model.SleepSession;

import java.util.List;

@Dao
public interface SleepSessionDao {
    /** Insert or replace a SleepSession record */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SleepSession session);

    /**
     * Return all sessions whose wake‐up timestamp is between start and end.
     * Used to build the monthly quality map.
     */
    @Query("SELECT * FROM sleep_sessions " +
            "WHERE dateMillis BETWEEN :start AND :end " +
            "ORDER BY dateMillis ASC")
    LiveData<List<SleepSession>> getSessionsBetween(long start, long end);

    /**
     * Return the single SleepSession on that day, if any.
     * Used when the user taps a day.
     */
    @Query("SELECT * FROM sleep_sessions " +
            "WHERE dateMillis BETWEEN :start AND :end " +
            "LIMIT 1")
    SleepSession getSessionBetween(long start, long end);

    /** (Optional) Fetch all sessions in ascending order. */
    @Query("SELECT * FROM sleep_sessions ORDER BY dateMillis ASC")
    LiveData<List<SleepSession>> getAllSessions();
}
