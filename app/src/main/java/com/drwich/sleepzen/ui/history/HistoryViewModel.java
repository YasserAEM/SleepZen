package com.drwich.sleepzen.ui.history;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.drwich.sleepzen.data.AppDatabase;
import com.drwich.sleepzen.data.SleepSessionDao;
import com.drwich.sleepzen.data.SleepRepository;
import com.drwich.sleepzen.model.SleepSession;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class HistoryViewModel extends AndroidViewModel {
    private final SleepRepository repo;
    private final SleepSessionDao sessionDao;
    private final ExecutorService exec;

    /** Maps each CalendarDay to that day’s quality (for decorating the calendar) */
    private final MediatorLiveData<Map<CalendarDay, Integer>> qualityMap = new MediatorLiveData<>();
    public LiveData<Map<CalendarDay, Integer>> getSleepQualityMap() {
        return qualityMap;
    }

    /** Holds the session data for the day the user tapped */
    private final MutableLiveData<SleepSession> clickedSession = new MutableLiveData<>();
    public LiveData<SleepSession> getClickedSession() {
        return clickedSession;
    }

    public HistoryViewModel(@NonNull Application app) {
        super(app);
        repo = new SleepRepository(app);
        sessionDao = AppDatabase.getInstance(app).sleepSessionDao();
        exec = AppDatabase.databaseWriteExecutor;

        // Build this month’s quality map
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long start = cal.getTimeInMillis();

        cal.add(Calendar.MONTH, 1);
        long end = cal.getTimeInMillis() - 1;

        LiveData<List<SleepSession>> sessions =
                repo.getSessionsBetween(start, end);
        qualityMap.addSource(sessions, list -> {
            Map<CalendarDay, Integer> map = new HashMap<>();
            for (SleepSession s : list) {
                Calendar c2 = Calendar.getInstance();
                c2.setTimeInMillis(s.dateMillis);
                CalendarDay day = CalendarDay.from(
                        c2.get(Calendar.YEAR),
                        c2.get(Calendar.MONTH) + 1,
                        c2.get(Calendar.DAY_OF_MONTH)
                );
                map.put(day, s.quality);
            }
            qualityMap.setValue(map);
        });
    }

    /**
     * Loads the single SleepSession whose dateMillis falls between
     * start-of-day and end-of-day, and posts it to clickedSession().
     *
     * You need to have added this DAO method:
     *   @Query("SELECT * FROM sleep_sessions WHERE dateMillis BETWEEN :start AND :end LIMIT 1")
     *   SleepSession getSessionBetween(long start, long end);
     */
    public void loadSessionForDate(int year, int month, int day) {
        exec.execute(() -> {
            Calendar cal = Calendar.getInstance();
            // Calendar month is 0‐based
            cal.set(year, month - 1, day, 0, 0, 0);
            long start = cal.getTimeInMillis();
            cal.add(Calendar.DAY_OF_MONTH, 1);
            long end = cal.getTimeInMillis() - 1;

            SleepSession session = sessionDao.getSessionBetween(start, end);
            clickedSession.postValue(session);
        });
    }
}
