package com.drwich.sleepzen.ui.history;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.drwich.sleepzen.databinding.FragmentHistoryBinding;
import com.drwich.sleepzen.model.SleepSession;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class HistoryFragment extends Fragment {
    private FragmentHistoryBinding binding;
    private HistoryViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        // 1) Observe the monthly quality map
        viewModel.getSleepQualityMap().observe(getViewLifecycleOwner(), qualityMap -> {
            // clear old decorators
            binding.calendarView.removeDecorators();

            // re-add a decorator for each day’s quality
            for (Map.Entry<CalendarDay, Integer> e : qualityMap.entrySet()) {
                binding.calendarView.addDecorator(
                        new QualityDecorator(e.getKey(), e.getValue())
                );
            }

            // highlight today
            binding.calendarView.addDecorator(
                    new TodayDecorator(requireContext())
            );

            // force redraw
            binding.calendarView.invalidateDecorators();
        });

        // 2) Scroll to today on first load
        binding.calendarView.setCurrentDate(CalendarDay.today(), false);

        // 3) Handle day clicks by loading that day’s session
        binding.calendarView.setOnDateChangedListener((widget, date, selected) ->
                viewModel.loadSessionForDate(
                        date.getYear(),
                        date.getMonth(),
                        date.getDay()
                )
        );

        // 4) Show details when a session is loaded
        viewModel.getClickedSession().observe(getViewLifecycleOwner(), session -> {
            if (session == null) {
                Toast.makeText(requireContext(),
                        "No sleep data for that day",
                        Toast.LENGTH_SHORT).show();
            } else {
                showSessionDialog(session);
            }
        });

        return binding.getRoot();
    }

    private void showSessionDialog(SleepSession session) {
        String wakeTime = new SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(new Date(session.dateMillis));

        String message = "Woke up at: " + wakeTime +
                "\nQuality: " + session.quality;

        new AlertDialog.Builder(requireContext())
                .setTitle("Sleep Details")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
