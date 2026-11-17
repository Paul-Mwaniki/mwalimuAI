package ke.co.mwalimuai.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import ke.co.mwalimuai.R;
import ke.co.mwalimuai.ai.OnDeviceSlmEngine;
import ke.co.mwalimuai.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateModelStatusBadge();

        binding.layoutModelStatusBadge.setOnClickListener(v -> {
            if (getActivity() != null) {
                OnDeviceSlmEngine.getInstance(requireContext()).promptModelDownloadIfNeeded(requireActivity(), true);
                updateModelStatusBadge();
            }
        });

        // Option 1: Lesson Plan
        binding.cardQuickPlan.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            NavOptions navOptions = new NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setPopUpTo(R.id.navigation_home, false)
                    .build();
            navController.navigate(R.id.navigation_planner, null, navOptions);
        });

        // Option 2: Free-Form AI Prompt
        binding.cardQuickAiPrompt.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            NavOptions navOptions = new NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setPopUpTo(R.id.navigation_home, false)
                    .build();
            navController.navigate(R.id.navigation_chat, null, navOptions);
        });

        // Option 3: Access Curriculum Offline
        binding.cardQuickCurriculum.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            NavOptions navOptions = new NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setPopUpTo(R.id.navigation_home, false)
                    .build();
            navController.navigate(R.id.navigation_curriculum, null, navOptions);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateModelStatusBadge();
    }

    private void updateModelStatusBadge() {
        if (binding == null || getContext() == null) return;
        if (OnDeviceSlmEngine.isModelDownloaded(requireContext())) {
            binding.tvModelBadge.setText("⚡ Qwen2.5-0.5B Offline AI Active");
        } else {
            binding.tvModelBadge.setText("⬇ Tap to Download Qwen2.5-0.5B AI (" + OnDeviceSlmEngine.QWEN_MODEL_SIZE_DISPLAY + ")");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
