package ke.co.mwalimuai.ai;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

import ke.co.mwalimuai.utils.AssetManagerHelper;

/**
 * Validates TinyML model predictions on Android against the Google Colab reference test vector.
 */
public class ModelValidator {

    private static final String TAG = "ModelValidator";
    private static final String TEST_VECTOR_PATH = "config/android_test_vector.json";

    public static class ValidationResult {
        public final boolean isSuccess;
        public final float expectedScore;
        public final float actualScore;
        public final float delta;
        public final boolean featureEncodingMatches;
        public final String details;

        public ValidationResult(boolean isSuccess, float expectedScore, float actualScore, float delta, boolean featureEncodingMatches, String details) {
            this.isSuccess = isSuccess;
            this.expectedScore = expectedScore;
            this.actualScore = actualScore;
            this.delta = delta;
            this.featureEncodingMatches = featureEncodingMatches;
            this.details = details;
        }
    }

    /**
     * Validates the loaded TinyML model against android_test_vector.json.
     */
    public static ValidationResult validate(Context context) {
        try {
            String testVectorJson = AssetManagerHelper.readAssetString(context, TEST_VECTOR_PATH);
            JSONObject root = new JSONObject(testVectorJson);

            // Parse reference float32_features
            JSONArray featuresArr = root.getJSONArray("float32_features");
            float[] referenceFeatures = new float[featuresArr.length()];
            for (int i = 0; i < featuresArr.length(); i++) {
                referenceFeatures[i] = (float) featuresArr.getDouble(i);
            }

            double expectedScoreDouble = root.getDouble("expected_score_1_to_5");
            float expectedScore = (float) expectedScoreDouble;

            TinyMLInferenceManager manager = TinyMLInferenceManager.getInstance(context);

            // 1. Verify our Java preprocessor generates identical features from teacher_activity_record
            JSONObject record = root.getJSONObject("teacher_activity_record");
            int grade = record.getInt("grade");
            int classSize = record.getInt("class_size");
            int availableMinutes = record.getInt("available_minutes");
            int activityDuration = record.getInt("activity_duration_minutes");
            boolean electricity = record.getInt("electricity_available") == 1;
            boolean internet = record.getInt("internet_available") == 1;
            String topic = record.getString("topic");
            String resources = record.getString("available_resources");
            String need = record.getString("learning_need");
            String strategy = record.getString("strategy");

            float[] computedFeatures = manager.preprocessFeatures(
                    grade, classSize, availableMinutes, activityDuration,
                    electricity, internet, topic, resources, need, strategy
            );

            boolean encodingMatches = true;
            for (int i = 0; i < referenceFeatures.length; i++) {
                if (Math.abs(referenceFeatures[i] - computedFeatures[i]) > 0.001f) {
                    encodingMatches = false;
                    Log.w(TAG, "Feature mismatch at index " + i + ": ref=" + referenceFeatures[i] + " vs computed=" + computedFeatures[i]);
                    break;
                }
            }

            // 2. Run inference with computed features
            float actualScore = manager.runInference(computedFeatures);
            float delta = Math.abs(expectedScore - actualScore);

            // In float32 or int8 optimized models, delta within 0.05 is acceptable, Colab reference is exact
            boolean passed = delta <= 0.05f && encodingMatches;

            String details = String.format(
                    "Model: %s\nEncoding Match: %b\nExpected: %.4f\nActual: %.4f\nDelta: %.5f\nStatus: %s",
                    manager.getActiveModelName(),
                    encodingMatches,
                    expectedScore,
                    actualScore,
                    delta,
                    passed ? "PASSED (Colab Agreement)" : "DISCREPANCY DETECTED"
            );

            Log.i(TAG, details);
            return new ValidationResult(passed, expectedScore, actualScore, delta, encodingMatches, details);

        } catch (Exception e) {
            Log.e(TAG, "Validation error", e);
            return new ValidationResult(false, 0f, 0f, 999f, false, "Validation exception: " + e.getMessage());
        }
    }
}
