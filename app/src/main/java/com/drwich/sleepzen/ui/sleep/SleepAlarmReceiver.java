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

        long wakeTime = intent.getLongExtra(EXTRA_WAKEUP_TIME, -1);

        if (ACTION_CHECK_PHASE.equals(action)) {
            // votre logique de check de phase…
            // si encore pas en phase légère, replanifier un nouveau CHECK_PHASE
        }
        else if (ACTION_END_SESSION.equals(action)) {
            // notification full-screen → AlarmActivity
            // … (code déjà en place) …

            // broadcast local vers SleepFragment
            sendEndBroadcast(context);
        }
    }

    private void sendEndBroadcast(Context ctx) {
        Intent i = new Intent(ACTION_END_SLEEP);
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(i);
    }
}
