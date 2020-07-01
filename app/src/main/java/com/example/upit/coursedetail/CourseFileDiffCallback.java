package com.example.upit.coursedetail;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Map;

/**
 * DiffCallback used by the recycler view adapter to calculates the difference between two lists
 * Read more: https://developer.android.com/reference/androidx/recyclerview/widget/DiffUtil
 */
public class CourseFileDiffCallback extends DiffUtil.ItemCallback<Map.Entry<String, String>> {
    @Override
    public boolean areItemsTheSame(@NonNull Map.Entry<String, String> oldItem, @NonNull Map.Entry<String, String> newItem) {
        return false;
    }

    @Override
    public boolean areContentsTheSame(@NonNull Map.Entry<String, String> oldItem, @NonNull Map.Entry<String, String> newItem) {
        return false;
    }
}
