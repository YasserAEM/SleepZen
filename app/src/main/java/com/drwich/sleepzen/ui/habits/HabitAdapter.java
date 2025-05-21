package com.drwich.sleepzen.ui.habits;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.drwich.sleepzen.R;
import com.drwich.sleepzen.model.HabitEntity;

import java.util.ArrayList;
import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.ViewHolder> {
    public interface OnHabitChangeListener {
        void onHabitChanged(HabitEntity habit, boolean isChecked);
    }

    private final List<HabitEntity> items = new ArrayList<>();
    private final OnHabitChangeListener listener;

    public HabitAdapter(OnHabitChangeListener listener) {
        this.listener = listener;
    }

    public List<HabitEntity> getItems() {
        return items;
    }

    public void setItems(List<HabitEntity> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_habit, parent, false);
        return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HabitEntity habit = items.get(position);

        // 1. Detach the listener before setting checked state
        holder.checkBox.setOnCheckedChangeListener(null);
        // 2. Bind the current state
        holder.checkBox.setChecked(habit.isDone());
        // 3. Re-attach listener to catch user toggles
        holder.checkBox.setOnCheckedChangeListener((cb, isChecked) -> {
            listener.onHabitChanged(habit, isChecked);
        });

        holder.label.setText(habit.getLabel());
    }

    @Override public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final CheckBox checkBox;
        final TextView label;

        ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.cbDone);
            label    = itemView.findViewById(R.id.tvHabitLabel);
        }
    }
}
