package com.example.upit.coursedetail;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upit.data.Users;
import com.example.upit.databinding.CourseStudentItemBinding;

import java.util.List;
import java.util.Map;

public class CourseStudentAdapter extends ListAdapter<Map.Entry<String, String>, CourseStudentAdapter.ViewHolder> {

    public CourseStudentAdapter() {
        super(new CourseStudentDiffCallback());
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

        // Submit the list to the adapter
        super.submitList(list);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Binds the item to the viewHolder
        holder.bind(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CourseStudentItemBinding binding;

        public ViewHolder(CourseStudentItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Map.Entry<String, String> student) {
            // Update the view and binding accordingly
            binding.textName.setText(student.getValue());
        }

        /**
         * Creates the view holder
         */
        public static ViewHolder from(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            CourseStudentItemBinding binding = CourseStudentItemBinding.inflate(layoutInflater, parent, false);
            return new ViewHolder(binding);
        }
    }
}
