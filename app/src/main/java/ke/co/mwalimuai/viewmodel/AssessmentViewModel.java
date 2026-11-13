package ke.co.mwalimuai.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ke.co.mwalimuai.generator.AssessmentGenerator;

public class AssessmentViewModel extends AndroidViewModel {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final MutableLiveData<List<AssessmentGenerator.AssessmentItem>> assessments = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private List<AssessmentGenerator.AssessmentItem> masterList = new ArrayList<>();

    public AssessmentViewModel(@NonNull Application application) {
        super(application);
        loadAllAssessments();
    }

    public LiveData<List<AssessmentGenerator.AssessmentItem>> getAssessments() {
        return assessments;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadAllAssessments() {
        isLoading.setValue(true);
        executor.execute(() -> {
            masterList = AssessmentGenerator.loadAssessments(getApplication());
            assessments.postValue(masterList);
            isLoading.postValue(false);
        });
    }

    public void filterByCurriculumId(String curriculumId) {
        if (curriculumId == null || curriculumId.equalsIgnoreCase("All")) {
            assessments.setValue(masterList);
            return;
        }
        List<AssessmentGenerator.AssessmentItem> filtered = new ArrayList<>();
        for (AssessmentGenerator.AssessmentItem item : masterList) {
            if (item.curriculumId != null && item.curriculumId.equalsIgnoreCase(curriculumId)) {
                filtered.add(item);
            }
        }
        assessments.setValue(filtered);
    }
}
