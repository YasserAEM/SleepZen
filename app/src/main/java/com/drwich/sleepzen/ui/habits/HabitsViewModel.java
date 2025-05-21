package com.drwich.sleepzen.ui.habits;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.drwich.sleepzen.model.HabitEntity;
import com.drwich.sleepzen.data.HabitRepository;
import com.drwich.sleepzen.model.HabitEntity;

import java.util.List;

public class HabitsViewModel extends AndroidViewModel {
    private final HabitRepository repo;
    public final LiveData<List<HabitEntity>> habits;

    public HabitsViewModel(@NonNull Application application) {
        super(application);
        repo = new HabitRepository(application);
        habits = repo.getAllHabits();
    }

    /** Add a new custom habit */
    public void addHabit(HabitEntity habit) {
        repo.addHabit(habit);
    }

    /** Called when a checkbox is toggled */
    public void setDone(HabitEntity habit, boolean done) {
        repo.setDone(habit, done);
    }

    /** Delete a habit */
    public void delete(HabitEntity habit) {
        repo.delete(habit);
    }
}
