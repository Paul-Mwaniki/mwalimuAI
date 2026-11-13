package ke.co.mwalimuai.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ke.co.mwalimuai.ai.ActivityRecommendationEngine;
import ke.co.mwalimuai.database.ActivityEntity;
import ke.co.mwalimuai.database.CurriculumDao;
import ke.co.mwalimuai.database.MwalimuDatabase;

public class ActivityViewModel extends AndroidViewModel {

    private final CurriculumDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<List<ActivityEntity>> activities = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private List<ActivityEntity> fullList = new ArrayList<>();

    public ActivityViewModel(@NonNull Application application) {
        super(application);
        dao = MwalimuDatabase.getInstance(application).curriculumDao();
        loadAllActivities();
    }

    public LiveData<List<ActivityEntity>> getActivities() {
        return activities;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadAllActivities() {
        isLoading.setValue(true);
        executor.execute(() -> {
            fullList = dao.getAllActivities();
            activities.postValue(fullList);
            isLoading.postValue(false);
        });
    }

    public void filterActivities(String query, String strategyFilter, boolean noInternetOnly) {
        executor.execute(() -> {
            List<ActivityEntity> filtered = new ArrayList<>();
            String q = query != null ? query.toLowerCase(Locale.ROOT).trim() : "";

            for (ActivityEntity act : fullList) {
                if (noInternetOnly && act.isInternetRequired()) {
                    continue;
                }
                if (strategyFilter != null && !strategyFilter.equalsIgnoreCase("All") &&
                        !strategyFilter.equalsIgnoreCase(act.getStrategy())) {
                    continue;
                }
                if (!q.isEmpty()) {
                    boolean match = act.getActivityTitle().toLowerCase(Locale.ROOT).contains(q) ||
                            act.getTopic().toLowerCase(Locale.ROOT).contains(q) ||
                            act.getDescription().toLowerCase(Locale.ROOT).contains(q) ||
                            act.getResources().toLowerCase(Locale.ROOT).contains(q);
                    if (!match) continue;
                }
                filtered.add(act);
            }
            activities.postValue(filtered);
        });
    }

    public void rankWithTeacherProfile(ActivityRecommendationEngine.RecommendationRequest request) {
        isLoading.setValue(true);
        executor.execute(() -> {
            List<ActivityEntity> ranked = ActivityRecommendationEngine.rankActivities(
                    getApplication(), fullList, request
            );
            activities.postValue(ranked);
            isLoading.postValue(false);
        });
    }
}
