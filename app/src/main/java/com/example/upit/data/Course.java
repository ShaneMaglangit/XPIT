package com.example.upit.data;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.List;

@IgnoreExtraProperties
public class Course {
    @Exclude
    private String courseId;
    private String courseName;
    private String professor;
    private String professorId;
    private HashMap<String, String> students;
    private HashMap<String, String> documents;

    public Course() {
        // Empty constructor
        this.courseName = "";
        this.professor = "";
        this.professorId = "";
        this.students = new HashMap<>();
        this.documents = new HashMap<>();
    }

    public Course(String courseName, String professor, String professorId, HashMap<String, String> students, HashMap<String, String> documents) {
        this.courseName = courseName;
        this.professor = professor;
        this.professorId = professorId;
        this.students = students;
        this.documents = documents;
    }

    @Exclude
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public String getProfessorId() {
        return professorId;
    }

    public void setProfessorId(String professorId) {
        this.professorId = professorId;
    }

    public HashMap<String, String> getStudents() {
        return students;
    }

    public void setStudents(HashMap<String, String> students) {
        this.students = students;
    }

    public HashMap<String, String> getDocuments() {
        return documents;
    }

    public void setDocuments(HashMap<String, String> documents) {
        this.documents = documents;
    }
}

