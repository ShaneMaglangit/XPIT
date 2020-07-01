package com.example.upit.contact;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upit.data.Conversation;
import com.example.upit.data.Users;
import com.example.upit.databinding.ContactItemBinding;
import com.example.upit.databinding.FileItemBinding;
import com.example.upit.util.ClickListener;

import java.util.List;
import java.util.Map;

public class ContactAdapter extends ListAdapter<Conversation, ContactAdapter.ViewHolder> {
    private ClickListener clickListener;
    private Users currentUser;

    public ContactAdapter(ClickListener clickListener, Users currentUser) {
        super(new ContactDiffCallback());
        this.clickListener = clickListener;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ViewHolder.from(parent);
    }

    @Override
    public void submitList(@Nullable List<Conversation> list) {
        notifyDataSetChanged();
        super.submitList(list);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), currentUser, clickListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ContactItemBinding binding;

        public ViewHolder(ContactItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Conversation conversation, Users currentUser, ClickListener clickListener) {
            // Set the text view
            if(conversation.getMembers().indexOf(currentUser.getName()) == 0) {
                binding.textName.setText(conversation.getMembers().get(1));
            } else {
                binding.textName.setText(conversation.getMembers().get(0));
            }

            // Set the caption that shows the most recent message in a conversation
            if(conversation.getRecentMessage() != null && !conversation.getRecentMessage().isEmpty()) {
                binding.textRecentMessage.setText(conversation.getRecentMessage());
            } else {
                binding.textRecentMessage.setText("No recent message");
            }

            binding.getRoot().setOnClickListener(v -> clickListener.onClick(conversation));
        }

        /**
         * Creates the view holder
         */
        public static ViewHolder from(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            ContactItemBinding binding = ContactItemBinding.inflate(layoutInflater, parent, false);
            return new ViewHolder(binding);
        }
    }
}