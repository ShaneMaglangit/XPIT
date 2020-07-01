package com.example.upit.contact;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.upit.data.Conversation;
import com.example.upit.data.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ContactViewModel extends ViewModel {
    private final FirebaseFirestore firestore;
    private final FirebaseAuth auth;

    private Users currentUser;

    private MutableLiveData<List<Conversation>> contacts;
    private MutableLiveData<Boolean> emailInput;
    private MutableLiveData<String> error;
    private MutableLiveData<Conversation> openConversation;

    @ViewModelInject
    public ContactViewModel(FirebaseFirestore firestore, FirebaseAuth auth) {
        this.firestore = firestore;
        this.auth = auth;
        this.contacts = new MutableLiveData<>(new LinkedList<>());
        this.emailInput = new MutableLiveData<>(false);
        this.error = new MutableLiveData<>("");
        this.openConversation = new MutableLiveData<>();
    }

    public void loadContacts(Users user) {
        this.currentUser = user;

        // Load the list of conversations
        firestore.collection("/conversations")
                .whereArrayContains("members", currentUser.getName())
                .orderBy("recentlyUpdated", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    // Create the initial list
                    LinkedList<Conversation> temp = new LinkedList<>();

                    // Add the conversations to the list
                    if(queryDocumentSnapshots != null) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Conversation con = doc.toObject(Conversation.class);
                            con.setId(doc.getId());
                            temp.add(con);
                        }
                    }

                    // Set the live data to have the conversations
                    contacts.setValue(temp);
                });
    }

    public void createConversation(String email) {
        if(email.equals(currentUser.getEmail())) {
            showError("Cannot send message to yourself");
        } else {
            firestore.collection("/users")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if(!queryDocumentSnapshots.isEmpty()) {
                        Users target = queryDocumentSnapshots.getDocuments().get(0).toObject(Users.class);

                        // Open existing conversation
                        if(contacts.getValue() != null) {
                            for (Conversation contact : contacts.getValue()) {
                                if (contact.getMembers().contains(target.getName())) {
                                    openConversation.setValue(contact);
                                    return;
                                }
                            }
                        }

                        // Create conversation
                        Conversation newConversation = new Conversation();
                        newConversation.getMembers().add(currentUser.getName());
                        newConversation.getMembers().add(target.getName());
                        newConversation.setRecentlyUpdated(Timestamp.now());

                        // Save new conversation to the database
                        firestore.collection("/conversations").add(newConversation)
                                .addOnSuccessListener(documentReference -> {
                                    newConversation.setId(documentReference.getId());
                                    openConversation.setValue(newConversation);
                                })
                                .addOnFailureListener(e -> {
                                    showError(e.getMessage());
                                });
                    } else {
                        showError("User does not exists");
                    }
                })
                .addOnFailureListener(e -> {
                    showError(e.getMessage());
                });
        }
    }

    public void conversationShown() {
        openConversation.setValue(null);
    }

    public void promptEmailInput() {
        emailInput.setValue(true);
    }

    public void emailInputShown() {
        emailInput.setValue(false);
    }

    public void showError(String message) {
        error.setValue(message);
    }

    public void closeErrorMessage() {
        error.setValue("");
    }

    public MutableLiveData<Conversation> getOpenConversation() {
        return openConversation;
    }

    public MutableLiveData<List<Conversation>> getContacts() {
        return contacts;
    }

    public MutableLiveData<Boolean> getEmailInput() {
        return emailInput;
    }

    public MutableLiveData<String> getError() {
        return error;
    }
}
