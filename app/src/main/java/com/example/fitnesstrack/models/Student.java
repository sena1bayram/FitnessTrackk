package com.example.fitnesstrack.models;

import java.io.Serializable;

public class Student implements Serializable {

    private String studentId;
    private String address;
    private String birthday;
    private String email;
    private String gender;
    private String height;
    private String password;
    private String teacherId;
    private String type;

    private String username;

    private String weight;

    private String groupId;

    private String studentProfileImage;

    private String registeredDate;
    public String getAddress() {
        return address;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getHeight() {
        return height;
    }

    public String getPassword() {
        return password;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public String getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public String getWeight() {
        return weight;
    }

    public Student(String studentId, String address, String birthday, String email,
                   String gender, String height, String password, String teacherId,
                   String type, String username, String weight, String  groupId,
                   String studentProfileImage, String registeredDate) {
        this.studentId = studentId;
        this.address = address;
        this.birthday = birthday;
        this.email = email;
        this.gender = gender;
        this.height = height;
        this.password = password;
        this.teacherId = teacherId;
        this.type = type;
        this.username = username;
        this.weight = weight;
        this.groupId = groupId;
        this.studentProfileImage = studentProfileImage;
        this.registeredDate = registeredDate;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public String getStudentProfileImage() {
        return studentProfileImage;
    }

    public void setStudentProfileImage(String studentProfileImage) {
        this.studentProfileImage = studentProfileImage;
    }

    public String getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(String registeredDate) {
        this.registeredDate = registeredDate;
    }
}
