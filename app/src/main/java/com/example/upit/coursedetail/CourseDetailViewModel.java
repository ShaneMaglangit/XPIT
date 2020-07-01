package com.example.upit.coursedetail;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.upit.data.Course;
import com.example.upit.data.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CourseDetailViewModel extends ViewModel {
    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;
    private final FirebaseStorage storage;
    private final DownloadManager downloadManager;

    private MutableLiveData<Course> course;
    private MutableLiveData<List<Map.Entry<String, String>>> documents;
    private MutableLiveData<List<Map.Entry<String, String>>> students;
    private MutableLiveData<Boolean> emailInput;
    private MutableLiveData<Boolean> fileInput;
    private MutableLiveData<Boolean> uploading;
    private MutableLiveData<String> error;


    @ViewModelInject
    public CourseDetailViewModel(FirebaseAuth auth, FirebaseFirestore firestore, FirebaseStorage storage, DownloadManager downloadManager) {
        this.auth = auth;
        this.firestore = firestore;
        this.storage = storage;
        this.downloadManager = downloadManager;
        this.documents = new MutableLiveData<>(new LinkedList<>());
        this.students = new MutableLiveData<>(new LinkedList<>());
        this.course = new MutableLiveData<>();
        this.emailInput = new MutableLiveData<>(false);
        this.fileInput = new MutableLiveData<>(false);
        this.uploading = new MutableLiveData<>(false);
    }

    public void loadCourse(String id) {
        firestore.collection("/courses")
            .document(id)
            .addSnapshotListener((documentSnapshot, e) -> {
                if(documentSnapshot != null && documentSnapshot.exists()) {
                    Course temp = documentSnapshot.toObject(Course.class);
                    temp.setCourseId(documentSnapshot.getId());
                    course.setValue(temp);
                }
            });
    }

    public void addStudent(String email) {
        firestore.collection("/users")
            .whereEqualTo("email", email)
            .limit(1)
            .get()
            .addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    if(task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot snapshot = task.getResult().getDocuments().get(0);
                        Users student = snapshot.toObject(Users.class);
                        student.setId(snapshot.getId());

                        if(course.getValue().getStudents().containsKey(student.getId())) {
                            error.setValue("Student already added");
                        } else {
                            // Update course
                            course.getValue().getStudents().put(student.getId(), student.getName());
                            firestore.collection("/courses")
                                    .document(course.getValue().getCourseId())
                                    .update("students", course.getValue().getStudents());

                            // Update user
                            student.getCourses().put(course.getValue().getCourseId(), course.getValue().getCourseName());
                            firestore.collection("/users")
                                    .document(student.getId())
                                    .update("courses", student.getCourses());
                        }
                    } else {
                        error.setValue("Student not found");
                    }
                } else {
                    error.setValue(task.getException().getMessage());
                }
            });
    }

    public void downloadFile(String path) {
        StorageReference fileRef = storage.getReference().child(path);

        fileRef.getDownloadUrl()
            .addOnSuccessListener(uri -> {
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, path.replace("/", ""));
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.allowScanningByMediaScanner();
                downloadManager.enqueue(request);
            })
            .addOnFailureListener(e -> {
                showError(e.getMessage());
            });
    }

    public void uploadFile(String filename, Uri uri) {
        // Create the reference and task
        StorageReference reference = storage.getReference().child("/" + filename);
        Task fileTask = reference.putFile(uri);

        // Show the loading dialog
        uploading.setValue(true);

        fileTask.addOnCompleteListener(task -> {
            // Hide the loading dialog
            uploading.setValue(false);
            if(task.isSuccessful()) {
                course.getValue().getDocuments().put(reference.getPath(), filename);
                firestore.collection("/courses")
                        .document(course.getValue().getCourseId())
                        .update("documents", course.getValue().getDocuments());
            } else {
                error.setValue(task.getException().getMessage());
            }
        });
    }

    public void updateLists() {
        // Convert the map to a list of entry set to let the recyclerview be able to handle it
        Set<Map.Entry<String, String>> studentSet = course.getValue().getStudents().entrySet();
        students.setValue(new LinkedList<>(studentSet));

        Set<Map.Entry<String, String>> documentSet = course.getValue().getDocuments().entrySet();
        documents.setValue(new LinkedList<>(documentSet));
    }

    public MutableLiveData<String> getError() {
        if(error == null) {
            error = new MutableLiveData<>();
        }
        return error;
    }

    public void promptFileInput() {
        fileInput.setValue(true);
    }

    public void fileInputShown() {
        fileInput.setValue(false);
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

    public void uploadingComplete() {
        uploading.setValue(false);
    }

    public MutableLiveData<Boolean> getUploading() {
        return uploading;
    }

    public MutableLiveData<Boolean> getFileInput() {
        return fileInput;
    }

    public MutableLiveData<Boolean> getEmailInput() {
        return emailInput;
    }

    public MutableLiveData<List<Map.Entry<String, String>>> getDocuments() {
        return documents;
    }

    public MutableLiveData<List<Map.Entry<String, String>>> getStudents() {
        return students;
    }

    public MutableLiveData<Course> getCourse() {
        return course;
    }
}
