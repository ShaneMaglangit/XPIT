package com.example.upit.courselist;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.upit.data.Course;
import com.example.upit.data.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CourseListViewModel extends ViewModel {
    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;

    private MutableLiveData<List<Map.Entry<String, String>>> courses;
    private MutableLiveData<String> error;

    @ViewModelInject
    public CourseListViewModel(FirebaseAuth auth, FirebaseFirestore firestore) {
        this.auth = auth;
        this.firestore = firestore;
        this.error = new MutableLiveData<>();
        this.courses = new MutableLiveData<>();
    }

    public void loadUserInfo(Users currentUser) {
        // Listen to updates on courses
        firestore.collection("/users")
                .document(currentUser.getId())
                .addSnapshotListener((documentSnapshot, e) -> {
                    if(documentSnapshot != null && documentSnapshot.exists()) {
                        Users user = documentSnapshot.toObject(Users.class);
                        Set<Map.Entry<String, String>> courseSet = user.getCourses().entrySet();
                        courses.setValue(new LinkedList<>(courseSet));
                    }
                });
    }

    public void saveCourse(Users user, String courseName) {
        Course course = new Course();
        course.setCourseName(courseName);
        course.setProfessor(user.getName());
        course.setProfessorId(user.getId());

        firestore.collection("/courses").add(course)
                .addOnSuccessListener(documentReference -> {
                    user.getCourses().put(documentReference.getId(), courseName);
                    firestore.collection("/users")
                            .document(user.getId())
                            .update("courses", user.getCourses());
                })
                .addOnFailureListener(e -> {
                    showError(e.getMessage());
                });
    }

    public MutableLiveData<List<Map.Entry<String, String>>> getCourses() {
        return courses;
    }

    public MutableLiveData<String> getError() {
        return error;
    }

    public void showError(String message) {
        error.setValue(message);
    }

    public void closeErrorMessage() {
        error.setValue("");
    }
}
