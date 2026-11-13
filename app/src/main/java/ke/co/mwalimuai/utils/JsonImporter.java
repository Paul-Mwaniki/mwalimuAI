package ke.co.mwalimuai.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ke.co.mwalimuai.database.ActivityEntity;
import ke.co.mwalimuai.database.CurriculumDao;
import ke.co.mwalimuai.database.CurriculumEntity;
import ke.co.mwalimuai.database.MwalimuDatabase;

public class JsonImporter {

    private static final String TAG = "JsonImporter";

    public interface ImportCallback {
        void onSuccess(int curriculumCount, int activityCount);
        void onError(Exception e);
    }

    /**
     * Imports curriculum and activity records from assets/data into the Room database if empty.
     */
    public static void populateDatabaseIfEmpty(Context context, MwalimuDatabase db, ImportCallback callback) {
        MwalimuDatabase.databaseWriteExecutor.execute(() -> {
            try {
                CurriculumDao dao = db.curriculumDao();
                int currCount = dao.getCurriculumCount();
                int actCount = dao.getActivityCount();

                if (currCount == 0) {
                    List<CurriculumEntity> curriculumList = loadCurriculumFromAssets(context);
                    if (!curriculumList.isEmpty()) {
                        dao.insertCurriculumList(curriculumList);
                        Log.d(TAG, "Imported " + curriculumList.size() + " curriculum records.");
                    }
                }

                if (actCount == 0) {
                    List<ActivityEntity> activityList = loadActivitiesFromAssets(context);
                    if (!activityList.isEmpty()) {
                        dao.insertActivities(activityList);
                        Log.d(TAG, "Imported " + activityList.size() + " activity records.");
                    }
                }

                if (callback != null) {
                    callback.onSuccess(dao.getCurriculumCount(), dao.getActivityCount());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error importing data from JSON", e);
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }

    public static List<CurriculumEntity> loadCurriculumFromAssets(Context context) throws IOException {
        String json = AssetManagerHelper.readAssetString(context, "data/Curriculum.json");
        Gson gson = new Gson();
        Type type = new TypeToken<List<CurriculumEntity>>() {}.getType();
        List<CurriculumEntity> list = gson.fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }

    public static List<ActivityEntity> loadActivitiesFromAssets(Context context) throws IOException {
        String json = AssetManagerHelper.readAssetString(context, "data/Activities.json");
        Gson gson = new Gson();
        Type type = new TypeToken<List<ActivityEntity>>() {}.getType();
        List<ActivityEntity> list = gson.fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }
}
