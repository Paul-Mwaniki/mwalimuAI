package ke.co.mwalimuai.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import ke.co.mwalimuai.R;
import ke.co.mwalimuai.database.ActivityEntity;
import ke.co.mwalimuai.databinding.FragmentActivitiesBinding;
import ke.co.mwalimuai.databinding.ItemActivityCardBinding;
import ke.co.mwalimuai.viewmodel.ActivityViewModel;

public class ActivitiesFragment extends Fragment {

    private FragmentActivitiesBinding binding;
    private ActivityViewModel viewModel;
    private ActivityAdapter adapter;
    private String currentStrategy = "All";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentActivitiesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ActivityViewModel.class);

        adapter = new ActivityAdapter(new ArrayList<>());
        binding.rvActivities.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvActivities.setAdapter(adapter);

        setupSearchAndFilters();

        viewModel.getActivities().observe(getViewLifecycleOwner(), list -> {
            adapter.updateItems(list);
            binding.tvActivitiesCount.setText("Showing " + list.size() + " CBC learning activities");
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading ->
                binding.pbActivitiesLoading.setVisibility(loading ? View.VISIBLE : View.GONE));
    }

    private void setupSearchAndFilters() {
        binding.etSearchActivities.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.chipGroupStrategies.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipStratAll) currentStrategy = "All";
            else if (id == R.id.chipStratManipulative) currentStrategy = "Manipulative exploration";
            else if (id == R.id.chipStratThinkPair) currentStrategy = "Think-pair-share";
            else if (id == R.id.chipStratGame) currentStrategy = "Learning game";
            else if (id == R.id.chipStratMarket) currentStrategy = "Market role-play";
            else if (id == R.id.chipStratStation) currentStrategy = "Station rotation";

            applyFilter(binding.etSearchActivities.getText().toString());
        });
    }

    private void applyFilter(String query) {
        viewModel.filterActivities(query, currentStrategy, true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private static class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {
        private final List<ActivityEntity> items;

        public ActivityAdapter(List<ActivityEntity> items) {
            this.items = items;
        }

        public void updateItems(List<ActivityEntity> newItems) {
            items.clear();
            items.addAll(newItems);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemActivityCardBinding binding = ItemActivityCardBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false
            );
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ActivityEntity item = items.get(position);
            holder.binding.tvActivityTitle.setText(item.getActivityTitle());
            holder.binding.tvActivityStrategy.setText(item.getStrategy());
            holder.binding.tvActivityTopic.setText("Topic: " + item.getTopic() + " • Duration: " + item.getDurationMinutes() + " mins");
            holder.binding.tvActivityDescription.setText(item.getDescription());
            holder.binding.tvActivityResources.setText("Resources: " + item.getResources());

            if (item.getAiSuitabilityScore() > 0) {
                holder.binding.tvAiSuitabilityBadge.setVisibility(View.VISIBLE);
                holder.binding.tvAiSuitabilityBadge.setText(String.format("AI Score: %.2f / 5.0", item.getAiSuitabilityScore()));
            } else {
                holder.binding.tvAiSuitabilityBadge.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final ItemActivityCardBinding binding;
            ViewHolder(ItemActivityCardBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}
