package ke.co.mwalimuai.ai;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.mediapipe.tasks.genai.llminference.LlmInference;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ke.co.mwalimuai.database.CurriculumEntity;
import ke.co.mwalimuai.generator.LessonGenerator;
import ke.co.mwalimuai.sne.InclusiveSNEPedagogyHelper;
import ke.co.mwalimuai.utils.CurriculumBreakdownHelper;

/**
 * On-Device Small Language Model (SLM) & Curriculum RAG Engine powered by Google MediaPipe GenAI.
 *
 * Uses Qwen2.5-0.5B-Instruct (qwen2.5-0.5b-instruct.task).
 */
public class OnDeviceSlmEngine {

    private static final String TAG = "OnDeviceSlmEngine";

    public static final String PRIMARY_MODEL_FILENAME = "qwen2.5-0.5b-instruct.task";
    public static final String QWEN_MODEL_DOWNLOAD_URL =
            "https://huggingface.co/litert-community/Qwen2.5-0.5B-Instruct/resolve/main/Qwen2.5-0.5B-Instruct_multi-prefill-seq_q8_ekv1280.task";
    public static final long QWEN_MODEL_SIZE_BYTES = 546660344L; // ~521 MB
    public static final String QWEN_MODEL_SIZE_DISPLAY = "521 MB";

    private static final List<String> SUPPORTED_MODEL_FILENAMES = Arrays.asList(
            PRIMARY_MODEL_FILENAME,
            "Qwen2.5-0.5B-Instruct_multi-prefill-seq_q8_ekv1280.task",
            "cbe_math_slm_int4.task",
            "gemma2-2b-it-cpu-int4.task",
            "qwen2.5-1.5b-instruct-int4.task",
            "llama-3.2-1b-instruct-int4.task"
    );

    private static volatile OnDeviceSlmEngine INSTANCE;
    private static boolean downloadPromptShownThisSession = false;
    private static volatile boolean isDownloading = false;

    private LlmInference llmInference;
    private String activeModelPath = null;
    private final ExecutorService downloadExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public static OnDeviceSlmEngine getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (OnDeviceSlmEngine.class) {
                if (INSTANCE == null) {
                    INSTANCE = new OnDeviceSlmEngine(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    private OnDeviceSlmEngine(Context context) {
        Context appCtx = context.getApplicationContext();
        downloadExecutor.execute(() -> initializeIfModelPresent(appCtx));
    }

    /**
     * Checks device storage for a quantized .task or .bin SLM file and initializes MediaPipe LlmInference.
     */
    public synchronized void initializeIfModelPresent(Context context) {
        if (llmInference != null) {
            return;
        }

        File modelFile = findOnDeviceModelFile(context);
        if (modelFile == null || !modelFile.exists()) {
            Log.i(TAG, "No on-device .task SLM found yet; ready for download or procedural synthesis.");
            return;
        }

        try {
            LlmInference.LlmInferenceOptions options = LlmInference.LlmInferenceOptions.builder()
                    .setModelPath(modelFile.getAbsolutePath())
                    .setMaxTokens(320)
                    .build();
            llmInference = LlmInference.createFromOptions(context, options);
            activeModelPath = modelFile.getAbsolutePath();
            Log.i(TAG, "Initialized On-Device Qwen2.5-0.5B SLM from: " + activeModelPath);
        } catch (Throwable t) {
            Log.w(TAG, "Could not initialize MediaPipe LlmInference from " + modelFile.getAbsolutePath() + ": " + t.getMessage());
            llmInference = null;
            activeModelPath = null;
        }
    }

    public static File findOnDeviceModelFile(Context context) {
        if (context == null) return null;

        File[] searchDirs = new File[]{
                context.getExternalFilesDir(null),
                context.getFilesDir()
        };

        for (File dir : searchDirs) {
            if (dir == null || !dir.exists()) continue;

            for (String name : SUPPORTED_MODEL_FILENAMES) {
                File candidate = new File(dir, name);
                if (candidate.exists() && candidate.isFile() && candidate.length() > 50L * 1024 * 1024) {
                    return candidate;
                }
            }

            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    String lower = f.getName().toLowerCase(Locale.ROOT);
                    if (f.isFile() && (lower.endsWith(".task") || lower.endsWith(".bin")) && f.length() > 50L * 1024 * 1024) {
                        return f;
                    }
                }
            }
        }
        return null;
    }

    public boolean isHardwareSlmReady() {
        return llmInference != null;
    }

    public static boolean isModelDownloaded(Context context) {
        return findOnDeviceModelFile(context) != null;
    }

    public String getActiveModelPath() {
        return activeModelPath;
    }

    public static String getActiveConnectionType(Context context) {
        if (context == null) return null;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) return null;
            Network activeNetwork = cm.getActiveNetwork();
            if (activeNetwork == null) return null;
            NetworkCapabilities caps = cm.getNetworkCapabilities(activeNetwork);
            if (caps == null) return null;
            if (!caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) return null;

            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return "Wi-Fi";
            } else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return "Mobile Data";
            } else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return "Ethernet";
            }
            return "Internet";
        } catch (Exception e) {
            return null;
        }
    }

    public void promptModelDownloadIfNeeded(Activity activity, boolean forceShowEvenIfDismissed) {
        if (activity == null || activity.isFinishing()) return;
        if (isModelDownloaded(activity)) {
            initializeIfModelPresent(activity.getApplicationContext());
            if (forceShowEvenIfDismissed) {
                Toast.makeText(activity, "Offline SNE AI Model is already downloaded and active!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        if (isDownloading) {
            Toast.makeText(activity, "Offline SNE AI download is already in progress...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (downloadPromptShownThisSession && !forceShowEvenIfDismissed) {
            return;
        }

        String connType = getActiveConnectionType(activity);
        if (connType == null) {
            if (forceShowEvenIfDismissed) {
                new AlertDialog.Builder(activity)
                        .setTitle("Offline Mode Active")
                        .setMessage("The full Offline SNE AI model (" + QWEN_MODEL_SIZE_DISPLAY + ") is not downloaded yet and no internet connection was detected.\n\n" +
                                "You can continue using the built-in Instant Lesson Synthesizer offline now, and download the " + QWEN_MODEL_SIZE_DISPLAY + " AI model whenever you connect to Wi-Fi or Mobile Data.")
                        .setPositiveButton("OK", null)
                        .show();
            }
            return;
        }

        downloadPromptShownThisSession = true;

        String dataNotice = "Wi-Fi".equals(connType)
                ? "You are currently connected via Wi-Fi."
                : "You are currently connected via Mobile Data (carrier data charges may apply).";

        new AlertDialog.Builder(activity)
                .setTitle("Download Offline SNE AI Model")
                .setMessage(dataNotice + "\n\n" +
                        "• Model: On-Device SNE AI Model\n" +
                        "• Download Size: " + QWEN_MODEL_SIZE_DISPLAY + " (One-time download)\n\n" +
                        "Once downloaded, the full SNE AI model runs 100% offline on your phone for free-form prompts and adaptive lesson generation.\n\n" +
                        "Would you like to download the " + QWEN_MODEL_SIZE_DISPLAY + " model now using " + connType + "?")
                .setCancelable(true)
                .setPositiveButton("Download (" + QWEN_MODEL_SIZE_DISPLAY + ")", (dialog, which) -> {
                    startModelDownloadWithProgress(activity);
                })
                .setNegativeButton("Later (Use Instant Mode)", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void startModelDownloadWithProgress(Activity activity) {
        if (isDownloading) return;
        isDownloading = true;

        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (24 * activity.getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad, pad, pad);

        ProgressBar progressBar = new ProgressBar(activity, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setIndeterminate(false);
        progressBar.setMax(100);
        progressBar.setProgress(0);

        TextView statusText = new TextView(activity);
        statusText.setText("Connecting to download Offline SNE AI (0 MB / " + QWEN_MODEL_SIZE_DISPLAY + ")...");
        statusText.setPadding(0, pad / 2, 0, 0);

        layout.addView(progressBar);
        layout.addView(statusText);

        AlertDialog progressDialog = new AlertDialog.Builder(activity)
                .setTitle("Downloading Offline SNE AI Model")
                .setView(layout)
                .setCancelable(false)
                .setNegativeButton("Cancel", (d, w) -> {
                    isDownloading = false;
                    d.dismiss();
                })
                .create();
        progressDialog.show();

        final Context appCtx = activity.getApplicationContext();

        downloadExecutor.execute(() -> {
            File targetDir = appCtx.getExternalFilesDir(null);
            if (targetDir == null) {
                targetDir = appCtx.getFilesDir();
            }
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
            File tempFile = new File(targetDir, PRIMARY_MODEL_FILENAME + ".part");
            File finalFile = new File(targetDir, PRIMARY_MODEL_FILENAME);

            HttpURLConnection conn = null;
            try {
                URL url = new URL(QWEN_MODEL_DOWNLOAD_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setInstanceFollowRedirects(true);
                conn.setConnectTimeout(20000);
                conn.setReadTimeout(30000);
                conn.connect();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP
                        || responseCode == HttpURLConnection.HTTP_MOVED_PERM
                        || responseCode == HttpURLConnection.HTTP_SEE_OTHER
                        || responseCode == 307 || responseCode == 308) {
                    String newUrl = conn.getHeaderField("Location");
                    conn.disconnect();
                    conn = (HttpURLConnection) new URL(newUrl).openConnection();
                    conn.setConnectTimeout(20000);
                    conn.setReadTimeout(30000);
                    conn.connect();
                }

                long totalBytes = conn.getContentLengthLong();
                if (totalBytes <= 0) {
                    totalBytes = QWEN_MODEL_SIZE_BYTES;
                }

                try (BufferedInputStream in = new BufferedInputStream(conn.getInputStream(), 65536);
                     FileOutputStream out = new FileOutputStream(tempFile)) {

                    byte[] buffer = new byte[65536];
                    long downloaded = 0;
                    int count;
                    long lastUiUpdate = 0;

                    while ((count = in.read(buffer)) != -1) {
                        if (!isDownloading) {
                            tempFile.delete();
                            return;
                        }
                        out.write(buffer, 0, count);
                        downloaded += count;

                        long now = System.currentTimeMillis();
                        if (now - lastUiUpdate > 250) {
                            lastUiUpdate = now;
                            int pct = (int) ((downloaded * 100L) / totalBytes);
                            long dlMb = downloaded / (1024L * 1024L);
                            long totMb = totalBytes / (1024L * 1024L);
                            mainHandler.post(() -> {
                                if (progressDialog.isShowing()) {
                                    progressBar.setProgress(pct);
                                    statusText.setText(String.format(Locale.US,
                                            "Downloaded %d MB / %d MB (%d%%)\nSaving for 100%% offline use...",
                                            dlMb, totMb, pct));
                                }
                            });
                        }
                    }
                    out.flush();
                }

                if (finalFile.exists()) {
                    finalFile.delete();
                }
                boolean renamed = tempFile.renameTo(finalFile);
                if (!renamed) {
                    throw new Exception("Could not finalize downloaded model file.");
                }

                isDownloading = false;
                initializeIfModelPresent(appCtx);

                mainHandler.post(() -> {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(appCtx,
                            "Offline SNE AI Model downloaded and activated!",
                            Toast.LENGTH_LONG).show();
                });

            } catch (Exception e) {
                isDownloading = false;
                if (tempFile.exists()) {
                    tempFile.delete();
                }
                Log.e(TAG, "Error downloading Qwen2.5-0.5B model", e);
                mainHandler.post(() -> {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (!activity.isFinishing()) {
                        new AlertDialog.Builder(activity)
                                .setTitle("Download Interrupted")
                                .setMessage("Could not complete the model download (" + e.getMessage() + ").\n\n" +
                                        "You can continue using Instant Offline Mode and retry the download anytime from the Home or AI Prompt screen.")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                });
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        });
    }

    /**
     * Builds the chat-formatted KICD SNE Curriculum RAG prompt for on-device AI generation.
     * Instructs the model to freely adapt practical visual/tactile classroom steps
     * directly around the teacher's exact materials without rigid boilerplate.
     */
    public String buildCurriculumRagPrompt(
            CurriculumEntity curr,
            LessonGenerator.LessonPlanningParameters params,
            String scenarioTitle,
            String synthesizedMathExample) {

        CurriculumBreakdownHelper.SubStrandBreakdown breakdown = CurriculumBreakdownHelper.getBreakdown(curr);
        String strand = curr != null && curr.getStrand() != null ? curr.getStrand() : "Numbers";
        String subStrand = curr != null && curr.getSubStrand() != null ? curr.getSubStrand() : "Whole numbers";
        String pages = curr != null && curr.getSourcePdfPages() != null ? curr.getSourcePdfPages() : "13-17";
        InclusiveSNEPedagogyHelper.SNEProfile profile = InclusiveSNEPedagogyHelper.detectProfile(params.learningNeed);

        StringBuilder prompt = new StringBuilder();
        prompt.append("<|im_start|>system\n");
        if (profile == InclusiveSNEPedagogyHelper.SNEProfile.DEAF_HARD_OF_HEARING) {
            prompt.append("You are an expert Special Needs Education (SNE) teacher for Kenyan KICD Grade 6 CBE Mathematics for Learners with Hearing Impairment (Deaf / KSL). ");
            prompt.append("All instruction is 100% visual and uses Kenya Sign Language (KSL). No spoken or auditory activities exist. ");
            prompt.append("Freely adapt the lesson: show how deaf learners visually and physically manipulate the teacher's exact materials (")
                    .append(params.availableResources).append(") using KSL signs, desk action cards, and visual realia to model and solve the math problem. Be creative, practical, and direct. Do not use fixed templates.<|im_end|>\n");
        } else if (profile == InclusiveSNEPedagogyHelper.SNEProfile.BLIND_LOW_VISION) {
            prompt.append("You are an expert Special Needs Education (SNE) teacher for Kenyan KICD Grade 6 CBE Mathematics for Learners with Visual Impairment (Blind / Low Vision). ");
            prompt.append("All instruction is tactile, kinesthetic, and audio-described. Freely adapt the lesson: show how blind learners touch, count, and physically manipulate the teacher's exact materials (")
                    .append(params.availableResources).append(") using knots, raised edges, and tactile landmarks to solve the math problem. Do not use fixed templates.<|im_end|>\n");
        } else {
            prompt.append("You are an expert Special Needs Education (SNE) teacher for Kenyan KICD Grade 6 CBE Mathematics adapting for ")
                    .append(InclusiveSNEPedagogyHelper.getProfileTargetName(profile)).append(". ");
            prompt.append("Freely adapt the lesson: show how learners physically manipulate the teacher's exact materials (")
                    .append(params.availableResources).append(") using concrete, differentiated, and inclusive steps to solve the math problem. Do not use fixed templates.<|im_end|>\n");
        }

        prompt.append("<|im_start|>user\n");
        prompt.append("SNE Target: ").append(InclusiveSNEPedagogyHelper.getProfileTitle(profile)).append("\n");
        prompt.append("Grade 6 SNE Mathematics Topic: ").append(strand).append(" — ").append(subStrand).append(" (KICD SNE Curriculum pp. ").append(pages).append(")\n");
        if (breakdown != null && !breakdown.learningOutcomes.isEmpty()) {
            prompt.append("Curriculum Focus: ").append(breakdown.learningOutcomes.get(0)).append("\n");
        }
        prompt.append("Materials Available: ").append(params.availableResources).append("\n");
        prompt.append("Practical Context: ").append(scenarioTitle).append("\n");
        prompt.append("Math Problem: ").append(synthesizedMathExample).append("\n");
        if (params.customActivityOrHook != null && !params.customActivityOrHook.trim().isEmpty()) {
            prompt.append("Teacher Notes: ").append(params.customActivityOrHook.trim()).append("\n");
        }
        prompt.append("\nFreely generate 3 practical, step-by-step classroom actions (1., 2., 3.) showing how learners in pods physically use ")
                .append(params.availableResources)
                .append(" to model and solve the problem, fully adapted for this SNE profile:<|im_end|>\n<|im_start|>assistant\n");

        return prompt.toString();
    }

    /**
     * Runs on-device AI inference for lesson concrete manipulation steps.
     */
    public synchronized String generateRagResponse(
            Context context,
            CurriculumEntity curr,
            LessonGenerator.LessonPlanningParameters params,
            String scenarioTitle,
            String synthesizedMathExample) {
        if (llmInference == null && context != null && isModelDownloaded(context)) {
            initializeIfModelPresent(context.getApplicationContext());
        }
        if (llmInference == null) {
            return null;
        }
        try {
            String prompt = buildCurriculumRagPrompt(curr, params, scenarioTitle, synthesizedMathExample);
            Log.i(TAG, "Prompting on-device AI for sub-strand " + (curr != null ? curr.getSubStrand() : "") + " with materials: " + params.availableResources);
            long t0 = System.currentTimeMillis();
            String response = llmInference.generateResponse(prompt);
            Log.i(TAG, "On-device AI responded in " + (System.currentTimeMillis() - t0) + " ms");
            if (response != null && !response.trim().isEmpty()) {
                return cleanModelOutput(response);
            }
        } catch (Throwable t) {
            Log.w(TAG, "On-device AI inference failed, using SNE Synthesizer: " + t.getMessage());
        }
        return null;
    }

    /**
     * Runs free-form prompt inference on the on-device model when loaded.
     */
    public synchronized String generateFreeFormSlmResponse(
            Context context,
            String userFreeFormPrompt,
            String topicName,
            String materials,
            String scenarioTitle,
            String workedProblem) {
        if (llmInference == null && context != null && isModelDownloaded(context)) {
            initializeIfModelPresent(context.getApplicationContext());
        }
        if (llmInference == null) {
            return null;
        }
        try {
            InclusiveSNEPedagogyHelper.SNEProfile profile = InclusiveSNEPedagogyHelper.detectProfile(userFreeFormPrompt);
            StringBuilder prompt = new StringBuilder();
            prompt.append("<|im_start|>system\n");
            prompt.append("You are an offline AI assistant for Kenyan Grade 6 CBE Special Needs Education (SNE) Mathematics teachers. ");
            if (profile == InclusiveSNEPedagogyHelper.SNEProfile.DEAF_HARD_OF_HEARING) {
                prompt.append("Focus is Hearing Impairment (Deaf / KSL): all communication is visual and Kenya Sign Language (KSL), zero auditory/spoken actions. ");
            } else if (profile == InclusiveSNEPedagogyHelper.SNEProfile.BLIND_LOW_VISION) {
                prompt.append("Focus is Visual Impairment (Blind / Tactile): all communication is tactile, kinesthetic, and audio-described. ");
            }
            prompt.append("Always build the scenario and steps strictly around the teacher's exact materials (").append(materials).append(").<|im_end|>\n");
            prompt.append("<|im_start|>user\n");
            prompt.append("Teacher Prompt: ").append(userFreeFormPrompt.trim()).append("\n");
            prompt.append("SNE Target: ").append(InclusiveSNEPedagogyHelper.getProfileTitle(profile)).append("\n");
            prompt.append("Matched KICD Topic: ").append(topicName).append("\n");
            prompt.append("Materials to Use: ").append(materials).append("\n");
            prompt.append("Scenario: ").append(scenarioTitle).append("\n");
            prompt.append("Worked Math Problem: ").append(workedProblem).append("\n\n");
            prompt.append("Freely generate 3 clear numbered steps showing how the teacher and learners physically use ")
                    .append(materials)
                    .append(" in this scenario to teach and solve ").append(topicName).append(" adapted for this SNE profile:<|im_end|>\n<|im_start|>assistant\n");

            long t0 = System.currentTimeMillis();
            String response = llmInference.generateResponse(prompt.toString());
            Log.i(TAG, "On-device Free-Form AI responded in " + (System.currentTimeMillis() - t0) + " ms");
            if (response != null && !response.trim().isEmpty()) {
                return cleanModelOutput(response);
            }
        } catch (Throwable t) {
            Log.w(TAG, "Free-form AI generation failed: " + t.getMessage());
        }
        return null;
    }

    private String cleanModelOutput(String raw) {
        if (raw == null) return "";
        return raw.replace("<|im_end|>", "")
                .replace("<|endoftext|>", "")
                .replaceAll("(?i)\\b(qwen2?\\.5?(-0\\.5b)?(-instruct)?|slm|large language model|small language model)\\b", "Offline SNE AI")
                .trim();
    }
}
