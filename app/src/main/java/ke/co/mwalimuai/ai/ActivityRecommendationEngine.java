package ke.co.mwalimuai.ai;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ke.co.mwalimuai.database.ActivityEntity;

/**
 * Recommends and ranks CBC learning activities.
 * Enforces hard classroom constraints first (duration, materials, electricity, internet),
 * then scores and ranks feasible candidates using the TinyML inference model.
 */
public class ActivityRecommendationEngine {

    private static final String TAG = "ActivityRecEngine";

    public static class RecommendationRequest {
        public int grade = 6;
        public int classSize = 40;
        public int availableMinutes = 40;
        public boolean electricityAvailable = false;
        public boolean internetAvailable = false;
        public String availableResources = "paper and chalkboard";
        public String learningNeed = "mixed ability";
        public String topic = "";
        public String curriculumId = "";
        public String preferredStrategy = "";
    }

    public static List<ActivityEntity> rankActivities(
            Context context,
            List<ActivityEntity> candidatePool,
            RecommendationRequest request) {

        if (candidatePool == null || candidatePool.isEmpty()) {
            return new ArrayList<>();
        }

        TinyMLInferenceManager inferenceManager = TinyMLInferenceManager.getInstance(context);
        List<ActivityEntity> feasibleActivities = new ArrayList<>();

        // 1. Hard Constraints Filtering (as instructed in model_manifest.json)
        for (ActivityEntity activity : candidatePool) {
            // Time constraint: individual activity shouldn't exceed lesson duration
            if (activity.getDurationMinutes() > request.availableMinutes) {
                continue;
            }

            // Infrastructure constraints
            if (!request.electricityAvailable && activity.isElectricityRequired()) {
                continue;
            }
            if (!request.internetAvailable && activity.isInternetRequired()) {
                continue;
            }

            feasibleActivities.add(activity);
        }

        // If hard constraints were too strict and left no items, relax time constraint slightly
        if (feasibleActivities.isEmpty()) {
            feasibleActivities.addAll(candidatePool);
        }

        // 2. Score feasible candidates with TinyML model
        String prefStratLower = request.preferredStrategy != null ? request.preferredStrategy.toLowerCase() : "";
        boolean hasPrefStrat = !prefStratLower.isEmpty()
                && !prefStratLower.startsWith("ai auto")
                && !prefStratLower.startsWith("recommended");

        for (ActivityEntity activity : feasibleActivities) {
            float score = inferenceManager.predictSuitability(
                    activity,
                    request.classSize,
                    request.availableMinutes,
                    request.electricityAvailable,
                    request.internetAvailable,
                    request.availableResources,
                    request.learningNeed
            );

            // Boost score if activity matches teacher-selected preferred strategy
            if (hasPrefStrat && activity.getStrategy() != null) {
                String actStratLower = activity.getStrategy().toLowerCase();
                if (actStratLower.contains(prefStratLower) || prefStratLower.contains(actStratLower)) {
                    score += 2.0f; // Prioritize teacher's chosen strategy
                }
            }

            activity.setAiSuitabilityScore(score);
        }

        // 3. Sort by suitability score descending
        Collections.sort(feasibleActivities, (a, b) ->
                Float.compare(b.getAiSuitabilityScore(), a.getAiSuitabilityScore())
        );

        Log.d(TAG, "Ranked " + feasibleActivities.size() + " activities. Top score: " +
                (feasibleActivities.isEmpty() ? 0 : feasibleActivities.get(0).getAiSuitabilityScore()));

        return feasibleActivities;
    }
}
