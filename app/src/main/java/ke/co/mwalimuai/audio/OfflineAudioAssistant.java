package ke.co.mwalimuai.audio;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.Locale;

/**
 * 100% Offline Hands-Free Audio Assistant for teachers and visually impaired educators.
 * Uses Android's built-in native Text-to-Speech (TTS) engine with zero internet usage.
 */
public class OfflineAudioAssistant {

    private static final String TAG = "OfflineAudioAssistant";

    public interface AudioListener {
        void onAudioStarted();
        void onAudioCompleted();
        void onAudioError(String message);
    }

    private TextToSpeech tts;
    private boolean isInitialized = false;
    private boolean isSpeaking = false;
    private AudioListener currentListener;
    private float currentSpeechRate = 1.0f;

    public OfflineAudioAssistant(Context context, Runnable onReady) {
        tts = new TextToSpeech(context.getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                int res = tts.setLanguage(Locale.UK);
                if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts.setLanguage(Locale.US);
                }
                tts.setSpeechRate(currentSpeechRate);
                isInitialized = true;
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        isSpeaking = true;
                        if (currentListener != null) currentListener.onAudioStarted();
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        isSpeaking = false;
                        if (currentListener != null) currentListener.onAudioCompleted();
                    }

                    @Override
                    public void onError(String utteranceId) {
                        isSpeaking = false;
                        if (currentListener != null) currentListener.onAudioError("Audio playback error");
                    }
                });
                if (onReady != null) onReady.run();
                Log.i(TAG, "Offline Audio Assistant (TTS) initialized successfully.");
            } else {
                Log.w(TAG, "Could not initialize offline TextToSpeech engine (status=" + status + ")");
            }
        });
    }

    public boolean isReady() {
        return isInitialized && tts != null;
    }

    public boolean isSpeaking() {
        return isSpeaking || (tts != null && tts.isSpeaking());
    }

    public void setSpeechRate(float rate) {
        this.currentSpeechRate = rate;
        if (tts != null) {
            tts.setSpeechRate(rate);
        }
    }

    public float getSpeechRate() {
        return currentSpeechRate;
    }

    public void speak(String text, AudioListener listener) {
        if (!isReady() || text == null || text.trim().isEmpty()) {
            if (listener != null) listener.onAudioError("Audio engine not ready or text is empty");
            return;
        }
        this.currentListener = listener;
        stop();

        // Clean out formatting dividers so TTS reads naturally
        String cleaned = text
                .replace("────────────────────────────────────────", "")
                .replace("▪", "")
                .replace("•", "")
                .replace("★", "")
                .replace("🤖", "")
                .replace("👉", "")
                .replace("⏱", "")
                .replace("📋", "")
                .replace("½", "half")
                .replace("¼", "one quarter")
                .replace("cm²", "square centimetres")
                .replace("m²", "square metres")
                .replace("cm³", "cubic centimetres")
                .replace("KES", "Kenyan Shillings")
                .trim();

        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MWALIMU_LESSON_UTTERANCE");
        tts.speak(cleaned, TextToSpeech.QUEUE_FLUSH, params, "MWALIMU_LESSON_UTTERANCE");
    }

    public void stop() {
        if (tts != null) {
            tts.stop();
        }
        isSpeaking = false;
    }

    public void shutdown() {
        stop();
        if (tts != null) {
            tts.shutdown();
            tts = null;
        }
        isInitialized = false;
    }
}
