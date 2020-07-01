package com.example.upit.student;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.upit.data.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;
import java.util.List;

public class StudentViewModel extends ViewModel {
    // Firebase component for the database
    private final FirebaseFirestore firestore;

    // Firebase components for the authentication
    private final FirebaseAuth auth;

    // A live data that will contain the list of users
    private MutableLiveData<List<Users>> students;

    // A live data that will contain a boolean that will serve as a flag to know
    // whether the user would like to add a new student
    private MutableLiveData<Boolean> addStudent;

    // The firestore and auth instances will be injected by hilt (dependency injection)
    @ViewModelInject
    StudentViewModel(FirebaseFirestore firestore, FirebaseAuth auth) {
        // Store the parameters as properties
        this.auth = auth;
        this.firestore = firestore;

        // Create the live data objects;
        this.addStudent = new MutableLiveData<>(false);
        this.students = new MutableLiveData<>(new LinkedList<>());

        // Gets the list of users from the database
        firestore
                // Gets the documents under the users collection (table)
                .collection("/users")
                // Only select the documents (rows / entities) where the accountType is STUDENT
                .whereEqualTo("accountType", "STUDENT")
                // The listener that gets the result of the query.
                // A snapshot listener will listen to the database in realtime.
                // Therefore, it will be triggered when any changes happens in the /users collection.
                // There is no need to recall it.
                .addSnapshotListener((querySnapshots, e) -> {
                    // Creates the initial empty list where the documents / rows will be stored
                    List<Users> users = new LinkedList<>();

                    // Skip trying to get the results if we know that e (exception) contains something
                    // or is the querySnapshot is empty (no results found)
                    if(e == null && querySnapshots != null) {
                        for (QueryDocumentSnapshot doc : querySnapshots) {
                            // Converts the document / row into an instance of the Users class
                            Users user = doc.toObject(Users.class);

                            // Add user to the lists
                            users.add(user);
                        }
                    }

                    // Set the value of the live data to the created list
                    // Using a live data will trigger its observers in the fragment / activity
                    // Which will invoke / run anything inside the observers.
                    students.setValue(users);
                });
    }

    public MutableLiveData<Boolean> getAddStudent() {
        return addStudent;
    }

    public void addNewStudent() {
        addStudent.setValue(true);
    }

    public void studentAdded() {
        addStudent.setValue(false);
    }

    public MutableLiveData<List<Users>> getStudents() {
        return students;
    }
}
