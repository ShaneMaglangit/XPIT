package com.example.upit.student;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.upit.data.Users;

import java.util.Map;

/**
 * DiffCallback used by the recycler view adapter to calculates the difference between two lists
 * Read more: https://developer.android.com/reference/androidx/recyclerview/widget/DiffUtil
 */
public class StudentDiffCallback extends DiffUtil.ItemCallback<Users> {

    @Override
    public boolean areItemsTheSame(@NonNull Users oldItem, @NonNull Users newItem) {
        return oldItem.getId().equals(newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull Users oldItem, @NonNull Users newItem) {
        return oldItem.getId().equals(newItem.getId());
    }
}