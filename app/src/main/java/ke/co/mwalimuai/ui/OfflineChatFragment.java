package ke.co.mwalimuai.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ke.co.mwalimuai.R;
import ke.co.mwalimuai.ai.OfflineLanguageModelEngine;
import ke.co.mwalimuai.ai.OnDeviceSlmEngine;
import ke.co.mwalimuai.database.CurriculumDao;
import ke.co.mwalimuai.database.CurriculumEntity;
import ke.co.mwalimuai.database.LessonEntity;
import ke.co.mwalimuai.database.MwalimuDatabase;
import ke.co.mwalimuai.databinding.FragmentOfflineChatBinding;
import ke.co.mwalimuai.databinding.ItemChatMessageBinding;
import ke.co.mwalimuai.generator.LessonGenerator;
import ke.co.mwalimuai.viewmodel.LessonViewModel;

public class OfflineChatFragment extends Fragment {

    private FragmentOfflineChatBinding binding;
    private ChatAdapter adapter;
    private LessonViewModel lessonViewModel;
    private final List<OfflineLanguageModelEngine.ChatMessage> messageList = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private LessonEntity lastGeneratedLesson = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOfflineChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lessonViewModel = new ViewModelProvider(requireActivity()).get(LessonViewModel.class);

        adapter = new ChatAdapter(messageList);
        binding.rvChatMessages.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvChatMessages.setAdapter(adapter);

        updateHeaderStatus();

        // Initial welcome message
        if (messageList.isEmpty()) {
            addMessage(new OfflineLanguageModelEngine.ChatMessage(
                    false,
                    "Habari Mwalimu! Welcome to Mwalimu SNE AI — your 100% offline inclusive CBC assistant.\n\n" +
                            "Type any Grade 6 Mathematics topic, materials, and learner needs (e.g. blind, deaf/KSL, dyslexia, dyscalculia, ADHD):\n\n" +
                            "Example:\n\"Teach Area of a triangle for 48 learners using strings and boxes for a class with 2 blind learners\"\n\n" +
                            "Press Enter or Send — I will generate a material-connected scenario, exact SNE adaptations, multi-pathway CBC rubrics, and let you listen hands-free!"
            ));
        }

        // Send button click
        binding.btnSendMessage.setOnClickListener(v ->
                sendMessage(binding.etChatMessage.getText().toString()));

        // Keyboard Enter / Send action listener
        binding.etChatMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || actionId == EditorInfo.IME_ACTION_GO
                    || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                sendMessage(binding.etChatMessage.getText().toString());
                return true;
            }
            return false;
        });

        binding.etChatMessage.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                sendMessage(binding.etChatMessage.getText().toString());
                return true;
            }
            return false;
        });

        binding.btnOpenLastAsLesson.setOnClickListener(v -> {
            if (lastGeneratedLesson != null) {
                lessonViewModel.setCurrentLesson(lastGeneratedLesson);
                Navigation.findNavController(v).navigate(R.id.action_chat_to_preview);
            }
        });

        // Quick free-form prompt buttons with SNE inclusive examples
        binding.btnSuggFractions.setText("Area (Blind SNE)");
        binding.btnSuggFractions.setOnClickListener(v ->
                sendMessage("Teach Area of a triangle for 48 learners using strings and cardboard boxes for 2 blind learners"));

        binding.btnSuggLargeClass.setText("Fractions (Deaf KSL)");
        binding.btnSuggLargeClass.setOnClickListener(v ->
                sendMessage("Teach Fractions using seeds and sticks with KSL sign language for deaf learners"));

        binding.btnSuggGame.setText("Division (Dyscalculia)");
        binding.btnSuggGame.setOnClickListener(v ->
                sendMessage("Teach Division for 50 learners with dyscalculia using bottle tops and ten-frames"));

        binding.btnSuggRubric.setText("3-D Objects (ADHD)");
        binding.btnSuggRubric.setOnClickListener(v ->
                sendMessage("Teach 3-D Objects using cardboard boxes and strings for ADHD active learners"));
    }

    private void hideKeyboard() {
        try {
            if (getActivity() != null && binding != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(binding.etChatMessage.getWindowToken(), 0);
                }
            }
        } catch (Exception ignored) {}
    }

    private void updateHeaderStatus() {
        if (binding == null || getContext() == null) return;
        if (OnDeviceSlmEngine.isModelDownloaded(requireContext())) {
            binding.tvChatTopTitle.setText("Free-Form AI Prompt (Qwen2.5-0.5B Active)");
        } else {
            binding.tvChatTopTitle.setText("Free-Form AI Prompt (Tap to Download Qwen2.5-0.5B)");
            binding.tvChatTopTitle.setOnClickListener(v -> {
                if (getActivity() != null) {
                    OnDeviceSlmEngine.getInstance(requireContext()).promptModelDownloadIfNeeded(requireActivity(), true);
                }
            });
        }
    }

    private void sendMessage(String text) {
        if (text == null || text.trim().isEmpty()) return;
        if (getContext() == null) return;

        String userQuery = text.trim();
        binding.etChatMessage.setText("");
        hideKeyboard();

        addMessage(new OfflineLanguageModelEngine.ChatMessage(true, userQuery));

        final boolean slmPresent = OnDeviceSlmEngine.isModelDownloaded(requireContext());
        final String thinkingText = slmPresent
                ? "🤖 Prompting on-device Qwen2.5-0.5B SLM with your materials & KICD Grade 6 curriculum..."
                : "⏳ Generating your material-connected KICD Grade 6 lesson plan...";
        addMessage(new OfflineLanguageModelEngine.ChatMessage(false, thinkingText));
        final int thinkingMsgIndex = messageList.size() - 1;

        final Context appCtx = requireContext().getApplicationContext();

        executor.execute(() -> {
            CurriculumDao dao = MwalimuDatabase.getInstance(appCtx).curriculumDao();
            List<CurriculumEntity> allCurricula = dao.getAllCurriculum();

            // Parse free-form prompt into LessonPlanningParameters & generate complete coherent LessonEntity (prompts Qwen2.5-0.5B SLM)
            LessonGenerator.LessonPlanningParameters params =
                    LessonGenerator.parseFreeFormPrompt(userQuery, allCurricula);
            LessonEntity generatedLesson =
                    LessonGenerator.generateLessonPlan(appCtx, params, new ArrayList<>());
            lastGeneratedLesson = generatedLesson;

            StringBuilder reply = new StringBuilder();
            reply.append("📋 ").append(generatedLesson.getLessonTitle()).append("\n");
            reply.append("▪ Materials in Use: ").append(params.availableResources).append("\n");
            reply.append("▪ Class Size: ").append(params.classSize).append(" Learners | Duration: ").append(params.durationMinutes).append(" Mins\n");
            if (slmPresent) {
                reply.append("▪ AI Engine: Qwen2.5-0.5B-Instruct (On-Device SLM + KICD RAG)\n");
            }
            reply.append("────────────────────────────────────────\n\n");
            reply.append(generatedLesson.getIntroduction()).append("\n\n");
            reply.append("────────────────────────────────────────\n\n");
            reply.append(generatedLesson.getLessonDevelopment()).append("\n\n");
            reply.append("👉 Tap 'Open Full Lesson' at the top right to view the complete formatted plan, save it, or export to Word (.docx) & PDF!");

            String finalReply = reply.toString();
            android.app.Activity activity = getActivity();
            if (activity != null && !activity.isFinishing()) {
                activity.runOnUiThread(() -> {
                    if (binding == null || !isAdded()) return;
                    if (thinkingMsgIndex >= 0 && thinkingMsgIndex < messageList.size()) {
                        messageList.set(thinkingMsgIndex, new OfflineLanguageModelEngine.ChatMessage(false, finalReply));
                        adapter.notifyItemChanged(thinkingMsgIndex);
                    } else {
                        addMessage(new OfflineLanguageModelEngine.ChatMessage(false, finalReply));
                    }
                    binding.btnOpenLastAsLesson.setVisibility(View.VISIBLE);
                    lessonViewModel.setCurrentLesson(generatedLesson);
                    updateHeaderStatus();
                });
            }
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
