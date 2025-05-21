package com.drwich.sleepzen.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.drwich.sleepzen.data.HabitDao;
import com.drwich.sleepzen.data.SleepSessionDao;
import com.drwich.sleepzen.model.HabitEntity;
import com.drwich.sleepzen.model.SleepSession;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = { HabitEntity.class, SleepSession.class },
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract HabitDao habitDao();
    public abstract SleepSessionDao sleepSessionDao();

    // Thread-pool for database writes
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "app.db"
                            )
                            // Drop & recreate on version change
                            .fallbackToDestructiveMigration()
                            // Seed built-in habits on create & open
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    databaseWriteExecutor.execute(() -> {
                                        List<HabitEntity> defaults = Arrays.asList(
                                                new HabitEntity("drink_water", "Drink water", true),
                                                new HabitEntity("no_caffeine", "No caffeine", true)
                                        );
                                        getInstance(context).habitDao().insertAll(defaults);
                                    });
                                }

                                @Override
                                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                    super.onOpen(db);
                                    databaseWriteExecutor.execute(() -> {
                                        List<HabitEntity> defaults = Arrays.asList(
                                                new HabitEntity("drink_water", "Drink water", true),
                                                new HabitEntity("no_caffeine", "No caffeine", true)
                                        );
                                        getInstance(context).habitDao().insertAll(defaults);
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
