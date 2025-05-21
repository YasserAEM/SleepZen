package com.drwich.sleepzen.ui.relax;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.drwich.sleepzen.databinding.FragmentRelaxContainerBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class RelaxContainerFragment extends Fragment {
    private FragmentRelaxContainerBinding binding;
    private final String[] tabTitles = {"Meditate", "Sounds"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRelaxContainerBinding.inflate(inflater, container, false);
        binding.getRoot().setPadding(0, 0, 0, 0);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Adapter
        binding.viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull @Override
            public Fragment createFragment(int pos) {
                return pos == 0
                        ? new MeditateFragment()
                        : new SoundsFragment();
            }
            @Override public int getItemCount() { return tabTitles.length; }
        });
        // TabLayout ↔ ViewPager2
        new TabLayoutMediator(
                binding.tabLayout, binding.viewPager,
                (tab, pos) -> tab.setText(tabTitles[pos])
        ).attach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
