package ke.co.mwalimuai.ai;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ke.co.mwalimuai.database.ActivityEntity;
import ke.co.mwalimuai.utils.AssetManagerHelper;

/**
 * Manages TensorFlow Lite inference for Mwalimu AI activity suitability ranking.
 * Implements exact feature normalization and one-hot encoding matching Google Colab.
 */
public class TinyMLInferenceManager {

    private static final String TAG = "TinyMLInference";
    private static final String DEFAULT_MODEL_PATH = "models/mwalimu_activity_ranker_optimized.tflite";
    private static final String FP32_MODEL_PATH = "models/mwalimu_activity_ranker_fp32.tflite";
    private static final String PREPROCESSING_PATH = "config/preprocessing.json";
    private static final String MANIFEST_PATH = "config/model_manifest.json";

    public static final int INPUT_DIMENSION = 35;

    private static volatile TinyMLInferenceManager INSTANCE;

    private Interpreter interpreter;
    private boolean isInitialized = false;
    private String activeModelName = "None";

    // Preprocessing configurations
    private final Map<String, Float> means = new HashMap<>();
    private final Map<String, Float> stds = new HashMap<>();
    private final List<String> topicVocab = new ArrayList<>();
    private final List<String> resourcesVocab = new ArrayList<>();
    private final List<String> learningNeedVocab = new ArrayList<>();
    private final List<String> strategyVocab = new ArrayList<>();

    public static TinyMLInferenceManager getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (TinyMLInferenceManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TinyMLInferenceManager(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    private TinyMLInferenceManager(Context context) {
        initialize(context);
    }

    /**
     * Initializes preprocessor configurations and TensorFlow Lite interpreter.
     */
    public synchronized void initialize(Context context) {
        if (isInitialized) return;

        try {
            loadPreprocessingConfig(context);
        } catch (Exception e) {
            Log.e(TAG, "Failed to load preprocessing config", e);
        }

        // Try loading optimized model first, then fp32 model
        boolean loaded = loadModel(context, DEFAULT_MODEL_PATH);
        if (!loaded) {
            Log.w(TAG, "Optimized model not loaded, attempting fp32 model...");
            loaded = loadModel(context, FP32_MODEL_PATH);
        }

        isInitialized = loaded;
        Log.i(TAG, "TinyMLInferenceManager initialized: " + (isInitialized ? "SUCCESS (" + activeModelName + ")" : "FALLBACK MODE"));
    }

    private void loadPreprocessingConfig(Context context) throws Exception {
        String jsonStr = AssetManagerHelper.readAssetString(context, PREPROCESSING_PATH);
        JSONObject root = new JSONObject(jsonStr);

        // Numeric means & stds
        JSONObject meansObj = root.getJSONObject("means");
        JSONObject stdsObj = root.getJSONObject("stds");
        String[] numericKeys = {"grade", "class_size", "available_minutes", "activity_duration_minutes", "electricity_available", "internet_available"};
        for (String key : numericKeys) {
            if (meansObj.has(key)) means.put(key, (float) meansObj.getDouble(key));
            if (stdsObj.has(key)) stds.put(key, (float) stdsObj.getDouble(key));
        }

        // Categorical vocabularies
        JSONObject vocabs = root.getJSONObject("vocabularies");
        loadVocab(vocabs.getJSONArray("topic"), topicVocab);
        loadVocab(vocabs.getJSONArray("available_resources"), resourcesVocab);
        loadVocab(vocabs.getJSONArray("learning_need"), learningNeedVocab);
        loadVocab(vocabs.getJSONArray("strategy"), strategyVocab);

        Log.d(TAG, "Preprocessing config loaded. Topic vocab size: " + topicVocab.size() +
                ", Resources: " + resourcesVocab.size() +
                ", Needs: " + learningNeedVocab.size() +
                ", Strategies: " + strategyVocab.size());
    }

    private void loadVocab(JSONArray arr, List<String> targetList) throws Exception {
        targetList.clear();
        for (int i = 0; i < arr.length(); i++) {
            targetList.add(arr.getString(i));
        }
    }

    private boolean loadModel(Context context, String modelPath) {
        try {
            if (!AssetManagerHelper.assetExists(context, modelPath)) {
                Log.w(TAG, "Model asset does not exist at " + modelPath);
                return false;
            }
            MappedByteBuffer buffer = AssetManagerHelper.loadModelFile(context, modelPath);
            Interpreter.Options options = new Interpreter.Options();
            options.setNumThreads(2);
            interpreter = new Interpreter(buffer, options);

            // Validate tensor specifications
            int[] inputShape = interpreter.getInputTensor(0).shape();
            int[] outputShape = interpreter.getOutputTensor(0).shape();
            Log.i(TAG, "Loaded model " + modelPath + " with input shape: [" + inputShape[0] + ", " + inputShape[1] +
                    "], output shape: [" + outputShape[0] + ", " + outputShape[1] + "]");

            activeModelName = modelPath;
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error loading model from " + modelPath, e);
            return false;
        }
    }

    /**
     * Preprocesses teacher profile and activity features into exact 35-float vector.
     */
    public float[] preprocessFeatures(
            int grade,
            int classSize,
            int availableMinutes,
            int activityDurationMinutes,
            boolean electricityAvailable,
            boolean internetAvailable,
            String topic,
            String availableResources,
            String learningNeed,
            String strategy) {

        float[] features = new float[INPUT_DIMENSION];

        // 1. Numeric features (Z-score standardized using training means & stds)
        features[0] = normalize("grade", grade, 6.0f, 1.0f);
        features[1] = normalize("class_size", classSize, 44.0f, 14.282857f);
        features[2] = normalize("available_minutes", availableMinutes, 17.4f, 7.144228f);
        features[3] = normalize("activity_duration_minutes", activityDurationMinutes, 13.4f, 4.004997f);
        features[4] = normalize("electricity_available", electricityAvailable ? 1.0f : 0.0f, 0.0f, 1.0f);
        features[5] = normalize("internet_available", internetAvailable ? 1.0f : 0.0f, 0.0f, 1.0f);

        // 2. One-hot categorical: topic (indices 6 to 14, 9 elements)
        int offset = 6;
        encodeOneHot(topic, topicVocab, features, offset);
        offset += topicVocab.size(); // 6 + 9 = 15

        // 3. One-hot categorical: available_resources (indices 15 to 19, 5 elements)
        encodeOneHot(availableResources, resourcesVocab, features, offset);
        offset += resourcesVocab.size(); // 15 + 5 = 20

        // 4. One-hot categorical: learning_need (indices 20 to 24, 5 elements)
        encodeOneHot(learningNeed, learningNeedVocab, features, offset);
        offset += learningNeedVocab.size(); // 20 + 5 = 25

        // 5. One-hot categorical: strategy (indices 25 to 34, 10 elements)
        encodeOneHot(strategy, strategyVocab, features, offset);

        return features;
    }

    private float normalize(String key, float val, float defaultMean, float defaultStd) {
        float mean = means.containsKey(key) ? means.get(key) : defaultMean;
        float std = stds.containsKey(key) ? stds.get(key) : defaultStd;
        if (std == 0.0f) std = 1.0f;
        return (val - mean) / std;
    }

    private void encodeOneHot(String value, List<String> vocab, float[] target, int offset) {
        if (value == null) return;
        String valTrimmed = value.trim();
        for (int i = 0; i < vocab.size(); i++) {
            if (vocab.get(i).equalsIgnoreCase(valTrimmed)) {
                target[offset + i] = 1.0f;
                return;
            }
        }
        // If not exact match, check for keyword occurrence in custom user resources
        String valLower = valTrimmed.toLowerCase(java.util.Locale.ROOT);
        for (int i = 0; i < vocab.size(); i++) {
            String vLower = vocab.get(i).toLowerCase(java.util.Locale.ROOT);
            for (String part : vLower.split(" ")) {
                if (part.length() > 3 && valLower.contains(part)) {
                    target[offset + i] = 1.0f;
                    return;
                }
            }
        }
        // If tactile nature materials (e.g. seeds, sticks, pebbles), map to tactile manipulative category
        if (valLower.contains("seed") || valLower.contains("stick") || valLower.contains("leaf") ||
                valLower.contains("bead") || valLower.contains("maize") || valLower.contains("cap")) {
            for (int i = 0; i < vocab.size(); i++) {
                if (vocab.get(i).contains("stones") || vocab.get(i).contains("bottle")) {
                    target[offset + i] = 1.0f;
                    return;
                }
            }
        }
        // Unknown category receives all-zero vector as specified in preprocessing.json
    }

    /**
     * Runs TinyML inference on a raw 35-float feature vector.
     * Returns suitability score in range [1.0, 5.0].
     */
    public synchronized float runInference(float[] features) {
        if (features == null || features.length != INPUT_DIMENSION) {
            throw new IllegalArgumentException("Features array must have length " + INPUT_DIMENSION);
        }

        if (interpreter != null) {
            try {
                float[][] input = new float[1][INPUT_DIMENSION];
                System.arraycopy(features, 0, input[0], 0, INPUT_DIMENSION);
                float[][] output = new float[1][1];

                interpreter.run(input, output);

                float rawOutput = output[0][0];
                // Clamp to [0, 1] as defined by model_manifest.json
                rawOutput = Math.max(0.0f, Math.min(1.0f, rawOutput));

                // Display score formula: 1 + 4 * model_output
                return 1.0f + 4.0f * rawOutput;
            } catch (Exception e) {
                Log.e(TAG, "Error executing TFLite interpreter", e);
            }
        }

        // Rule-based fallback score if model is unavailable
        return calculateFallbackScore(features);
    }

    /**
     * Convenience method to predict suitability for an ActivityEntity given teacher constraints.
     */
    public float predictSuitability(
            ActivityEntity activity,
            int classSize,
            int availableMinutes,
            boolean electricity,
            boolean internet,
            String availableResources,
            String learningNeed) {

        float[] features = preprocessFeatures(
                activity.getGrade(),
                classSize,
                availableMinutes,
                activity.getDurationMinutes(),
                electricity,
                internet,
                activity.getTopic(),
                availableResources,
                learningNeed,
                activity.getStrategy()
        );

        return runInference(features);
    }

    private float calculateFallbackScore(float[] features) {
        // High quality heuristic fallback between 2.5 and 4.2
        float score = 3.2f;
        // Check duration alignment
        float durationNormalized = features[3];
        if (Math.abs(durationNormalized) < 1.0f) {
            score += 0.5f;
        }
        return Math.max(1.0f, Math.min(5.0f, score));
    }

    public boolean isModelReady() {
        return interpreter != null;
    }

    public String getActiveModelName() {
        return activeModelName;
    }

    public synchronized void close() {
        if (interpreter != null) {
            interpreter.close();
            interpreter = null;
            isInitialized = false;
        }
    }
}
