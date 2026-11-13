package ke.co.mwalimuai.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ke.co.mwalimuai.ai.ActivityRecommendationEngine;
import ke.co.mwalimuai.database.ActivityEntity;
import ke.co.mwalimuai.database.CurriculumDao;
import ke.co.mwalimuai.database.CurriculumEntity;
import ke.co.mwalimuai.database.LessonEntity;
import ke.co.mwalimuai.database.MwalimuDatabase;
import ke.co.mwalimuai.export.PdfExportManager;
import ke.co.mwalimuai.export.WordExportManager;
import ke.co.mwalimuai.generator.LessonGenerator;

public class LessonViewModel extends AndroidViewModel {

    private final CurriculumDao dao;
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    private final LiveData<List<LessonEntity>> savedLessons;
    private final MutableLiveData<LessonEntity> currentLesson = new MutableLiveData<>();
    private final MutableLiveData<List<ActivityEntity>> topRankedActivities = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isGenerating = new MutableLiveData<>(false);
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();

    public interface ExportCallback {
        void onSuccess(File file);
        void onError(Exception e);
    }

    public LessonViewModel(@NonNull Application application) {
        super(application);
        MwalimuDatabase db = MwalimuDatabase.getInstance(application);
        dao = db.curriculumDao();
        savedLessons = dao.getAllLessonsLiveData();
    }

    public LiveData<List<LessonEntity>> getSavedLessons() {
        return savedLessons;
    }

    public LiveData<LessonEntity> getCurrentLesson() {
        return currentLesson;
    }

    public LiveData<List<ActivityEntity>> getTopRankedActivities() {
        return topRankedActivities;
    }

    public LiveData<Boolean> getIsGenerating() {
        return isGenerating;
    }

    public LiveData<String> getStatusMessage() {
        return statusMessage;
    }

    public void setCurrentLesson(LessonEntity lesson) {
        this.currentLesson.setValue(lesson);
    }

    /**
     * Generates a complete AI-ranked CBC lesson plan asynchronously.
     */
    public void generateLessonPlan(LessonGenerator.LessonPlanningParameters params) {
        isGenerating.setValue(true);
        executor.execute(() -> {
            try {
                long startTime = System.currentTimeMillis();

                // 1. Fetch candidate activities for this curriculum strand/topic
                String topic = params.curriculum != null ? params.curriculum.getSubStrand() : "";
                String currId = params.curriculum != null ? params.curriculum.getCurriculumId() : "";
                List<ActivityEntity> candidatePool = dao.getActivitiesByTopicOrCurriculum(topic, currId);
                if (candidatePool.isEmpty()) {
                    candidatePool = dao.getAllActivities();
                }

                // 2. Rank candidates using TinyML inference
                ActivityRecommendationEngine.RecommendationRequest req = new ActivityRecommendationEngine.RecommendationRequest();
                req.grade = 6;
                req.classSize = params.classSize;
                req.availableMinutes = params.durationMinutes;
                req.electricityAvailable = params.electricity;
                req.internetAvailable = params.internet;
                req.availableResources = params.availableResources;
                req.learningNeed = params.learningNeed;
                req.topic = topic;
                req.curriculumId = currId;
                req.preferredStrategy = params.selectedActivityStrategy;

                List<ActivityEntity> ranked = ActivityRecommendationEngine.rankActivities(
                        getApplication(), candidatePool, req
                );

                // 3. Assemble complete lesson plan
                LessonEntity lesson = LessonGenerator.generateLessonPlan(getApplication(), params, ranked);

                // Ensure user sees the "Developing the plan..." feedback clearly
                long elapsed = System.currentTimeMillis() - startTime;
                if (elapsed < 1200) {
                    try {
                        Thread.sleep(1200 - elapsed);
                    } catch (InterruptedException ignored) {}
                }

                topRankedActivities.postValue(ranked);
                currentLesson.postValue(lesson);
                isGenerating.postValue(false);
                statusMessage.postValue("Lesson plan successfully generated!");
            } catch (Exception e) {
                isGenerating.postValue(false);
                statusMessage.postValue("Error generating lesson: " + e.getMessage());
            }
        });
    }

    public void saveCurrentLesson() {
        LessonEntity lesson = currentLesson.getValue();
        if (lesson != null) {
            executor.execute(() -> {
                long id = dao.insertLesson(lesson);
                lesson.setId(id);
                statusMessage.postValue("Lesson plan saved to local database!");
            });
        }
    }

    public void deleteLesson(LessonEntity lesson) {
        if (lesson != null) {
            executor.execute(() -> {
                dao.deleteLesson(lesson);
                statusMessage.postValue("Lesson plan deleted.");
            });
        }
    }

    public void exportToWord(Context context, LessonEntity lesson, ExportCallback callback) {
        executor.execute(() -> {
            try {
                File file = WordExportManager.exportLessonToDocx(context, lesson);
                callback.onSuccess(file);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public void exportToPdf(Context context, LessonEntity lesson, ExportCallback callback) {
        executor.execute(() -> {
            try {
                File file = PdfExportManager.exportLessonToPdf(context, lesson);
                callback.onSuccess(file);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }
}
