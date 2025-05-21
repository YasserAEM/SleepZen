package com.drwich.sleepzen.ui.sleep;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.drwich.sleepzen.databinding.FragmentSleepBinding;

import java.util.Calendar;

public class SleepFragment extends Fragment {
    private static final String TAG = "SleepFragment";
    private static final String PREFS = "sleep_prefs";
    private static final String KEY_DARK_HOUR = "dark_hour";
    private static final String KEY_DARK_MIN  = "dark_min";

    private FragmentSleepBinding binding;
    private SleepViewModel viewModel;
    private long wakeupTimeMillis = -1;
    private BroadcastReceiver endReceiver;
    private ActivityResultLauncher<String> audioPermLauncher;
    private ActivityResultLauncher<String> notifPermLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Launcher for RECORD_AUDIO
        audioPermLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (Boolean.TRUE.equals(granted)) {
                        startSleepSession();
                    } else {
                        Toast.makeText(
                                requireContext(),
                                "Audio permission required to record sleep.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );

        // Launcher for POST_NOTIFICATIONS (Android 13+)
        notifPermLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (!Boolean.TRUE.equals(granted)) {
                        Toast.makeText(
                                requireContext(),
                                "Please allow notifications to get Dark Mode reminders.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSleepBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity())
                .get(SleepViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Request notifications permission if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
                notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        // Wake-up time picker
        binding.btnPickTime.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            new TimePickerDialog(requireContext(),
                    (picker, hour, minute) -> {
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.HOUR_OF_DAY, hour);
                        cal.set(Calendar.MINUTE, minute);
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MILLISECOND, 0);
                        wakeupTimeMillis = cal.getTimeInMillis();
                        String fmt = DateFormat.getTimeFormat(requireContext())
                                .format(cal.getTime());
                        binding.tvChosenTime.setText(fmt);
                    },
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    DateFormat.is24HourFormat(requireContext())
            ).show();
        });

        // Start sleep session
        binding.btnStartSleep.setOnClickListener(v -> {
            if (wakeupTimeMillis < 0) {
                Toast.makeText(
                        requireContext(),
                        "Please set a wake-up time first",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                audioPermLauncher.launch(Manifest.permission.RECORD_AUDIO);
            } else {
                startSleepSession();
            }
        });

        // Dark-mode time picker
        binding.btnPickDarkModeTime.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            new TimePickerDialog(requireContext(),
                    (picker, hour, minute) -> {
                        SharedPreferences prefs = requireContext()
                                .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
                        prefs.edit()
                                .putInt(KEY_DARK_HOUR, hour)
                                .putInt(KEY_DARK_MIN,  minute)
                                .apply();
                        updateDarkModeTimeUI(hour, minute);
                        DarkModeScheduler.scheduleNext(
                                requireContext(), hour, minute
                        );
                    },
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    DateFormat.is24HourFormat(requireContext())
            ).show();
        });

        // Handle exact-alarm permission requests
        viewModel.needExactAlarmPermission().observe(getViewLifecycleOwner(), need -> {
            if (Boolean.TRUE.equals(need)) {
                startActivity(new Intent(
                        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                ));
                viewModel.clearExactAlarmPermissionFlag();
            }
        });

        // Listen for sleep-alarm end broadcast
        endReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                viewModel.endSession();
                binding.tvChosenTime.setText("Session ended");
                Toast.makeText(
                        requireContext(),
                        "Sleep session ended",
                        Toast.LENGTH_SHORT
                ).show();
            }
        };
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
                endReceiver,
                new IntentFilter(SleepAlarmReceiver.ACTION_END_SLEEP)
        );

        // Load & display saved dark-mode time, and reschedule alarm
        SharedPreferences prefs = requireContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        int h = prefs.getInt(KEY_DARK_HOUR, -1);
        int m = prefs.getInt(KEY_DARK_MIN,  -1);
        if (h >= 0 && m >= 0) {
            updateDarkModeTimeUI(h, m);
            DarkModeScheduler.scheduleNext(requireContext(), h, m);
            Log.d(TAG, "Re-scheduled dark-mode alarm for " + h + ":" + m);
        }
    }

    private void startSleepSession() {
        viewModel.startSession(wakeupTimeMillis);
        binding.btnPickTime.setEnabled(false);
        binding.btnStartSleep.setEnabled(false);
        binding.tvChosenTime.setText("Sleeping…");
    }

    private void updateDarkModeTimeUI(int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        String fmt = DateFormat.getTimeFormat(requireContext())
                .format(cal.getTime());
        binding.tvDarkModeTime.setText(fmt);
    }

    @Override
    public void onDestroyView() {
        LocalBroadcastManager.getInstance(requireContext())
                .unregisterReceiver(endReceiver);
        super.onDestroyView();
        binding = null;
    }
}
