package com.drwich.sleepzen.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.drwich.sleepzen.model.HabitEntity;

import java.util.List;

@Dao
public interface HabitDao {
    @Query("SELECT * FROM habits")
    LiveData<List<HabitEntity>> getAllHabits();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<HabitEntity> habits);

    /** Insert a single habit (built-in or custom) */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertHabit(HabitEntity habit);

    @Update
    void updateHabit(HabitEntity habit);

    @Query("DELETE FROM habits WHERE id = :id")
    void deleteById(String id);

    @Query("UPDATE habits SET done = 0")
    void resetAll();
}
