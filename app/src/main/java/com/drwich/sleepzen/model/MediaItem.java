package com.drwich.sleepzen.model;

public class MediaItem {
    private final String title;
    private final String assetPath;  // e.g. "meditations/Pulse.mp3" or "sounds/forest.mp3"

    public MediaItem(String title, String assetPath) {
        this.title = title;
        this.assetPath = assetPath;
    }

    public String getTitle() {
        return title;
    }

    public String getAssetPath() {
        return assetPath;
    }
}