package ke.co.mwalimuai.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ke.co.mwalimuai.databinding.FragmentGamificationBinding;
import ke.co.mwalimuai.databinding.ItemGameCardBinding;
import ke.co.mwalimuai.generator.GamificationGenerator;

public class GamificationFragment extends Fragment {

    private FragmentGamificationBinding binding;
    private GameAdapter adapter;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGamificationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new GameAdapter(new ArrayList<>());
        binding.rvGames.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvGames.setAdapter(adapter);

        loadGames();
    }

    private void loadGames() {
        executor.execute(() -> {
            List<GamificationGenerator.GameItem> games = GamificationGenerator.loadGames(requireContext());
            requireActivity().runOnUiThread(() -> adapter.updateItems(games));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private static class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {
        private final List<GamificationGenerator.GameItem> items;

        public GameAdapter(List<GamificationGenerator.GameItem> items) {
            this.items = items;
        }

        public void updateItems(List<GamificationGenerator.GameItem> newItems) {
            items.clear();
            items.addAll(newItems);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemGameCardBinding binding = ItemGameCardBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false
            );
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            GamificationGenerator.GameItem item = items.get(position);
            holder.binding.tvGameName.setText(item.name);
            holder.binding.tvGameDuration.setText(item.durationMinutes + " Mins");
            holder.binding.tvGamePlayers.setText("Grouping: " + item.players + " • 100% Offline");
            holder.binding.tvGameRules.setText(item.rules);
            holder.binding.tvGameMaterials.setText("Materials: " + item.materials);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final ItemGameCardBinding binding;
            ViewHolder(ItemGameCardBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}
