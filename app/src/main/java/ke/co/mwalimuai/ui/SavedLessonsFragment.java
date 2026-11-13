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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ke.co.mwalimuai.R;
import ke.co.mwalimuai.database.LessonEntity;
import ke.co.mwalimuai.databinding.FragmentSavedLessonsBinding;
import ke.co.mwalimuai.databinding.ItemSavedLessonBinding;
import ke.co.mwalimuai.viewmodel.LessonViewModel;

public class SavedLessonsFragment extends Fragment {

    private FragmentSavedLessonsBinding binding;
    private LessonViewModel viewModel;
    private SavedLessonAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSavedLessonsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(LessonViewModel.class);

        adapter = new SavedLessonAdapter(new ArrayList<>(), new SavedLessonAdapter.OnSavedLessonActionListener() {
            @Override
            public void onView(LessonEntity lesson) {
                viewModel.setCurrentLesson(lesson);
                Navigation.findNavController(requireView()).navigate(R.id.action_saved_to_preview);
            }

            @Override
            public void onExportWord(LessonEntity lesson) {
                viewModel.exportToWord(requireContext(), lesson, new LessonViewModel.ExportCallback() {
                    @Override
                    public void onSuccess(File file) {
                        requireActivity().runOnUiThread(() ->
                                shareFile(file, "application/vnd.openxmlformats-officedocument.wordprocessingml.document", lesson.getLessonTitle()));
                    }

                    @Override
                    public void onError(Exception e) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Export failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                });
            }

            @Override
            public void onExportPdf(LessonEntity lesson) {
                viewModel.exportToPdf(requireContext(), lesson, new LessonViewModel.ExportCallback() {
                    @Override
                    public void onSuccess(File file) {
                        requireActivity().runOnUiThread(() ->
                                shareFile(file, "application/pdf", lesson.getLessonTitle()));
                    }

                    @Override
                    public void onError(Exception e) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Export failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                });
            }

            @Override
            public void onDelete(LessonEntity lesson) {
                viewModel.deleteLesson(lesson);
                Toast.makeText(requireContext(), "Lesson plan deleted", Toast.LENGTH_SHORT).show();
            }
        });

        binding.rvSavedLessons.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSavedLessons.setAdapter(adapter);

        viewModel.getSavedLessons().observe(getViewLifecycleOwner(), list -> {
            if (list == null || list.isEmpty()) {
                binding.layoutEmptyState.setVisibility(View.VISIBLE);
                binding.rvSavedLessons.setVisibility(View.GONE);
                binding.tvSavedCount.setText("No saved lessons in Room SQLite database");
            } else {
                binding.layoutEmptyState.setVisibility(View.GONE);
                binding.rvSavedLessons.setVisibility(View.VISIBLE);
                binding.tvSavedCount.setText("Showing " + list.size() + " saved CBE lesson plans");
                adapter.updateItems(list);
            }
        });
    }

    private void shareFile(File file, String mimeType, String title) {
        try {
            Uri contentUri = FileProvider.getUriForFile(
                    requireContext(),
                    "ke.co.mwalimuai.fileprovider",
                    file
            );

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType(mimeType);
            intent.putExtra(Intent.EXTRA_STREAM, contentUri);
            intent.putExtra(Intent.EXTRA_SUBJECT, title);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Share Lesson Document"));
        } catch (Exception e) {
            Toast.makeText(requireContext(), "File generated at: " + file.getName(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private static class SavedLessonAdapter extends RecyclerView.Adapter<SavedLessonAdapter.ViewHolder> {
        public interface OnSavedLessonActionListener {
            void onView(LessonEntity lesson);
            void onExportWord(LessonEntity lesson);
            void onExportPdf(LessonEntity lesson);
            void onDelete(LessonEntity lesson);
        }

        private final List<LessonEntity> items;
        private final OnSavedLessonActionListener listener;

        public SavedLessonAdapter(List<LessonEntity> items, OnSavedLessonActionListener listener) {
            this.items = items;
            this.listener = listener;
        }

        public void updateItems(List<LessonEntity> newItems) {
            items.clear();
            items.addAll(newItems);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemSavedLessonBinding binding = ItemSavedLessonBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false
            );
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            LessonEntity item = items.get(position);
            holder.binding.tvSavedLessonTitle.setText(item.getLessonTitle());
            holder.binding.tvSavedLessonStrand.setText(item.getStrand() + " • " + item.getSubStrand());
            holder.binding.tvSavedLessonMeta.setText("Duration: " + item.getDurationMinutes() +
                    " Mins • Class Size: " + item.getClassSize() + " Learners");

            String dateStr = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date(item.getCreatedTimestamp()));
            holder.binding.tvSavedLessonDate.setText(dateStr);

            holder.binding.btnViewLesson.setOnClickListener(v -> listener.onView(item));
            holder.binding.btnExportDocx.setOnClickListener(v -> listener.onExportWord(item));
            holder.binding.btnExportPdf.setOnClickListener(v -> listener.onExportPdf(item));
            holder.binding.btnDeleteLesson.setOnClickListener(v -> listener.onDelete(item));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final ItemSavedLessonBinding binding;
            ViewHolder(ItemSavedLessonBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}
