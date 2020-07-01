package com.example.upit.message;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.upit.data.Message;

import java.util.List;
import java.util.Map;

/**
 * DiffCallback used by the recycler view adapter to calculates the difference between two lists
 * Read more: https://developer.android.com/reference/androidx/recyclerview/widget/DiffUtil
 */
public class MessageDiffCallback extends DiffUtil.ItemCallback<Message> {
    @Override
    public boolean areItemsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
        return false;
    }

    @Override
    public boolean areContentsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
        return false;
    }
}
