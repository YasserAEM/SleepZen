package com.drwich.sleepzen.ui.relax;

import android.app.Application;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.drwich.sleepzen.data.RelaxRepository;
import com.drwich.sleepzen.model.MediaItem;
import java.io.IOException;
import java.util.List;

public class RelaxViewModel extends AndroidViewModel {
    private final RelaxRepository repo;
    private final MutableLiveData<List<MediaItem>> meditations = new MutableLiveData<>();
    private final MutableLiveData<List<MediaItem>> sounds      = new MutableLiveData<>();
    private final MutableLiveData<MediaItem> currentTrack      = new MutableLiveData<>();
    private MediaPlayer mediaPlayer;

    private final MutableLiveData<Boolean> isPlayingLiveData = new MutableLiveData<>(false);

    public LiveData<Boolean> isPlaying() { return isPlayingLiveData; }


    public RelaxViewModel(@NonNull Application application) {
        super(application);
        repo = new RelaxRepository(application);
        meditations.setValue(repo.getMeditations());
        sounds.setValue(repo.getSounds());
    }

    public LiveData<List<MediaItem>> getMeditations() { return meditations; }
    public LiveData<List<MediaItem>> getSounds()       { return sounds; }
    public LiveData<MediaItem> getCurrentTrack()       { return currentTrack; }

    public void play(@NonNull MediaItem item) {
        stop();
        mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor afd = getApplication().getAssets().openFd(item.getAssetPath());
            mediaPlayer.setDataSource(
                    afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mediaPlayer.prepare();
            // 3) In play(), after mediaPlayer.start():
            mediaPlayer.start();
            isPlayingLiveData.setValue(true);
            currentTrack.setValue(item);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlayingLiveData.setValue(false);
        }
    }
    public void resume() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            isPlayingLiveData.setValue(true);
        }
    }
    public void stop() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            currentTrack.setValue(null);
            isPlayingLiveData.setValue(false);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stop();
    }
}