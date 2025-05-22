package com.drwich.sleepzen.ui.sleep;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.drwich.sleepzen.R;

public class SleepAlarmReceiver extends BroadcastReceiver {
    public static final String ACTION_CHECK_PHASE     = "com.drwich.sleepzen.CHECK_PHASE";
    public static final String ACTION_END_SESSION     = "com.drwich.sleepzen.END_SESSION";
    public static final String EXTRA_WAKEUP_TIME      = "com.drwich.sleepzen.EXTRA_WAKEUP_TIME";
    public static final String ACTION_END_SLEEP       = "com.drwich.sleepzen.ACTION_END_SLEEP";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;

        if (ACTION_END_SESSION.equals(action)) {
            // 1) Prepare full-screen intent for AlarmActivity
            Intent fullScreenIntent = new Intent(context, AlarmActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent fullScreenPi = PendingIntent.getActivity(
                    context,
                    0,
                    fullScreenIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            // 2) Create or update notification channel
            String channelId = "sleepzen_alarm_channel";
            NotificationManager nm =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel chan = new NotificationChannel(
                        channelId,
                        "SleepZen Alarm",
                        NotificationManager.IMPORTANCE_HIGH
                );
                chan.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                nm.createNotificationChannel(chan);
            }

            // 3) Build and fire the notification
            NotificationCompat.Builder nb = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.alarm_24)
                    .setContentTitle("Sleep session ended")
                    .setContentText("Tap to stop or snooze")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setFullScreenIntent(fullScreenPi, true)
                    .setContentIntent(fullScreenPi)
                    .setAutoCancel(true);

            nm.notify(1001, nb.build());

            // 4) Also send local broadcast so SleepFragment updates its UI
            sendEndBroadcast(context);
        }
        else if (ACTION_CHECK_PHASE.equals(action)) {
            // TODO: implement your phase‐check logic here,
            //       and if still not in light sleep, reschedule another check.
        }
    }

    private void sendEndBroadcast(Context ctx) {
        Intent i = new Intent(ACTION_END_SLEEP);
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(i);
    }
}
