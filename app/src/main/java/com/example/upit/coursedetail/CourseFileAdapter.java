package com.example.upit.coursedetail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upit.databinding.CourseStudentItemBinding;
import com.example.upit.databinding.FileItemBinding;
import com.example.upit.util.ClickListener;

import java.util.List;
import java.util.Map;

public class CourseFileAdapter extends ListAdapter<Map.Entry<String, String>, CourseFileAdapter.ViewHolder> {
    private ClickListener clickListener;

    public CourseFileAdapter(ClickListener clickListener) {
        super(new CourseFileDiffCallback());
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ViewHolder.from(parent);
    }

    @Override
    public void submitList(@Nullable List<Map.Entry<String, String>> list) {
        // Force update the recyclerview adapter
        notifyDataSetChanged();

        // Submit the list to the adapter
        super.submitList(list);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind the item to the view holder
        holder.bind(getItem(position), clickListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private FileItemBinding binding;

        public ViewHolder(FileItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Map.Entry<String, String> file, ClickListener clickListener) {
            // Update the views and binding accordingly
            binding.buttonFile.setText(file.getValue());
            binding.buttonFile.setOnClickListener(v -> clickListener.onClick(file.getKey()));
        }

        /**
         * Creates the view holder
         */
        public static ViewHolder from(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            FileItemBinding binding = FileItemBinding.inflate(layoutInflater, parent, false);
            return new ViewHolder(binding);
        }
    }
}