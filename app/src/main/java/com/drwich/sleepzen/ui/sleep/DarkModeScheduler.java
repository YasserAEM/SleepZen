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

public class DarkModeScheduler {
    private static final String TAG = "DarkModeScheduler";
    private static final int REQUEST_CODE = 54321;

    private static PendingIntent getPendingIntent(@NonNull Context ctx) {
        Intent i = new Intent(ctx, DarkModeReceiver.class);
        return PendingIntent.getBroadcast(
                ctx,
                REQUEST_CODE,
                i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    /**
     * Schedule the next exact alarm at the next occurrence of hour:minute.
     * On Android S+ this checks canScheduleExactAlarms() and otherwise
     * directs the user to grant the exact-alarm permission.
     */
    public static void scheduleNext(@NonNull Context ctx, int hour, int minute) {
        AlarmManager am = ctx.getSystemService(AlarmManager.class);
        PendingIntent pi = getPendingIntent(ctx);

        // Compute the trigger time
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        if (cal.getTimeInMillis() <= System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        long triggerMillis = cal.getTimeInMillis();

        // On Android S+ we must check canScheduleExactAlarms()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!am.canScheduleExactAlarms()) {
                Log.w(TAG, "Exact-alarm permission not granted; prompting user.");
                Intent i = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
                return;
            }
        }

        // Finally schedule it, catching any SecurityException
        try {
            am.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pi
            );
            Log.d(TAG, "Scheduled dark-mode alarm at: " + cal.getTime());
        } catch (SecurityException e) {
            Log.e(TAG, "Failed to set exact alarm", e);
            // Optionally prompt user here as well
        }
    }
}
