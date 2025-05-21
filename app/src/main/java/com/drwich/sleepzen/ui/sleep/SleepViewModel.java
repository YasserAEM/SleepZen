package com.drwich.sleepzen.ui.sleep;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.drwich.sleepzen.data.SleepRepository;
import com.drwich.sleepzen.model.SleepSession;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

public class SleepViewModel extends AndroidViewModel implements SensorEventListener {
    private final SensorManager sensorManager;
    private final Sensor accelerometer;
    private MediaRecorder recorder;
    private final AtomicInteger movementCount = new AtomicInteger(0);

    private final MutableLiveData<Boolean> _needExactAlarmPermission = new MutableLiveData<>(false);
    public LiveData<Boolean> needExactAlarmPermission() {
        return _needExactAlarmPermission;
    }
    /** Call this after you’ve sent the user off to Settings */
    public void clearExactAlarmPermissionFlag() {
        _needExactAlarmPermission.setValue(false);
    }

    public SleepViewModel(@NonNull Application app) {
        super(app);
        sensorManager = app.getSystemService(SensorManager.class);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void startSession(long wakeupTimeMillis) {
        AlarmManager am = getApplication().getSystemService(AlarmManager.class);

        // If the user picked a time earlier than now, bump to tomorrow
        if (wakeupTimeMillis <= System.currentTimeMillis()) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(wakeupTimeMillis);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            wakeupTimeMillis = cal.getTimeInMillis();
        }

        // On Android S+ we can test permission first
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && !am.canScheduleExactAlarms()) {
            _needExactAlarmPermission.setValue(true);
            return;
        }

        Intent intent = new Intent(getApplication(), SleepAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(
                getApplication(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Schedule the exact alarm
        am.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                wakeupTimeMillis,
                pi
        );

        // Reset the flag, in case it was true before
        _needExactAlarmPermission.setValue(false);

        // Start motion tracking
        movementCount.set(0);
        sensorManager.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);

        // Start audio recording
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(
                getApplication().getCacheDir()
                        + "/sleep_audio_" + System.currentTimeMillis() + ".3gp"
        );
        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        SleepCycleScheduler.scheduleCycleAlarms(
                getApplication(), wakeupTimeMillis
        );
    }

    public void endSession() {
        // Stop audio
        if (recorder != null) {
            try { recorder.stop(); }
            catch (RuntimeException ignored) {}
            recorder.release();
            recorder = null;
        }

        // Stop sensors
        sensorManager.unregisterListener(this);

        // Simple quality metric by movement
        int moves = movementCount.get();
        int quality = moves < 50 ? 2
                : moves < 200 ? 1
                : 0;

        // Persist
        SleepSession session = new SleepSession(System.currentTimeMillis(), quality);
        new SleepRepository(getApplication()).insertSession(session);
    }

    // SensorEventListener
    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0], y = event.values[1], z = event.values[2];
        double mag = Math.hypot(Math.hypot(x, y), z);
        if (Math.abs(mag - SensorManager.GRAVITY_EARTH) > 1.0) {
            movementCount.incrementAndGet();
        }
    }
    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
