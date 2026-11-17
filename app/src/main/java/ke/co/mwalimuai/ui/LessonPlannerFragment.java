package ke.co.mwalimuai.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ke.co.mwalimuai.R;
import ke.co.mwalimuai.database.CurriculumDao;
import ke.co.mwalimuai.database.CurriculumEntity;
import ke.co.mwalimuai.database.MwalimuDatabase;
import ke.co.mwalimuai.databinding.FragmentLessonPlannerBinding;
import ke.co.mwalimuai.generator.LessonGenerator;
import ke.co.mwalimuai.viewmodel.LessonViewModel;

public class LessonPlannerFragment extends Fragment {

    private FragmentLessonPlannerBinding binding;
    private LessonViewModel viewModel;
    private final List<CurriculumEntity> curriculumList = new ArrayList<>();
    private final List<String> subStrandTitles = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private boolean isGeneratingInitiated = false;
    private String selectedSNEProfile = "Hearing Impairment / Deaf (KSL Sign Glosses & Visual Desk Cards)";

    /**
     * ArrayAdapter that never filters out non-matching items when an AutoCompleteTextView
     * already contains text. Ensures every dropdown always shows 100% of its options,
     * including when returning via "Plan Another".
     */
    private static class UnfilteredArrayAdapter extends ArrayAdapter<String> {
        private final List<String> allItems;
        private final Filter noOpFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                results.values = allItems;
                results.count = allItems.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }
        };

        public UnfilteredArrayAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
            super(context, resource, objects);
            this.allItems = objects;
        }

        @NonNull
        @Override
        public Filter getFilter() {
            return noOpFilter;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLessonPlannerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(LessonViewModel.class);

        Bundle args = getArguments();
        String incomingSNE = args != null ? args.getString("selectedSNE") : null;
        if (incomingSNE != null && !incomingSNE.isEmpty()) {
            selectedSNEProfile = incomingSNE;
        }
        updateSNEBanner(selectedSNEProfile);
        loadCurriculumEntities();

        binding.btnGeneratePlan.setOnClickListener(v -> generatePlan());
        binding.btnViewSavedLessons.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_planner_to_saved));

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new androidx.activity.OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (Boolean.TRUE.equals(viewModel.getIsGenerating().getValue())) {
                            Toast.makeText(requireContext(), "Developing lesson plan... Please wait.", Toast.LENGTH_SHORT).show();
                        } else {
                            setEnabled(false);
                            requireActivity().getOnBackPressedDispatcher().onBackPressed();
                        }
                    }
                });

        viewModel.getIsGenerating().observe(getViewLifecycleOwner(), isGenerating -> {
            boolean generating = Boolean.TRUE.equals(isGenerating);
            binding.layoutGeneratingOverlay.setVisibility(generating ? View.VISIBLE : View.GONE);
            binding.btnGeneratePlan.setEnabled(!generating);

            if (!generating && isGeneratingInitiated) {
                navigateToPreviewIfReady();
            }
        });

        viewModel.getCurrentLesson().observe(getViewLifecycleOwner(), lesson -> {
            if (lesson != null && isGeneratingInitiated) {
                navigateToPreviewIfReady();
            }
        });
    }

    private void updateSNEBanner(String sneProfile) {
        if (binding == null) return;
        String lower = sneProfile != null ? sneProfile.toLowerCase(Locale.ROOT) : "";
        if (lower.contains("deaf") || lower.contains("hearing") || lower.contains("ksl")) {
            binding.tvActiveSNEIcon.setText("👂");
            binding.tvActiveSNETitle.setText("Hearing Impairment / Deaf (KSL)");
            binding.tvActiveSNESubtitle.setText("KICD Grade 6 SNE: KSL Signs, Visual Pods & 3-Color Desk Cards");
        } else if (lower.contains("blind") || lower.contains("low vision") || lower.contains("visual impair")) {
            binding.tvActiveSNEIcon.setText("👁️");
            binding.tvActiveSNETitle.setText("Visual Impairment / Blind (Tactile)");
            binding.tvActiveSNESubtitle.setText("Tactile Knotted Ropes, Embossed Boundaries & Audio Guide");
        } else if (lower.contains("dyslexi") || lower.contains("reading")) {
            binding.tvActiveSNEIcon.setText("📖");
            binding.tvActiveSNETitle.setText("Dyslexia & Reading Difficulties");
            binding.tvActiveSNESubtitle.setText("Visual Storyboards, Color-Coded Cues & Reduced Text");
        } else if (lower.contains("dyscalculi") || lower.contains("number struggle")) {
            binding.tvActiveSNEIcon.setText("🧮");
            binding.tvActiveSNETitle.setText("Dyscalculia (Math Difficulties)");
            binding.tvActiveSNESubtitle.setText("Concrete CPA Progression, Dot Math & Ten-Frames");
        } else if (lower.contains("adhd") || lower.contains("focus") || lower.contains("attention")) {
            binding.tvActiveSNEIcon.setText("⚡");
            binding.tvActiveSNETitle.setText("ADHD & Attention Focus");
            binding.tvActiveSNESubtitle.setText("3-Minute Micro-Intervals & Active Kinesthetic Roles");
        } else if (lower.contains("motor") || lower.contains("physical")) {
            binding.tvActiveSNEIcon.setText("♿");
            binding.tvActiveSNETitle.setText("Motor & Physical Challenges");
            binding.tvActiveSNESubtitle.setText("Adapted Manipulative Grips & Peer Scribe Protocol");
        } else {
            binding.tvActiveSNEIcon.setText("🌟");
            binding.tvActiveSNETitle.setText("Universal Inclusive SNE");
            binding.tvActiveSNESubtitle.setText("Multi-Tier Differentiated Scaffolding");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Re-bind unfiltered adapters on resume so all dropdown options remain available after "Plan Another"
        bindDropdownAdapters();
    }

    private void navigateToPreviewIfReady() {
        if (!isGeneratingInitiated) return;
        if (Boolean.TRUE.equals(viewModel.getIsGenerating().getValue())) return;
        if (viewModel.getCurrentLesson().getValue() == null) return;

        isGeneratingInitiated = false;
        try {
            if (getView() != null) {
                androidx.navigation.NavController controller = Navigation.findNavController(requireView());
                if (controller.getCurrentDestination() != null &&
                        controller.getCurrentDestination().getId() == R.id.navigation_planner) {
                    controller.navigate(R.id.action_planner_to_preview);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindDropdownAdapters() {
        if (getContext() == null || binding == null) return;
        if (!subStrandTitles.isEmpty()) {
            UnfilteredArrayAdapter subStrandAdapter = new UnfilteredArrayAdapter(
                    requireContext(), android.R.layout.simple_dropdown_item_1line, subStrandTitles);
            binding.actvSubStrand.setAdapter(subStrandAdapter);
        }
    }

    private void loadCurriculumEntities() {
        executor.execute(() -> {
            CurriculumDao dao = MwalimuDatabase.getInstance(requireContext()).curriculumDao();
            List<CurriculumEntity> list = dao.getAllCurriculum();
            curriculumList.clear();
            curriculumList.addAll(list);

            List<String> titles = new ArrayList<>();
            for (CurriculumEntity c : list) {
                titles.add(c.getSubStrandCode() + " " + c.getSubStrand() + " (" + c.getStrand() + ")");
            }

            requireActivity().runOnUiThread(() -> {
                if (getContext() == null || binding == null) return;
                subStrandTitles.clear();
                subStrandTitles.addAll(titles);

                UnfilteredArrayAdapter subStrandAdapter = new UnfilteredArrayAdapter(
                        requireContext(), android.R.layout.simple_dropdown_item_1line, subStrandTitles);
                binding.actvSubStrand.setAdapter(subStrandAdapter);

                // Handle incoming pre-selected sub-strand
                Bundle args = getArguments();
                String preselected = args != null ? args.getString("selectedSubStrand") : null;
                if (preselected != null) {
                    for (String title : subStrandTitles) {
                        if (title.toLowerCase().contains(preselected.toLowerCase())) {
                            binding.actvSubStrand.setText(title, false);
                            return;
                        }
                    }
                }

                if (binding.actvSubStrand.getText().toString().isEmpty() && !subStrandTitles.isEmpty()) {
                    binding.actvSubStrand.setText(subStrandTitles.get(0), false);
                }
            });
        });
    }

    private void hideKeyboard() {
        try {
            if (getActivity() != null && binding != null) {
                android.view.inputmethod.InputMethodManager imm =
                        (android.view.inputmethod.InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null && getView() != null) {
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                }
            }
        } catch (Exception ignored) {}
    }

    private void generatePlan() {
        hideKeyboard();

        String subStrandText = binding.actvSubStrand.getText() != null
                ? binding.actvSubStrand.getText().toString() : "";
        if (subStrandText.isEmpty() || curriculumList.isEmpty()) {
            Toast.makeText(requireContext(), "Please select a curriculum topic", Toast.LENGTH_SHORT).show();
            return;
        }

        // Find selected CurriculumEntity
        CurriculumEntity selectedCurr = curriculumList.get(0);
        for (CurriculumEntity c : curriculumList) {
            if (subStrandText.contains(c.getSubStrand())) {
                selectedCurr = c;
                break;
            }
        }

        int duration = 40;
        try {
            duration = Integer.parseInt(binding.etDuration.getText().toString().trim());
        } catch (Exception ignored) {}

        int classSize = 48;
        try {
            classSize = Integer.parseInt(binding.etClassSize.getText().toString().trim());
        } catch (Exception ignored) {}

        String customHook = binding.etCustomHook.getText() != null
                ? binding.etCustomHook.getText().toString().trim() : "";

        LessonGenerator.LessonPlanningParameters params = new LessonGenerator.LessonPlanningParameters();
        params.curriculum = selectedCurr;
        params.durationMinutes = duration;
        params.classSize = classSize;
        params.availableResources = "chalkboard visual diagrams and geometric representations";
        params.learningNeed = selectedSNEProfile;
        params.customActivityOrHook = customHook;

        isGeneratingInitiated = true;
        viewModel.generateLessonPlan(params);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
