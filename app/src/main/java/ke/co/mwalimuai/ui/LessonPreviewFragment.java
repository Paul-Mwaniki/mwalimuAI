package ke.co.mwalimuai.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.io.File;

import ke.co.mwalimuai.R;
import ke.co.mwalimuai.audio.OfflineAudioAssistant;
import ke.co.mwalimuai.database.LessonEntity;
import ke.co.mwalimuai.databinding.FragmentLessonPreviewBinding;
import ke.co.mwalimuai.viewmodel.LessonViewModel;

public class LessonPreviewFragment extends Fragment {

    private FragmentLessonPreviewBinding binding;
    private LessonViewModel viewModel;
    private LessonEntity activeLesson;
    private OfflineAudioAssistant audioAssistant;
    private int currentAudioStage = 0; // 0=Intro, 1=Development/Step3, 2=Assessment/Rubric, 3=SNE Inclusion
    private static final String[] STAGE_NAMES = {
            "Intro & Hook",
            "Hands-On Development",
            "Assessment & Rubrics",
            "SNE & UDL Inclusion"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLessonPreviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(LessonViewModel.class);

        viewModel.getCurrentLesson().observe(getViewLifecycleOwner(), lesson -> {
            if (lesson != null) {
                activeLesson = lesson;
                displayLesson(lesson);
            }
        });

        // Register system back button callback
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new androidx.activity.OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        NavController navController = Navigation.findNavController(requireView());
                        if (!navController.popBackStack()) {
                            navController.navigate(R.id.action_preview_to_planner);
                        }
                    }
                });

        // Navigation Top Back Button
        binding.btnReturnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            if (!navController.popBackStack()) {
                navController.navigate(R.id.action_preview_to_planner);
            }
        });

        // Bottom Navigation Buttons
        binding.btnBottomPlanAnother.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            if (!navController.popBackStack(R.id.navigation_planner, false)) {
                navController.navigate(R.id.action_preview_to_planner);
            }
        });

        binding.btnBottomGoToSaved.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_preview_to_saved);
        });

        binding.btnSaveLesson.setOnClickListener(v -> {
            if (activeLesson != null) {
                viewModel.saveCurrentLesson();
                binding.btnSaveLesson.setText("Saved ✓");
                binding.btnSaveLesson.setEnabled(false);
                com.google.android.material.snackbar.Snackbar.make(
                        binding.getRoot(),
                        "Lesson Plan saved to local Room database!",
                        com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                ).setAction("View Saved", viewSaved -> {
                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(R.id.action_preview_to_saved);
                }).show();
            }
        });

        binding.btnExportWord.setOnClickListener(v -> exportWord());
        binding.btnExportPdf.setOnClickListener(v -> exportPdf());

        setupAudioAssistant();
    }

    private void setupAudioAssistant() {
        audioAssistant = new OfflineAudioAssistant(requireContext(), () -> {
            if (binding != null && getActivity() != null) {
                getActivity().runOnUiThread(() ->
                        binding.tvAudioStatus.setText("Stage: " + STAGE_NAMES[currentAudioStage]));
            }
        });

        binding.btnAudioPlayPause.setOnClickListener(v -> {
            if (audioAssistant == null) return;
            if (audioAssistant.isSpeaking()) {
                audioAssistant.stop();
                binding.btnAudioPlayPause.setText("🔊 Listen (TTS)");
                binding.tvAudioStatus.setText("Audio Paused");
            } else {
                playCurrentStageAudio();
            }
        });

        binding.btnAudioStageSkip.setOnClickListener(v -> {
            currentAudioStage = (currentAudioStage + 1) % STAGE_NAMES.length;
            binding.tvAudioStatus.setText("Selected: " + STAGE_NAMES[currentAudioStage]);
            if (audioAssistant != null && audioAssistant.isSpeaking()) {
                playCurrentStageAudio();
            }
        });

        binding.btnAudioSpeed.setOnClickListener(v -> {
            if (audioAssistant == null) return;
            float current = audioAssistant.getSpeechRate();
            float next;
            String label;
            if (current <= 0.85f) {
                next = 1.0f;
                label = "1.0x";
            } else if (current <= 1.05f) {
                next = 1.25f;
                label = "1.2x";
            } else {
                next = 0.8f;
                label = "0.8x";
            }
            audioAssistant.setSpeechRate(next);
            binding.btnAudioSpeed.setText(label);
        });
    }

    private void playCurrentStageAudio() {
        if (activeLesson == null || audioAssistant == null) return;
        String textToSpeak = getStageText(currentAudioStage);
        if (textToSpeak.trim().isEmpty()) {
            Toast.makeText(requireContext(), "No text available for this stage", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnAudioPlayPause.setText("⏸ Pause");
        binding.tvAudioStatus.setText("Playing: " + STAGE_NAMES[currentAudioStage]);

        audioAssistant.speak(textToSpeak, new OfflineAudioAssistant.AudioListener() {
            @Override
            public void onAudioStarted() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (binding != null) {
                            binding.btnAudioPlayPause.setText("⏸ Pause");
                            binding.tvAudioStatus.setText("Playing: " + STAGE_NAMES[currentAudioStage]);
                        }
                    });
                }
            }

            @Override
            public void onAudioCompleted() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (binding != null) {
                            binding.btnAudioPlayPause.setText("🔊 Listen (TTS)");
                            binding.tvAudioStatus.setText(STAGE_NAMES[currentAudioStage] + " • Done");
                        }
                    });
                }
            }

            @Override
            public void onAudioError(String message) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (binding != null) {
                            binding.btnAudioPlayPause.setText("🔊 Listen (TTS)");
                            binding.tvAudioStatus.setText("Audio ready");
                        }
                    });
                }
            }
        });
    }

    private String getStageText(int stage) {
        if (activeLesson == null) return "";
        switch (stage) {
            case 0:
                return "Lesson Title: " + activeLesson.getLessonTitle() + ". " + activeLesson.getIntroduction();
            case 1:
                return "Hands-On Lesson Development: " + activeLesson.getLessonDevelopment();
            case 2:
                return "Formative Assessment and Rubric: " + activeLesson.getAssessmentMethods();
            case 3:
                return "Special Needs Education and Universal Design: " + activeLesson.getInclusionNotes();
            default:
                return activeLesson.getIntroduction();
        }
    }

    private void displayLesson(LessonEntity lesson) {
        binding.tvPreviewLessonTitle.setText(lesson.getLessonTitle());
        binding.tvPreviewStrandMeta.setText("Grade " + lesson.getGrade() + " • " + lesson.getLearningArea() +
                "  |  Strand: " + lesson.getStrand() + "  |  Sub-strand: " + lesson.getSubStrand());
        int squads = Math.max(2, Math.round(lesson.getClassSize() / 5.0f));
        binding.tvPreviewClassMeta.setText("Duration: " + lesson.getDurationMinutes() + " Minutes  |  Class Roll: " +
                lesson.getClassSize() + " Learners (" + squads + " Squads)");

        binding.tvPreviewObjective.setText(lesson.getLearningObjective());
        binding.tvPreviewResources.setText(lesson.getResourcesNeeded());
        binding.tvPreviewIntro.setText(lesson.getIntroduction());
        binding.tvPreviewDevelopment.setText(lesson.getLessonDevelopment());
        binding.tvPreviewAssessment.setText(lesson.getAssessmentMethods());
        binding.tvPreviewInclusion.setText(lesson.getLargeClassAdaptation() + "\n" + lesson.getInclusionNotes());
        binding.tvPreviewReflection.setText(lesson.getReflectionQuestion());
    }

    private void exportWord() {
        if (activeLesson == null) return;
        Toast.makeText(requireContext(), "Generating Word document (.docx)...", Toast.LENGTH_SHORT).show();

        viewModel.exportToWord(requireContext(), activeLesson, new LessonViewModel.ExportCallback() {
            @Override
            public void onSuccess(File file) {
                requireActivity().runOnUiThread(() -> shareFile(file, "application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
            }

            @Override
            public void onError(Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Error exporting Word: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    private void exportPdf() {
        if (activeLesson == null) return;
        Toast.makeText(requireContext(), "Generating PDF document...", Toast.LENGTH_SHORT).show();

        viewModel.exportToPdf(requireContext(), activeLesson, new LessonViewModel.ExportCallback() {
            @Override
            public void onSuccess(File file) {
                requireActivity().runOnUiThread(() -> shareFile(file, "application/pdf"));
            }

            @Override
            public void onError(Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Error exporting PDF: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    private void shareFile(File file, String mimeType) {
        try {
            Uri contentUri = FileProvider.getUriForFile(
                    requireContext(),
                    "ke.co.mwalimuai.fileprovider",
                    file
            );

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType(mimeType);
            intent.putExtra(Intent.EXTRA_STREAM, contentUri);
            intent.putExtra(Intent.EXTRA_SUBJECT, activeLesson != null ? activeLesson.getLessonTitle() : "Lesson Plan");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Share Lesson Document"));
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Document ready at: " + file.getName(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (audioAssistant != null) {
            audioAssistant.shutdown();
            audioAssistant = null;
        }
        binding = null;
    }
}
