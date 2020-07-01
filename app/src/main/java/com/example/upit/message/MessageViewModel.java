package com.example.upit.message;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.upit.data.Conversation;
import com.example.upit.data.Message;
import com.example.upit.data.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageViewModel extends ViewModel {
    private FirebaseFirestore firestore;

    private MutableLiveData<Conversation> conversation;
    private MutableLiveData<List<Message>> recentMessages;
    private MutableLiveData<String> error;

    public MutableLiveData<String> message;

    private Users currentUser;

    @ViewModelInject
    public MessageViewModel(FirebaseFirestore firestore) {
        this.firestore = firestore;
        this.conversation = new MutableLiveData<>();
        this.recentMessages = new MutableLiveData<>();
        this.message = new MutableLiveData<>();
        this.error = new MutableLiveData<>();
    }

    public void loadConversation(Conversation con, Users user) {
        this.currentUser = user;

        // Set the live data for conversations to notify observers in the view
        this.conversation.setValue(con);

        // Get the messages for this conversation
        firestore.collection("/conversations")
            .document(con.getId())
            .collection("/messages")
            .orderBy("timeSent", Query.Direction.ASCENDING)
            .addSnapshotListener((queryDocumentSnapshots, e) -> {
                // Create the initial list
                List<Message> tempList = new ArrayList<>();

                // Add the messages to the list
                if(queryDocumentSnapshots != null) {
                    for(DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Message temp = snapshot.toObject(Message.class);
                        tempList.add(temp);
                    }
                }

                // Set the new list to the live data
                recentMessages.setValue(tempList);
            });
    }

    public void sendMessage() {
        if(message.getValue() != null && !message.getValue().isEmpty()) {
            // Create the message objects to be added to the collections
            Message newMessage = new Message();
            newMessage.setSender(currentUser.getName());
            newMessage.setText(message.getValue());
            newMessage.setTimeSent(Timestamp.now());

            firestore.collection("/conversations")
                .document(conversation.getValue().getId())
                .collection("/messages")
                .add(newMessage)
                .addOnSuccessListener(documentReference -> {
                    // Update the conversation fields
                    firestore.collection("/conversations")
                            .document(conversation.getValue().getId())
                            .update("recentlyUpdated", newMessage.getTimeSent());

                    firestore.collection("/conversations")
                            .document(conversation.getValue().getId())
                            .update("recentMessage", newMessage.getText());
                })
                .addOnFailureListener(e -> {
                    // Show an error message if any failure occurs
                    error.setValue(e.getMessage());
                });

            message.setValue("");
        }
    }

    public MutableLiveData<Conversation> getConversation() {
        return conversation;
    }

    public MutableLiveData<String> getError() {
        return error;
    }

    public void closeErrorMessage() {
        error.setValue("");
    }

    public MutableLiveData<List<Message>> getRecentMessages() {
        return recentMessages;
    }
}
