package com.example.upit.contact;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.upit.data.Conversation;

import java.util.Map;

/**
 * DiffCallback used by the recycler view adapter to calculates the difference between two lists
 * Read more: https://developer.android.com/reference/androidx/recyclerview/widget/DiffUtil
 */
public class ContactDiffCallback extends DiffUtil.ItemCallback<Conversation> {
    @Override
    public boolean areItemsTheSame(@NonNull Conversation oldItem, @NonNull Conversation newItem) {
        return oldItem.getId().equals(newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull Conversation oldItem, @NonNull Conversation newItem) {
        return oldItem.getMembers().equals(newItem.getMembers());
    }
}