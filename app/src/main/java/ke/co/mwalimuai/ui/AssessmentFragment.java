package ke.co.mwalimuai.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ke.co.mwalimuai.databinding.FragmentAssessmentBinding;
import ke.co.mwalimuai.databinding.ItemAssessmentCardBinding;
import ke.co.mwalimuai.generator.AssessmentGenerator;
import ke.co.mwalimuai.viewmodel.AssessmentViewModel;

public class AssessmentFragment extends Fragment {

    private FragmentAssessmentBinding binding;
    private AssessmentViewModel viewModel;
    private AssessmentAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAssessmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AssessmentViewModel.class);

        adapter = new AssessmentAdapter(new ArrayList<>());
        binding.rvAssessments.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAssessments.setAdapter(adapter);

        viewModel.getAssessments().observe(getViewLifecycleOwner(), list -> adapter.updateItems(list));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private static class AssessmentAdapter extends RecyclerView.Adapter<AssessmentAdapter.ViewHolder> {
        private final List<AssessmentGenerator.AssessmentItem> items;

        public AssessmentAdapter(List<AssessmentGenerator.AssessmentItem> items) {
            this.items = items;
        }

        public void updateItems(List<AssessmentGenerator.AssessmentItem> newItems) {
            items.clear();
            items.addAll(newItems);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemAssessmentCardBinding binding = ItemAssessmentCardBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false
            );
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            AssessmentGenerator.AssessmentItem item = items.get(position);
            holder.binding.tvAssessmentType.setText(item.assessmentType != null ? item.assessmentType : "Observation");
            holder.binding.tvAssessmentCurriculumId.setText(item.curriculumId);
            holder.binding.tvAssessmentPrompt.setText(item.assessmentPrompt);
            holder.binding.tvEvidence.setText("Evidence: " + item.evidenceOfLearning);

            holder.binding.tvRubricExceeds.setText("★ Exceeds (EE): " + item.exceedsExpectations);
            holder.binding.tvRubricMeets.setText("✔ Meets (ME): " + item.meetsExpectations);
            holder.binding.tvRubricApproaching.setText("▲ Approaching (AE): " + item.approachingExpectations);
            holder.binding.tvRubricBelow.setText("● Below (BE): " + item.belowExpectations);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final ItemAssessmentCardBinding binding;
            ViewHolder(ItemAssessmentCardBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}
