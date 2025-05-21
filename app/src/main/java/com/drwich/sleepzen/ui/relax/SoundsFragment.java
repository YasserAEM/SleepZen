package com.drwich.sleepzen.ui.relax;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.drwich.sleepzen.databinding.FragmentSoundsBinding;
import com.drwich.sleepzen.model.MediaItem;

import java.util.List;

public class SoundsFragment extends Fragment {
    private FragmentSoundsBinding binding;
    private RelaxViewModel viewModel;
    private MediaAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSoundsBinding.inflate(inflater, container, false);
        // shared ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(RelaxViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1) Adapter with click → play()
        adapter = new MediaAdapter(item -> viewModel.play(item));
        binding.rvSounds.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        binding.rvSounds.setAdapter(adapter);

        // 2) Observe list and populate
        viewModel.getSounds().observe(getViewLifecycleOwner(),
                this::onSoundsLoaded);

        // 3) Observe currentTrack and highlight
        viewModel.getCurrentTrack().observe(getViewLifecycleOwner(), track -> {
            adapter.setCurrentTrack(track);
        });
    }

    private void onSoundsLoaded(List<MediaItem> items) {
        adapter.setItems(items);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
