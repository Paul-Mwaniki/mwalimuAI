package ke.co.mwalimuai.generator;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ke.co.mwalimuai.utils.AssetManagerHelper;

/**
 * Generates CBC formative assessment rubrics and assessment prompts.
 */
public class AssessmentGenerator {

    private static final String TAG = "AssessmentGenerator";
    private static final String ASSESSMENTS_FILE = "data/Assessments.json";

    public static class AssessmentItem {
        @SerializedName("assessment_id")
        public String assessmentId;

        @SerializedName("curriculum_id")
        public String curriculumId;

        @SerializedName("activity_id")
        public String activityId;

        @SerializedName("assessment_type")
        public String assessmentType;

        @SerializedName("assessment_prompt")
        public String assessmentPrompt;

        @SerializedName("evidence_of_learning")
        public String evidenceOfLearning;

        @SerializedName("meets_expectations")
        public String meetsExpectations;

        @SerializedName("approaching_expectations")
        public String approachingExpectations;

        @SerializedName("exceeds_expectations")
        public String exceedsExpectations;

        @SerializedName("below_expectations")
        public String belowExpectations;
    }

    public static List<AssessmentItem> loadAssessments(Context context) {
        try {
            if (AssetManagerHelper.assetExists(context, ASSESSMENTS_FILE)) {
                String json = AssetManagerHelper.readAssetString(context, ASSESSMENTS_FILE);
                Gson gson = new Gson();
                Type type = new TypeToken<List<AssessmentItem>>() {}.getType();
                List<AssessmentItem> list = gson.fromJson(json, type);
                if (list != null) return list;
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to load assessments from JSON", e);
        }
        return new ArrayList<>();
    }

    public static AssessmentItem getAssessmentForCurriculum(Context context, String curriculumId) {
        if (context != null) {
            List<AssessmentItem> list = loadAssessments(context);
            for (AssessmentItem item : list) {
                if (item.curriculumId != null && item.curriculumId.equalsIgnoreCase(curriculumId)) {
                    return item;
                }
            }
        }

        // Generate synthetic CBC default rubric if not found
        AssessmentItem defaultItem = new AssessmentItem();
        defaultItem.assessmentId = "ASM-DEFAULT";
        defaultItem.curriculumId = curriculumId;
        defaultItem.assessmentType = "Formative Observation & Exit Ticket";
        defaultItem.assessmentPrompt = "Demonstrate and explain mathematical problem solving steps using local materials.";
        defaultItem.evidenceOfLearning = "Learner represents mathematical concept concretely and explains rationale.";
        defaultItem.exceedsExpectations = "Accurately solves multi-step problem and clearly articulates general mathematical principle to peers.";
        defaultItem.meetsExpectations = "Correct procedure and solution with clear justification of key steps.";
        defaultItem.approachingExpectations = "Understands core concept but requires occasional guidance on calculations or representation.";
        defaultItem.belowExpectations = "Requires continuous teacher scaffolding and hands-on manipulative assistance.";
        return defaultItem;
    }
}
