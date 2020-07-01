package com.example.upit.data;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@IgnoreExtraProperties
public class Conversation implements Serializable {
    private String id;
    private List<String> members;
    private Timestamp recentlyUpdated;
    private String recentMessage;

    public Conversation() {
        this.members = new ArrayList<>();
        this.recentMessage = "";
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public Timestamp getRecentlyUpdated() {
        return recentlyUpdated;
    }

    public void setRecentlyUpdated(Timestamp recentlyUpdated) {
        this.recentlyUpdated = recentlyUpdated;
    }

    public String getRecentMessage() {
        return recentMessage;
    }

    public void setRecentMessage(String recentMessage) {
        this.recentMessage = recentMessage;
    }
}
