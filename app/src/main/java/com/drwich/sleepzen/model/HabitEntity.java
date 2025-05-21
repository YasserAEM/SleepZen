package com.drwich.sleepzen.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "habits")
public class HabitEntity {
    @PrimaryKey
    @NonNull
    private String id;       // e.g. "drink_water" or a UUID
    private String label;    // e.g. "Drink water"
    private boolean done;    // checked state
    private boolean builtIn; // true for the two defaults

    public HabitEntity(@NonNull String id, String label, boolean builtIn) {
        this.id = id;
        this.label = label;
        this.builtIn = builtIn;
        this.done = false;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isBuiltIn() {
        return builtIn;
    }
}
