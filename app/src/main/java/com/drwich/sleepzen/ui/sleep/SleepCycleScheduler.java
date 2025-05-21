package com.drwich.sleepzen.ui.sleep;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Calendar;

public class SleepCycleScheduler {
    private static final String TAG         = "SleepCycleScheduler";
    private static final int    REQ_END     = 2001;
    private static final int    REQ_CHECK   = 2002;
    private static final long   WINDOW_MS   = 30 * 60 * 1000; // 30 minutes
    public  static final long   INTERVAL_MS = 60 * 1000;      // 1 minute

    private static PendingIntent endIntent(Context ctx) {
        Intent i = new Intent(ctx, SleepAlarmReceiver.class)
                .setAction(SleepAlarmReceiver.ACTION_END_SESSION);
        return PendingIntent.getBroadcast(
                ctx, REQ_END, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private static PendingIntent checkIntent(Context ctx, long wakeTime) {
        Intent i = new Intent(ctx, SleepAlarmReceiver.class)
                .setAction(SleepAlarmReceiver.ACTION_CHECK_PHASE)
                .putExtra(SleepAlarmReceiver.EXTRA_WAKEUP_TIME, wakeTime);
        return PendingIntent.getBroadcast(
                ctx, REQ_CHECK, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    /**
     * Schedule both:
     * 1) the first “phase‐check” sometime in the 30-minute window before wakeTimeMillis
     * 2) the final wake alarm exactly at wakeTimeMillis
     */
    public static void scheduleCycleAlarms(@NonNull Context ctx, long wakeTimeMillis) {
        AlarmManager am = ctx.getSystemService(AlarmManager.class);
        if (am == null) return;

        // On Android 12+ must have exact-alarm permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && !am.canScheduleExactAlarms()) {
            Log.w(TAG, "Exact-alarm permission missing; requesting user grant");
            Intent req = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(req);
            return;
        }

        long now = System.currentTimeMillis();
        long windowStart = wakeTimeMillis - WINDOW_MS;
        long firstCheck;

        if (now < windowStart) {
            // before the 30-min window → schedule at window start
            firstCheck = windowStart;
        } else if (wakeTimeMillis - now > INTERVAL_MS) {
            // inside window but more than 1 min before wake → schedule 1 min from now
            firstCheck = now + INTERVAL_MS;
        } else {
            // last minute of window → schedule immediately
            firstCheck = now;
        }

        // 1) Phase-check alarm
        try {
            am.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    firstCheck,
                    checkIntent(ctx, wakeTimeMillis)
            );
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(firstCheck);
            Log.d(TAG, "First phase-check @ " + c.getTime());
        } catch (SecurityException se) {
            Log.e(TAG, "Failed to schedule phase-check", se);
        }

        // 2) Final wake alarm
        try {
            am.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    wakeTimeMillis,
                    endIntent(ctx)
            );
            Calendar c2 = Calendar.getInstance();
            c2.setTimeInMillis(wakeTimeMillis);
            Log.d(TAG, "Final wake alarm @ " + c2.getTime());
        } catch (SecurityException se) {
            Log.e(TAG, "Failed to schedule final wake alarm", se);
        }
    }

    /**
     * If a phase-check didn’t detect light sleep, schedule the next one 1 minute later,
     * but only if still before the final wake time.
     */
    public static void scheduleNextCheck(@NonNull Context ctx, long wakeTimeMillis) {
        AlarmManager am = ctx.getSystemService(AlarmManager.class);
        if (am == null) return;

        long next = System.currentTimeMillis() + INTERVAL_MS;
        if (next >= wakeTimeMillis) {
            Log.d(TAG, "Next check would be after wake time; skipping");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && !am.canScheduleExactAlarms()) {
            Log.w(TAG, "Exact-alarm permission missing; requesting user grant");
            Intent req = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(req);
            return;
        }

        try {
            am.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    next,
                    checkIntent(ctx, wakeTimeMillis)
            );
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(next);
            Log.d(TAG, "Next phase-check @ " + c.getTime());
        } catch (SecurityException se) {
            Log.e(TAG, "Failed to schedule next phase-check", se);
        }
    }
}
