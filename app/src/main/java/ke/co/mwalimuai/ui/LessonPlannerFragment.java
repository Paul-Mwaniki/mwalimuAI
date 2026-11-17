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

    private final String[] resourceOptions = {
            "paper and chalkboard",
            "bottle tops and paper",
            "stones and chalkboard",
            "ruler and paper",
            "number cards and counters",
            " sticks, strings and cartons",
            "no extra materials",
            "Custom / Add Own Materials..."
    };

    private final String[] learningNeedOptions = {
            "Universal Inclusive (Mixed Abilities & SNE Scaffolding)",
            "Visual Impairment / Blind (Tactile Knotted Ropes & Audio Guide)",
            "Hearing Impairment / Deaf (KSL Sign Glosses & Visual Desk Cards)",
            "Dyslexia & Reading Difficulties (Symbolic Storyboards & Color Cues)",
            "Dyscalculia (Concrete CPA, Dot Math & Ten-Frames)",
            "ADHD & Attention Focus (3-Min Micro-Intervals & Kinesthetic Roles)",
            "Motor / Physical Challenges (Adapted Grips & Peer Scribing)",
            "Large Inclusive Class (45+ Learners with SNE Squad Roles)"
    };

    private final String[] strategyOptions = {
            "Recommended Best Fit (Auto-Select)",
            "Manipulative exploration (Hands-on)",
            "Think-pair-share (Peer Discussion)",
            "Learning game (Classroom Game)",
            "Market role-play (Real-world simulation)",
            "Station rotation (Squad Zones)",
            "Custom Activity / Hook"
    };

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

        setupDropdowns();
        loadCurriculumEntities();

        binding.btnGeneratePlan.setOnClickListener(v -> generatePlan());
        binding.btnGenerateFreeForm.setOnClickListener(v -> generateFromFreeFormPrompt());
        binding.btnViewSavedLessons.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_planner_to_saved));

        // Allow pressing Enter / Send on the keyboard inside the Free-Form Prompt box
        binding.etFreeFormPrompt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEND
                    || actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE
                    || actionId == android.view.inputmethod.EditorInfo.IME_ACTION_GO
                    || (event != null && event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER
                    && event.getAction() == android.view.KeyEvent.ACTION_DOWN)) {
                generateFromFreeFormPrompt();
                return true;
            }
            return false;
        });

        binding.etFreeFormPrompt.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == android.view.KeyEvent.KEYCODE_ENTER
                    && event.getAction() == android.view.KeyEvent.ACTION_DOWN) {
                generateFromFreeFormPrompt();
                return true;
            }
            return false;
        });

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

    private void setupDropdowns() {
        bindDropdownAdapters();

        if (binding.actvResources.getText().toString().isEmpty()) {
            binding.actvResources.setText(resourceOptions[0], false);
        }
        if (binding.actvLearningNeed.getText().toString().isEmpty()) {
            binding.actvLearningNeed.setText(learningNeedOptions[0], false);
        }
        if (binding.actvStrategy.getText().toString().isEmpty()) {
            binding.actvStrategy.setText(strategyOptions[0], false);
        }

        binding.actvResources.setOnItemClickListener((parent, view1, position, id) -> {
            String selected = resourceOptions[position].trim();
            boolean isCustom = "Custom / Add Own Materials...".equals(selected);
            binding.tilCustomResources.setVisibility(isCustom ? View.VISIBLE : View.GONE);
            if (isCustom) {
                binding.etCustomResources.requestFocus();
            }
        });

        binding.actvStrategy.setOnItemClickListener((parent, view1, position, id) -> {
            String selected = strategyOptions[position];
            if ("Custom Activity / Hook".equals(selected)) {
                binding.etCustomHook.requestFocus();
            }
        });
    }

    private void bindDropdownAdapters() {
        if (getContext() == null || binding == null) return;

        UnfilteredArrayAdapter resAdapter = new UnfilteredArrayAdapter(
                requireContext(), android.R.layout.simple_dropdown_item_1line, Arrays.asList(resourceOptions));
        binding.actvResources.setAdapter(resAdapter);

        UnfilteredArrayAdapter needAdapter = new UnfilteredArrayAdapter(
                requireContext(), android.R.layout.simple_dropdown_item_1line, Arrays.asList(learningNeedOptions));
        binding.actvLearningNeed.setAdapter(needAdapter);

        UnfilteredArrayAdapter stratAdapter = new UnfilteredArrayAdapter(
                requireContext(), android.R.layout.simple_dropdown_item_1line, Arrays.asList(strategyOptions));
        binding.actvStrategy.setAdapter(stratAdapter);

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

    private void generateFromFreeFormPrompt() {
        String freePrompt = binding.etFreeFormPrompt.getText() != null
                ? binding.etFreeFormPrompt.getText().toString().trim() : "";
        if (freePrompt.isEmpty()) {
            binding.tilFreeFormPrompt.setError("Please enter your lesson request or materials (e.g. Area of a triangle using strings and boxes)");
            binding.etFreeFormPrompt.requestFocus();
            return;
        }
        binding.tilFreeFormPrompt.setError(null);
        hideKeyboard();

        CurriculumEntity selectedDropdownCurr = null;
        String subStrandText = binding.actvSubStrand.getText() != null
                ? binding.actvSubStrand.getText().toString() : "";
        for (CurriculumEntity c : curriculumList) {
            if (subStrandText.contains(c.getSubStrand())) {
                selectedDropdownCurr = c;
                break;
            }
        }
        // Only override with dropdown if user explicitly changed dropdown away from 1.1 Whole numbers
        CurriculumEntity fallbackCurr = (selectedDropdownCurr != null && !"1.1".equals(selectedDropdownCurr.getSubStrandCode()))
                ? selectedDropdownCurr : null;

        LessonGenerator.LessonPlanningParameters params =
                LessonGenerator.parseFreeFormPrompt(freePrompt, curriculumList, fallbackCurr);

        isGeneratingInitiated = true;
        viewModel.generateLessonPlan(params);
    }

    private void generatePlan() {
        hideKeyboard();
        // If the teacher typed a free-form prompt at the top and tapped the bottom Generate button, honor their free-form prompt!
        String topFreePrompt = binding.etFreeFormPrompt.getText() != null
                ? binding.etFreeFormPrompt.getText().toString().trim() : "";
        if (!topFreePrompt.isEmpty()) {
            generateFromFreeFormPrompt();
            return;
        }

        String subStrandText = binding.actvSubStrand.getText().toString();
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
        } catch (NumberFormatException ignored) {}

        int classSize = 45;
        try {
            classSize = Integer.parseInt(binding.etClassSize.getText().toString().trim());
        } catch (NumberFormatException ignored) {}

        String selectedResources = binding.actvResources.getText().toString().trim();
        if ("Custom / Add Own Materials...".equals(selectedResources)) {
            String custom = binding.etCustomResources.getText() != null
                    ? binding.etCustomResources.getText().toString().trim() : "";
            if (custom.isEmpty()) {
                binding.tilCustomResources.setError("Please specify your local materials");
                binding.etCustomResources.requestFocus();
                return;
            } else {
                binding.tilCustomResources.setError(null);
                selectedResources = custom;
            }
        }

        String selectedStrategy = binding.actvStrategy.getText().toString();
        String customHook = binding.etCustomHook.getText() != null
                ? binding.etCustomHook.getText().toString().trim() : "";

        LessonGenerator.LessonPlanningParameters params = new LessonGenerator.LessonPlanningParameters();
        params.curriculum = selectedCurr;
        params.durationMinutes = duration;
        params.classSize = classSize;
        params.availableResources = selectedResources;
        params.learningNeed = binding.actvLearningNeed.getText().toString();
        params.selectedActivityStrategy = selectedStrategy;
        params.customActivityOrHook = customHook;
        params.electricity = binding.switchElectricity.isChecked();
        params.internet = binding.switchInternet.isChecked();

        isGeneratingInitiated = true;
        viewModel.generateLessonPlan(params);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
