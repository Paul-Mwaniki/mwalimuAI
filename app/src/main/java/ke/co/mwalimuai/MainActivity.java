package ke.co.mwalimuai;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import ke.co.mwalimuai.ai.OnDeviceSlmEngine;
import ke.co.mwalimuai.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.topToolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home,
                    R.id.navigation_planner,
                    R.id.navigation_chat,
                    R.id.navigation_curriculum
            ).build();

            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

            binding.bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (navController.getCurrentDestination() != null &&
                        navController.getCurrentDestination().getId() == itemId) {
                    return true;
                }
                androidx.navigation.NavOptions navOptions = new androidx.navigation.NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(R.id.navigation_home, itemId == R.id.navigation_home)
                        .build();
                try {
                    navController.navigate(itemId, null, navOptions);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });

            // Synchronize bottom nav selection when destination changes
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int destId = destination.getId();
                android.view.Menu menu = binding.bottomNavigationView.getMenu();
                for (int i = 0; i < menu.size(); i++) {
                    android.view.MenuItem menuItem = menu.getItem(i);
                    if (menuItem.getItemId() == destId) {
                        menuItem.setChecked(true);
                        break;
                    }
                }
            });

            // Keep top toolbar navigation working seamlessly
            binding.topToolbar.setNavigationOnClickListener(v -> {
                if (navController != null && !NavigationUI.navigateUp(navController, appBarConfiguration)) {
                    getOnBackPressedDispatcher().onBackPressed();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if Qwen2.5-0.5B model is downloaded; if not, and internet/Wi-Fi is connected, prompt user
        OnDeviceSlmEngine.getInstance(this).promptModelDownloadIfNeeded(this, false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return (navController != null && NavigationUI.navigateUp(navController, appBarConfiguration))
                || super.onSupportNavigateUp();
    }
}
