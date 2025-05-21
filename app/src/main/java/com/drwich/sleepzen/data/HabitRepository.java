package com.drwich.sleepzen.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.drwich.sleepzen.data.AppDatabase;
import com.drwich.sleepzen.data.HabitDao;
import com.drwich.sleepzen.model.HabitEntity;

import java.util.List;
import java.util.concurrent.Executors;

public class HabitRepository {
    private final HabitDao dao;

    public HabitRepository(Context context) {
        dao = AppDatabase.getInstance(context).habitDao();
    }

    public LiveData<List<HabitEntity>> getAllHabits() {
        return dao.getAllHabits();
    }

    /** Persist a brand-new habit */
    public void addHabit(HabitEntity habit) {
        Executors.newSingleThreadExecutor()
                .execute(() -> dao.insertHabit(habit));
    }

    /** Set the done state (called from the adapter) */
    public void setDone(HabitEntity habit, boolean done) {
        habit.setDone(done);
        Executors.newSingleThreadExecutor()
                .execute(() -> dao.updateHabit(habit));
    }

    public void delete(HabitEntity habit) {
        Executors.newSingleThreadExecutor()
                .execute(() -> dao.deleteById(habit.getId()));
    }

    public void resetAll() {
        Executors.newSingleThreadExecutor()
                .execute(dao::resetAll);
    }
}
