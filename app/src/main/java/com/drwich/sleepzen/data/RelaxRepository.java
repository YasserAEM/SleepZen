package com.drwich.sleepzen.data;

import android.content.Context;
import com.drwich.sleepzen.model.MediaItem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RelaxRepository {
    private static final String FOLDER_MEDITATIONS = "meditations";
    private static final String FOLDER_SOUNDS      = "sounds";

    private final Context context;

    public RelaxRepository(Context context) {
        this.context = context;
    }

    public List<MediaItem> getMeditations() {
        return loadFromAssets(FOLDER_MEDITATIONS);
    }

    public List<MediaItem> getSounds() {
        return loadFromAssets(FOLDER_SOUNDS);
    }

    private List<MediaItem> loadFromAssets(String folder) {
        List<MediaItem> items = new ArrayList<>();
        try {
            String[] files = context.getAssets().list(folder);
            if (files != null) {
                for (String filename : files) {
                    String title = filename;
                    String assetPath = folder + "/" + filename;
                    items.add(new MediaItem(title, assetPath));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }
}