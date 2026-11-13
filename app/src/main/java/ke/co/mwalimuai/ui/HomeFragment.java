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
import ke.co.mwalimuai.ai.TinyMLInferenceManager;
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

        // Display Active TinyML Model info
        TinyMLInferenceManager manager = TinyMLInferenceManager.getInstance(requireContext());
        if (manager.isModelReady()) {
            binding.tvModelBadge.setText("⚡ Offline CBE Engine Ready");
        } else {
            binding.tvModelBadge.setText("⚡ Offline Mode Active");
        }

        // Option 1: Lesson Plan
        binding.cardQuickPlan.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            NavOptions navOptions = new NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setPopUpTo(R.id.navigation_home, false)
                    .build();
            navController.navigate(R.id.navigation_planner, null, navOptions);
        });

        // Option 2: Access Curriculum Offline
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
