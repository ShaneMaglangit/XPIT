package com.example.upit.newstudent;

import androidx.annotation.NonNull;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.upit.data.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewStudentViewModel extends ViewModel {
    // Firebase component for the database
    private final FirebaseFirestore firestore;

    // Firebase components for the authentication
    private final FirebaseAuth auth;

    // Live data for the input fields.
    // This will be used for two way data binding.
    // Example use case: android:text="@={viewModel.name}
    // If changes in the name live data is occured, the view with the attribute will also update
    // If changes in the view where this is attached, the content of the live data will also change
    public MutableLiveData<String> name;
    public MutableLiveData<String> email;
    public MutableLiveData<String> password;

    // A live data that will contain the error messages
    private MutableLiveData<String> error;

    // A live data that will contain a flag that will serve as a flag for navigating back
    private MutableLiveData<Boolean> navigateBack;

    @ViewModelInject
    public NewStudentViewModel(FirebaseFirestore firestore, FirebaseAuth auth) {
        this.firestore = firestore;
        this.auth = auth;
        this.name = new MutableLiveData<>();
        this.email = new MutableLiveData<>();
        this.password = new MutableLiveData<>();
        this.error = new MutableLiveData<>();
        this.navigateBack = new MutableLiveData<>(false);
    }

    public void createStudent() {
        // Show an error message if the editText for name is empty
        if(name.getValue() == null || name.getValue().isEmpty()) {
            error.setValue("Enter a valid name");
        }
        // Show an error message if the editText for email is empty
        else if(email.getValue() == null || email.getValue().isEmpty()) {
            error.setValue("Enter a valid email");
        }
        // Show an error message if the editText for password is empty
        else if(password.getValue() == null || password.getValue().isEmpty()) {
            error.setValue("Enter a valid password");
        } else {
            // Create a new account with firebase auth by passing the email and password
            auth.createUserWithEmailAndPassword(email.getValue(), password.getValue())
                // Add a listener that will be triggered if the process is complete
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        // After creating the account, store the additional user details to firestore
                        // Create the user object that will hold the fields / columns of the row
                        Users temp = new Users();
                        temp.setName(name.getValue());
                        temp.setAccountType("STUDENT");
                        temp.setEmail(email.getValue());

                        // Use the uid from FirebaseAuth as the document id / primary key
                        // Allows us to easily retrieve the user details from the result of firebaseAuth
                        String uid = task.getResult().getUser().getUid();

                        firestore
                            // Store the user details in the users collection
                            .collection("/users")
                            // Create a document with the uid as its id / primary key
                            .document(uid)
                            // Set the user object as the content of this document
                            .set(temp)
                            .addOnSuccessListener(aVoid -> {
                                // Return after creating the account
                                navigateBack();
                            })
                            .addOnFailureListener(e -> {
                                // Show an error message in case any problem occurs
                                error.setValue(e.getMessage());
                            });
                    } else {
                        // Show an error dialog containing the exception message
                        error.setValue(task.getException().getMessage());
                    }
                });
        }
    }

    public MutableLiveData<String> getError() {
        return error;
    }

    public void closeErrorMessage() {
        error.setValue("");
    }

    public MutableLiveData<Boolean> getNavigateBack() {
        return navigateBack;
    }

    public void navigateBack() {
        navigateBack.setValue(true);
    }

    public void navigateBackDone() {
        navigateBack.setValue(false);
    }
}
