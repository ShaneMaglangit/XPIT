package com.example.upit.student;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upit.courselist.CourseAdapter;
import com.example.upit.courselist.CourseDiffCallback;
import com.example.upit.data.Users;
import com.example.upit.databinding.CourseItemBinding;
import com.example.upit.databinding.StudentItemBinding;

import java.util.List;
import java.util.Map;

public class StudentAdapter extends ListAdapter<Users, StudentAdapter.ViewHolder> {

    public StudentAdapter() {
        super(new StudentDiffCallback());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ViewHolder.from(parent);
    }

    @Override
    public void submitList(@Nullable List<Users> list) {
        // Force the recyclerview to update
        notifyDataSetChanged();

        // Submit the list
        super.submitList(list);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentAdapter.ViewHolder holder, int position) {
        // Binds the item to the view holder
        holder.bind(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private StudentItemBinding binding;

        public ViewHolder(StudentItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Users user) {
            // Modify the view holder elements / binding
            binding.setStudent(user);
            binding.executePendingBindings();
        }

        public static StudentAdapter.ViewHolder from(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            StudentItemBinding binding = StudentItemBinding.inflate(layoutInflater, parent, false);
            return new StudentAdapter.ViewHolder(binding);
        }
    }
}