package ke.co.mwalimuai;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ke.co.mwalimuai.ai.TinyMLInferenceManager;
import ke.co.mwalimuai.database.MwalimuDatabase;
import ke.co.mwalimuai.databinding.ActivitySplashBinding;
import ke.co.mwalimuai.utils.JsonImporter;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tvSplashStatus.setText("Initializing Offline Engine & CBE Curriculum...");

        executor.execute(() -> {
            long startTime = System.currentTimeMillis();

            // 1. Initialize Room Database and populate from JSON assets if empty
            MwalimuDatabase db = MwalimuDatabase.getInstance(getApplicationContext());
            try {
                // Ensure tables are populated
                JsonImporter.populateDatabaseIfEmpty(getApplicationContext(), db, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 2. Preload TinyML model and preprocessing configurations
            try {
                TinyMLInferenceManager.getInstance(getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Ensure splash stays visible at least 1.2s for pleasant brand experience
            long elapsed = System.currentTimeMillis() - startTime;
            if (elapsed < 1200) {
                try {
                    Thread.sleep(1200 - elapsed);
                } catch (InterruptedException ignored) {}
            }

            new Handler(Looper.getMainLooper()).post(this::navigateToMain);
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
