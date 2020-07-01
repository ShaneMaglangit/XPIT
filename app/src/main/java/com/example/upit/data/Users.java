package com.example.upit.data;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@IgnoreExtraProperties
public class Users implements Serializable {
    @Exclude
    private String id;
    private String name;
    private String accountType;
    private HashMap<String, String> courses;
    private String email;
    private List<String> conversations;

    public Users() {
        courses = new HashMap<>();
        conversations = new LinkedList<>();
    }

    public Users(String id, String name, String accountType, HashMap<String, String> courses, String email, List<String> conversations) {
        this.id = id;
        this.name = name;
        this.accountType = accountType;
        this.courses = courses;
        this.email = email;
        this.conversations = conversations;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public HashMap<String, String> getCourses() {
        return courses;
    }

    public void setCourses(HashMap<String, String> courses) {
        this.courses = courses;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getConversations() {
        return conversations;
    }

    public void setConversations(List<String> conversations) {
        this.conversations = conversations;
    }
}
