package com.drwich.sleepzen.ui.habits;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.drwich.sleepzen.databinding.FragmentHabitsBinding;
import com.drwich.sleepzen.model.HabitEntity;

import java.util.UUID;

public class HabitsFragment extends Fragment {
    private FragmentHabitsBinding binding;
    private HabitsViewModel viewModel;
    private HabitAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHabitsBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity())
                .get(HabitsViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1) Adapter listens for checked changes
        adapter = new HabitAdapter((habit, isChecked) ->
                viewModel.setDone(habit, isChecked)
        );

        // 2) RecyclerView setup
        binding.rvHabits.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );
        binding.rvHabits.setAdapter(adapter);

        // 3) Observe habit list
        viewModel.habits.observe(getViewLifecycleOwner(), habits ->
                adapter.setItems(habits)
        );

        // 4) Swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
        ) {
            @Override public boolean onMove(@NonNull RecyclerView rv,
                                            @NonNull RecyclerView.ViewHolder vh,
                                            @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int dir) {
                int pos = vh.getAdapterPosition();
                HabitEntity habit = adapter.getItems().get(pos);
                viewModel.delete(habit);
            }
        })
                .attachToRecyclerView(binding.rvHabits);

        // 5) Add new habit dialog
        binding.fabAddHabit.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Add New Habit");

            EditText input = new EditText(requireContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("Add", (dlg, which) -> {
                String label = input.getText().toString().trim();
                if (!label.isEmpty()) {
                    String id = UUID.randomUUID().toString();
                    HabitEntity newHabit = new HabitEntity(id, label, false);
                    viewModel.addHabit(newHabit);
                }
            });
            builder.setNegativeButton("Cancel", (dlg, which) -> dlg.cancel());
            builder.show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
