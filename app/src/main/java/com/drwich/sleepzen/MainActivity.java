package com.drwich.sleepzen;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.drwich.sleepzen.databinding.ActivityMainBinding;
import com.drwich.sleepzen.ui.relax.RelaxViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        // Inflate view binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Hide the default ActionBar (we'll use a custom media controller)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize media controller views
        TextView tvMediaTitle = findViewById(R.id.tvMediaTitle);
        ImageButton btnPlayPause = findViewById(R.id.btnPlayPause);

        // Obtain shared RelaxViewModel
        RelaxViewModel viewModel = new ViewModelProvider(this).get(RelaxViewModel.class);

        // Observe current track to update title visibility
        viewModel.getCurrentTrack().observe(this, item -> {
            if (item != null) {
                tvMediaTitle.setText(item.getTitle());
                btnPlayPause.setVisibility(View.VISIBLE);
            } else {
                tvMediaTitle.setText("No track");
                btnPlayPause.setVisibility(View.GONE);
            }
        });

        // Observe playing state to toggle play/pause icon
        viewModel.isPlaying().observe(this, playing -> {
            if (playing != null && playing) {
                btnPlayPause.setImageResource(R.drawable.pause_50);
            } else {
                btnPlayPause.setImageResource(R.drawable.play_50);
            }
        });

        // Handle play/pause toggle
        btnPlayPause.setOnClickListener(v -> {
            Boolean playing = viewModel.isPlaying().getValue();
            if (playing != null && playing) {
                viewModel.pause();
            } else {
                viewModel.resume();
            }
        });

        // Setup navigation with BottomNavigationView
        BottomNavigationView navView = binding.navView;
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(navView, navController);
    }
}
