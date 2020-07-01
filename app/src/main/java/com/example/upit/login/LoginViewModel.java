package com.example.upit.login;

import androidx.annotation.NonNull;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.upit.data.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LoginViewModel extends ViewModel {
    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;

    public MutableLiveData<String> email;
    public MutableLiveData<String> password;

    private MutableLiveData<String> error;
    private MutableLiveData<Users> user;

    @ViewModelInject
    LoginViewModel(FirebaseAuth auth, FirebaseFirestore firestore) {
        this.auth = auth;
        this.firestore = firestore;
        this.email = new MutableLiveData<>();
        this.password = new MutableLiveData<>();
        this.user = new MutableLiveData<Users>();
        this.error = new MutableLiveData<String>("");
    }

    public void login() {
        // Show an error message if the editText for email is empty
        if(email.getValue() == null || email.getValue().isEmpty()) {
            error.setValue("Please enter a valid email");
        }
        // Show an error message if the editText for password is empty
        else if(password.getValue() == null || password.getValue().isEmpty()) {
            error.setValue("Please enter a valid password");
        }
        else {
            // Sign in with firebase auth by passing the email and password
            Task task = auth.signInWithEmailAndPassword(email.getValue(), password.getValue());

            // Navigate to main activity if successful
            task.addOnSuccessListener(o -> {
                // Get a copy of the uid of the current signed in user
                String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();

                // Gets the extra user details stored in firestore
                firestore
                        // Get the data under the users collections
                        .collection("/users")
                        // Get the document where the id matches with the user uid from firebase auth
                        .document(uid)
                        // Perform the query
                        .get()
                        // Create the listeners
                        // This will be invoked if the query is successful
                        .addOnSuccessListener(documentSnapshot -> {
                            // Convert the document into an instance of the Users class
                            Users tmpUser = documentSnapshot.toObject(Users.class);

                            // Show an error if any problem occurered while retrieving the user detail
                            if(tmpUser == null) {
                                error.setValue("An error occurred while parsing the user data");
                            }
                            // Set vale of the user live data to the retrieved document
                            else {
                                tmpUser.setId(uid);
                                user.setValue(tmpUser);
                            }
                        })
                        // This will be invoked if the query failed
                        .addOnFailureListener(e -> {
                            error.setValue(e.getMessage());
                        });
            });

            // Show error message on fail
            task.addOnFailureListener(e -> error.setValue(e.getMessage()));
        }
    }


    public MutableLiveData<String> getError() {
        return error;
    }

    public MutableLiveData<Users> getUser() {
        return user;
    }

    public void loggedIn() {
        user.setValue(null);
    }

    public void closeErrorMessage() {
        error.setValue("");
    }
}
