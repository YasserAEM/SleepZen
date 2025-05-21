package com.drwich.sleepzen.ui.sleep;

import android.app.KeyguardManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.drwich.sleepzen.R;

public class AlarmActivity extends AppCompatActivity {
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1) Show on lock screen, turn screen on
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            );
        }

        // 2) Dismiss (or at least hide) the keyguard
        // Using window flag:
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        // Or, to truly request dismissal (API 26+):
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            if (km != null) {
                km.requestDismissKeyguard(this, null);
            }
        }

        setContentView(R.layout.activity_alarm);

        // 3) Start the alarm sound
        player = MediaPlayer.create(this, R.raw.alarm_sound);
        player.setLooping(true);
        player.start();

        // 4) Stop button
        Button btnStop = findViewById(R.id.btnStop);
        btnStop.setOnClickListener(v -> {
            if (player.isPlaying()) player.stop();
            finish();
        });

        // 5) Snooze button
        Button btnSnooze = findViewById(R.id.btnSnooze);
        btnSnooze.setOnClickListener(v -> {
            if (player.isPlaying()) player.stop();
            // schedule next alarm in 5 minutes
            long next = System.currentTimeMillis() + 5 * 60 * 1000;
            SleepCycleScheduler.scheduleCycleAlarms(this, next);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null && player.isPlaying()) {
            player.stop();
        }
    }
}
