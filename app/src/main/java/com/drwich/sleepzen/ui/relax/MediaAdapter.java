package com.drwich.sleepzen.ui.relax;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.drwich.sleepzen.R;
import com.drwich.sleepzen.model.MediaItem;

import java.util.ArrayList;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(MediaItem item);
    }

    private final List<MediaItem> items = new ArrayList<>();
    private final OnItemClickListener listener;
    // Track which item is currently playing
    private MediaItem currentTrack;

    public MediaAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<MediaItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    /** Call this whenever playback changes */
    public void setCurrentTrack(MediaItem track) {
        this.currentTrack = track;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_media, parent, false);
        return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        MediaItem item = items.get(pos);

        // Strip “.mp3” extension if present (case-insensitive)
        String rawTitle = item.getTitle();
        String displayTitle = rawTitle;
        if (displayTitle.toLowerCase().endsWith(".mp3")) {
            displayTitle = displayTitle.substring(0, displayTitle.length() - 4);
        }
        holder.title.setText(displayTitle);

        // Highlight if this is the current track
        if (item.equals(currentTrack)) {
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
        }
    }
}
