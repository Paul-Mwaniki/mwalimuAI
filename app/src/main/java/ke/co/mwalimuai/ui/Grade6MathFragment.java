package ke.co.mwalimuai.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ke.co.mwalimuai.R;
import ke.co.mwalimuai.database.ActivityEntity;
import ke.co.mwalimuai.database.CurriculumDao;
import ke.co.mwalimuai.database.CurriculumEntity;
import ke.co.mwalimuai.database.MwalimuDatabase;
import ke.co.mwalimuai.databinding.FragmentGrade6MathBinding;
import ke.co.mwalimuai.databinding.ItemCurriculumCardBinding;
import ke.co.mwalimuai.generator.AssessmentGenerator;
import ke.co.mwalimuai.utils.CurriculumBreakdownHelper;

public class Grade6MathFragment extends Fragment {

    private FragmentGrade6MathBinding binding;
    private CurriculumAdapter adapter;
    private final List<CurriculumEntity> allCurriculumList = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGrade6MathBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new CurriculumAdapter(new ArrayList<>(), new OnCurriculumClickListener() {
            @Override
            public void onPlanClick(CurriculumEntity item) {
                navigateToPlanner(item);
            }

            @Override
            public void onViewBreakdownClick(CurriculumEntity item) {
                showDetailedBreakdownDialog(item);
            }
        });

        binding.rvCurriculum.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCurriculum.setAdapter(adapter);

        binding.btnOpenCurriculumSource.setOnClickListener(v -> {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(CurriculumBreakdownHelper.OFFLINE_CURRICULUM_DRIVE_URL));
                startActivity(browserIntent);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Full curriculum is already available offline below!", Toast.LENGTH_SHORT).show();
            }
        });

        setupFilterChips();
        loadCurriculumData();
    }

    private void navigateToPlanner(CurriculumEntity item) {
        Bundle bundle = new Bundle();
        bundle.putString("selectedSubStrand", item.getSubStrand());
        bundle.putString("selectedCurriculumId", item.getCurriculumId());
        Navigation.findNavController(requireView()).navigate(R.id.navigation_planner, bundle);
    }

    private void showDetailedBreakdownDialog(CurriculumEntity item) {
        executor.execute(() -> {
            CurriculumDao dao = MwalimuDatabase.getInstance(requireContext()).curriculumDao();
            List<ActivityEntity> activities = dao.getActivitiesByTopicOrCurriculum(item.getSubStrand(), item.getCurriculumId());
            AssessmentGenerator.AssessmentItem rubric = AssessmentGenerator.getAssessmentForCurriculum(requireContext(), item.getCurriculumId());
            CurriculumBreakdownHelper.SubStrandBreakdown breakdown = CurriculumBreakdownHelper.getBreakdown(item);

            requireActivity().runOnUiThread(() -> {
                if (!isAdded() || getContext() == null) return;

                ScrollView scrollView = new ScrollView(requireContext());
                int pad = (int) (18 * getResources().getDisplayMetrics().density);
                LinearLayout container = new LinearLayout(requireContext());
                container.setOrientation(LinearLayout.VERTICAL);
                container.setPadding(pad, pad / 2, pad, pad);
                scrollView.addView(container);

                // 1. Metadata Summary
                addSectionHeader(container, "📌 Curriculum Overview");
                addBodyText(container,
                        "• Strand: " + item.getStrandCode() + " " + item.getStrand() + "\n" +
                        "• Sub-strand: " + item.getSubStrandCode() + " " + item.getSubStrand() + "\n" +
                        "• KICD Source Pages: Pages " + item.getSourcePdfPages() + " (" + item.getLessonsAllocated() + " Lessons Allocated)\n" +
                        "• Kenyan Context: " + item.getKenyanContext() + "\n" +
                        "• Local Materials: " + item.getLocalResources() + "\n" +
                        "• Core Competencies: " + breakdown.coreCompetencies + "\n" +
                        "• Core Values: " + breakdown.valuesIntegrated
                );

                // 2. Specific Learning Outcomes
                addSectionHeader(container, "🎯 Specific Learning Outcomes");
                StringBuilder outcomesText = new StringBuilder("By the end of the sub-strand, the learner should be able to:\n");
                for (int i = 0; i < breakdown.learningOutcomes.size(); i++) {
                    outcomesText.append(i + 1).append(". ").append(breakdown.learningOutcomes.get(i)).append("\n");
                }
                addBodyText(container, outcomesText.toString().trim());

                // 3. Page-by-Page Breakdown
                addSectionHeader(container, "📖 Page-by-Page Curriculum Breakdown (Pages " + item.getSourcePdfPages() + ")");
                StringBuilder pagesText = new StringBuilder();
                for (CurriculumBreakdownHelper.PageBreakdown p : breakdown.pages) {
                    pagesText.append("▪ ").append(p.pageRange).append(" — ").append(p.topicTitle).append("\n")
                            .append("   • What to Teach: ").append(p.simplifiedExplanation).append("\n")
                            .append("   • Quick Page Activity: ").append(p.quickActivityIdea).append("\n\n");
                }
                addBodyText(container, pagesText.toString().trim());

                // 4. Key Inquiry Questions
                addSectionHeader(container, "❓ Key Inquiry Questions");
                StringBuilder kiqText = new StringBuilder();
                for (String q : breakdown.keyInquiryQuestions) {
                    kiqText.append("• ").append(q).append("\n");
                }
                addBodyText(container, kiqText.toString().trim());

                // 5. Classroom Learning Activities from Offline DB
                addSectionHeader(container, "🏃 Classroom Activities to View (" + activities.size() + " Offline Activities)");
                StringBuilder actBuilder = new StringBuilder();
                int maxToShow = Math.min(activities.size(), 6);
                for (int i = 0; i < maxToShow; i++) {
                    ActivityEntity act = activities.get(i);
                    actBuilder.append(i + 1).append(". ").append(act.getActivityTitle())
                            .append(" (").append(act.getDurationMinutes()).append(" mins • ").append(act.getGrouping()).append(")\n")
                            .append("   • Strategy: ").append(act.getStrategy()).append("\n")
                            .append("   • Materials: ").append(act.getResources()).append("\n")
                            .append("   • Step-by-Step Instructions:\n")
                            .append(ke.co.mwalimuai.generator.LessonGenerator.formatNumberedStepsNewLines(act.getLearnerSteps())).append("\n")
                            .append("   • Support/Inclusion: ").append(act.getDifferentiationSupport()).append("\n\n");
                }
                addBodyText(container, actBuilder.toString().trim());

                // 6. 4-Tier CBE Assessment Rubric
                addSectionHeader(container, "📊 4-Tier CBE Assessment Rubric");
                addBodyText(container,
                        "• Assessment Task: " + rubric.assessmentPrompt + "\n" +
                        "• Exceeds Expectations (EE): " + rubric.exceedsExpectations + "\n" +
                        "• Meets Expectations (ME): " + rubric.meetsExpectations + "\n" +
                        "• Approaching Expectations (AE): " + rubric.approachingExpectations + "\n" +
                        "• Below Expectations (BE): " + rubric.belowExpectations
                );

                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(item.getSubStrandCode() + " " + item.getSubStrand() + " (pp. " + item.getSourcePdfPages() + ")")
                        .setView(scrollView)
                        .setPositiveButton("Plan Lesson", (dialog, which) -> navigateToPlanner(item))
                        .setNegativeButton("Close", null)
                        .show();
            });
        });
    }

    private void addSectionHeader(LinearLayout parent, String title) {
        TextView tv = new TextView(requireContext());
        tv.setText(title);
        tv.setTextSize(14.5f);
        tv.setTypeface(tv.getTypeface(), android.graphics.Typeface.BOLD);
        tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
        int topMargin = (int) (12 * getResources().getDisplayMetrics().density);
        int bottomMargin = (int) (4 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, topMargin, 0, bottomMargin);
        tv.setLayoutParams(lp);
        parent.addView(tv);
    }

    private void addBodyText(LinearLayout parent, String text) {
        TextView tv = new TextView(requireContext());
        tv.setText(text);
        tv.setTextSize(13f);
        tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
        tv.setLineSpacing(6f, 1.0f);
        parent.addView(tv);
    }

    private void setupFilterChips() {
        binding.chipGroupStrands.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipStrandAll) {
                filterByStrand("All");
            } else if (id == R.id.chipStrandNumbers) {
                filterByStrand("Numbers");
            } else if (id == R.id.chipStrandMeasurement) {
                filterByStrand("Measurement");
            } else if (id == R.id.chipStrandGeometry) {
                filterByStrand("Geometry");
            } else if (id == R.id.chipStrandData) {
                filterByStrand("Data");
            } else if (id == R.id.chipStrandAlgebra) {
                filterByStrand("Algebra");
            }
        });
    }

    private void loadCurriculumData() {
        binding.pbCurriculumLoading.setVisibility(View.VISIBLE);
        executor.execute(() -> {
            CurriculumDao dao = MwalimuDatabase.getInstance(requireContext()).curriculumDao();
            List<CurriculumEntity> list = dao.getAllCurriculum();
            allCurriculumList.clear();
            allCurriculumList.addAll(list);

            requireActivity().runOnUiThread(() -> {
                if (binding == null) return;
                binding.pbCurriculumLoading.setVisibility(View.GONE);
                adapter.updateItems(allCurriculumList);
            });
        });
    }

    private void filterByStrand(String strand) {
        if (strand.equalsIgnoreCase("All")) {
            adapter.updateItems(allCurriculumList);
            return;
        }
        List<CurriculumEntity> filtered = new ArrayList<>();
        for (CurriculumEntity c : allCurriculumList) {
            if (c.getStrand() != null && c.getStrand().toLowerCase().contains(strand.toLowerCase())) {
                filtered.add(c);
            }
        }
        adapter.updateItems(filtered);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // --- RecyclerView Adapter ---
    public interface OnCurriculumClickListener {
        void onPlanClick(CurriculumEntity item);
        void onViewBreakdownClick(CurriculumEntity item);
    }

    private static class CurriculumAdapter extends RecyclerView.Adapter<CurriculumAdapter.ViewHolder> {
        private final List<CurriculumEntity> items;
        private final OnCurriculumClickListener listener;

        public CurriculumAdapter(List<CurriculumEntity> items, OnCurriculumClickListener listener) {
            this.items = items;
            this.listener = listener;
        }

        public void updateItems(List<CurriculumEntity> newItems) {
            items.clear();
            items.addAll(newItems);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemCurriculumCardBinding binding = ItemCurriculumCardBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false
            );
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CurriculumEntity item = items.get(position);
            holder.binding.tvStrandCode.setText(item.getSubStrandCode());
            holder.binding.tvSubStrandTitle.setText(item.getSubStrand());
            holder.binding.tvSourcePages.setText("Pages " + item.getSourcePdfPages());
            holder.binding.tvLessonsCount.setText(item.getLessonsAllocated() + " Lessons");
            holder.binding.tvStrandName.setText("Strand " + item.getStrandCode() + ": " + item.getStrand()
                    + " • Context: " + item.getKenyanContext());

            // Format focus as clean bullets
            String rawFocus = item.getCurriculumAlignedFocus() != null ? item.getCurriculumAlignedFocus() : "";
            String[] parts = rawFocus.split(";");
            StringBuilder focusBullets = new StringBuilder();
            for (String p : parts) {
                String trimmed = p.trim();
                if (!trimmed.isEmpty()) {
                    String cap = trimmed.substring(0, 1).toUpperCase() + trimmed.substring(1);
                    focusBullets.append("• ").append(cap).append("\n");
                }
            }
            holder.binding.tvCurriculumFocus.setText(focusBullets.toString().trim());

            // Populate simplified Page-by-Page preview
            CurriculumBreakdownHelper.SubStrandBreakdown breakdown = CurriculumBreakdownHelper.getBreakdown(item);
            StringBuilder pageSummary = new StringBuilder();
            for (CurriculumBreakdownHelper.PageBreakdown pb : breakdown.pages) {
                pageSummary.append("• ").append(pb.pageRange).append(": ").append(pb.topicTitle).append("\n");
            }
            holder.binding.tvPagesPreview.setText(pageSummary.toString().trim());

            holder.binding.tvLocalResources.setText("🛠️ Local Materials: " + item.getLocalResources());

            holder.binding.btnViewBreakdown.setOnClickListener(v -> {
                if (listener != null) listener.onViewBreakdownClick(item);
            });

            holder.binding.cardCurriculumItem.setOnClickListener(v -> {
                if (listener != null) listener.onViewBreakdownClick(item);
            });

            holder.binding.btnPlanThisSubStrand.setOnClickListener(v -> {
                if (listener != null) listener.onPlanClick(item);
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final ItemCurriculumCardBinding binding;
            ViewHolder(ItemCurriculumCardBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}
