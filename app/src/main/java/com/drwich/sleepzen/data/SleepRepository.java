package com.drwich.sleepzen.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.drwich.sleepzen.model.SleepSession;

import java.util.List;

public class SleepRepository {
    private final SleepSessionDao dao;

    public SleepRepository(Application app) {
        AppDatabase db = AppDatabase.getInstance(app);
        dao = db.sleepSessionDao();
    }

    /**
     * For the quality‐map, observe all sessions in [start, end].
     */
    public LiveData<List<SleepSession>> getSessionsBetween(long start, long end) {
        return dao.getSessionsBetween(start, end);
    }

    /**
     * Insert (or replace) a SleepSession on a background thread.
     */
    public void insertSession(SleepSession session) {
        AppDatabase.databaseWriteExecutor.execute(() -> dao.insert(session));
    }
}
