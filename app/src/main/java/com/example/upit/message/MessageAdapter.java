package com.example.upit.message;


import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upit.R;
import com.example.upit.data.Message;
import com.example.upit.data.Users;
import com.example.upit.databinding.MessageItemBinding;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MessageAdapter extends ListAdapter<Message, MessageAdapter.ViewHolder> {
    private Users currentUser;

    public MessageAdapter(Users currentUser) {
        super(new MessageDiffCallback());
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ViewHolder.from(parent);
    }

    @Override
    public void submitList(@Nullable List<Message> list) {
        // Force update the recycler view
        notifyDataSetChanged();

        // Submit the list
        super.submitList(list);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        // Binds the message to the viewholder
        holder.bind(getItem(position), currentUser);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private MessageItemBinding binding;

        public ViewHolder(MessageItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Message message, Users currentUser) {
            // Update the views and binding accordingly
            if(!message.getSender().equals(currentUser.getName())) {
                binding.getRoot().setGravity(Gravity.START);
            }

            binding.textMessage.setText(message.getText());
        }

        public static MessageAdapter.ViewHolder from(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            MessageItemBinding binding = MessageItemBinding.inflate(layoutInflater, parent, false);
            return new MessageAdapter.ViewHolder(binding);
        }
    }
}