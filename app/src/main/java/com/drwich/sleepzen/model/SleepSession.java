// app/src/main/java/com/drwich/sleepzen/data/local/SleepSession.java
package com.drwich.sleepzen.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sleep_sessions")
public class SleepSession {
    @PrimaryKey(autoGenerate = true)
    public long id;

    // Epoch millis when the session ended (or use startTime if you prefer)
    public long dateMillis;

    /** 0 = poor, 1 = okay, 2 = good */
    public int quality;

    public SleepSession(long dateMillis, int quality) {
        this.dateMillis = dateMillis;
        this.quality    = quality;
    }
}
