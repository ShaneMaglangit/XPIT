package com.example.upit.courselist;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upit.databinding.CourseItemBinding;
import com.example.upit.util.ClickListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseAdapter extends ListAdapter<Map.Entry<String, String>, CourseAdapter.ViewHolder> {
    private ClickListener clickListener;

    public CourseAdapter(ClickListener clickListener) {
        super(new CourseDiffCallback());
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ViewHolder.from(parent);
    }

    @Override
    public void submitList(@Nullable List<Map.Entry<String, String>> list) {
        // Force update the recyclerview
        notifyDataSetChanged();

        // Submit the list
        super.submitList(list);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Binds the course item to the view holder
        holder.bind(getItem(position), clickListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CourseItemBinding binding;

        public ViewHolder(CourseItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Map.Entry<String, String> course, ClickListener clickListener) {
            // Update the views and binding accordingly
            binding.textName.setText(course.getValue());
            binding.getRoot().setOnClickListener(l -> clickListener.onClick(course.getKey()));
        }

        /**
         * Create the view holder
         */
        public static ViewHolder from(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            CourseItemBinding binding = CourseItemBinding.inflate(layoutInflater, parent, false);
            return new ViewHolder(binding);
        }
    }
}