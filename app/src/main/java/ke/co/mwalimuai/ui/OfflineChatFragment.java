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

import ke.co.mwalimuai.ai.OfflineLanguageModelEngine;
import ke.co.mwalimuai.databinding.FragmentOfflineChatBinding;
import ke.co.mwalimuai.databinding.ItemChatMessageBinding;

public class OfflineChatFragment extends Fragment {

    private FragmentOfflineChatBinding binding;
    private ChatAdapter adapter;
    private final List<OfflineLanguageModelEngine.ChatMessage> messageList = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOfflineChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ChatAdapter(messageList);
        binding.rvChatMessages.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvChatMessages.setAdapter(adapter);

        // Initial welcome message
        if (messageList.isEmpty()) {
            addMessage(new OfflineLanguageModelEngine.ChatMessage(
                    false,
                    "Habari Mwalimu! I am your offline CBC assistant for Grade 6 Mathematics. " +
                            "Ask me about lesson planning, low-cost activities, large classroom management, or formative assessment rubrics."
            ));
        }

        binding.btnSendMessage.setOnClickListener(v -> sendMessage(binding.etChatMessage.getText().toString()));

        // Suggestion buttons
        binding.btnSuggFractions.setOnClickListener(v -> sendMessage("How do I teach fractions using stones in Grade 6?"));
        binding.btnSuggLargeClass.setOnClickListener(v -> sendMessage("Suggest strategies for managing a large class of 55 learners in math."));
        binding.btnSuggGame.setOnClickListener(v -> sendMessage("Give me a low-cost game for Whole numbers in a Kenyan school."));
        binding.btnSuggRubric.setOnClickListener(v -> sendMessage("What is the CBC assessment rubric for Grade 6 Mathematics?"));
    }

    private void sendMessage(String text) {
        if (text == null || text.trim().isEmpty()) return;
        String userQuery = text.trim();
        binding.etChatMessage.setText("");

        addMessage(new OfflineLanguageModelEngine.ChatMessage(true, userQuery));

        executor.execute(() -> {
            OfflineLanguageModelEngine engine = OfflineLanguageModelEngine.getInstance(requireContext());
            String response = engine.generateResponse(userQuery);

            requireActivity().runOnUiThread(() ->
                    addMessage(new OfflineLanguageModelEngine.ChatMessage(false, response)));
        });
    }

    private void addMessage(OfflineLanguageModelEngine.ChatMessage message) {
        messageList.add(message);
        adapter.notifyItemInserted(messageList.size() - 1);
        binding.rvChatMessages.smoothScrollToPosition(messageList.size() - 1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private static class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
        private final List<OfflineLanguageModelEngine.ChatMessage> messages;

        public ChatAdapter(List<OfflineLanguageModelEngine.ChatMessage> messages) {
            this.messages = messages;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemChatMessageBinding binding = ItemChatMessageBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false
            );
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            OfflineLanguageModelEngine.ChatMessage msg = messages.get(position);
            if (msg.isUser) {
                holder.binding.layoutUserMsg.setVisibility(View.VISIBLE);
                holder.binding.layoutAiMsg.setVisibility(View.GONE);
                holder.binding.tvUserMsg.setText(msg.text);
            } else {
                holder.binding.layoutUserMsg.setVisibility(View.GONE);
                holder.binding.layoutAiMsg.setVisibility(View.VISIBLE);
                holder.binding.tvAiMsg.setText(msg.text);
            }
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final ItemChatMessageBinding binding;
            ViewHolder(ItemChatMessageBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}
