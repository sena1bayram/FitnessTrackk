package com.example.fitnesstrack.models;

public class Group {

    private String groupId;

    private String groupName;

    // an empty constructor is
    // required when using
    // Firebase Realtime Database.
    public Group(String groupId, String groupName) {
        this.groupId=groupId;
        this.groupName = groupName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
