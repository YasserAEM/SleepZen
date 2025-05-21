package com.drwich.sleepzen.ui.sleep;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

/**
 * Fires at the user’s chosen time to:
 *  1) Activate Do Not Disturb
 *  2) Reschedule itself for tomorrow
 *  3) Post a “Dark Mode & DND Enabled” notification
 */
public class DarkModeReceiver extends BroadcastReceiver {
    private static final String TAG = "DarkModeReceiver";

    private static final String PREFS     = "sleep_prefs";
    private static final String KEY_HOUR  = "dark_hour";
    private static final String KEY_MIN   = "dark_min";
    private static final String CHANNEL_ID = "dark_mode_channel";
    private static final int    NOTIF_ID   = 1001;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Receiver fired; activating DND + notification");

        NotificationManager nm =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 1) Attempt to turn on Do Not Disturb
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!nm.isNotificationPolicyAccessGranted()) {
                // Prompt user to grant DND access
                Intent settings = new Intent(
                        Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(settings);
                Log.w(TAG, "No DND access; sending user to settings");
            } else {
                nm.setInterruptionFilter(
                        NotificationManager.INTERRUPTION_FILTER_NONE
                );
                Log.d(TAG, "Do Not Disturb enabled");
            }
        }

        // 2) Reschedule for tomorrow
        SharedPreferences prefs =
                context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        int h = prefs.getInt(KEY_HOUR, -1);
        int m = prefs.getInt(KEY_MIN,  -1);
        if (h >= 0 && m >= 0) {
            DarkModeScheduler.scheduleNext(context, h, m);
        }

        // 3) Ensure channel exists (O+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chan = new NotificationChannel(
                    CHANNEL_ID,
                    "Dark Mode & DND",
                    NotificationManager.IMPORTANCE_HIGH
            );
            chan.setDescription("Your app has entered Dark Mode and Do Not Disturb");
            nm.createNotificationChannel(chan);
        }

        // 4) Build intent to open system Display settings
        Intent disp = new Intent(Settings.ACTION_DISPLAY_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent piSettings = PendingIntent.getActivity(
                context, 0, disp,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 5) Build the notification
        NotificationCompat.Builder b = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_manage)
                .setContentTitle("Dark Mode & DND Enabled")
                .setContentText("Tap to open Display settings")
                .setContentIntent(piSettings)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER);

        // 6) Check POST_NOTIFICATIONS (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "No POST_NOTIFICATIONS; skipping notify");
                return;
            }
        }

        // 7) Check that notifications are enabled
        NotificationManagerCompat nmCompat = NotificationManagerCompat.from(context);
        if (!nmCompat.areNotificationsEnabled()) {
            Log.w(TAG, "Notifications disabled; skipping notify");
            return;
        }

        // 8) Post the notification
        nmCompat.notify(NOTIF_ID, b.build());
        Log.d(TAG, "Posted Dark Mode & DND notification");
    }
}
