package com.example.fitnesstrack.models;

import java.io.Serializable;
import java.util.List;

public class Coach implements Serializable {
    // string variable for
    // storing employee name.
    private String personEmail;

    private String coachId;

    private String personName;

    // string variable for storing
    // employee contact number
    private String personPassword;

    private String personType;

    private List<Student> studentList; // Öğrencileri temsil eden liste

    private List<Group> groupList; // Öğrenci Grupları temsil eden liste

    private String personProfileImage;

    private String registeredDate;
    // string variable for storing
    // employee address.

    // an empty constructor is
    // required when using
    // Firebase Realtime Database.
    public Coach(String CoachId,String personEmail, String personName, String personPassword,
                 String personType, String personProfileImage, String registeredDate) {
        this.coachId =CoachId;
        this.personEmail = personEmail;
        this.personName = personName;
        this.personPassword = personPassword;
        this.personType = personType;
        this.personProfileImage = personProfileImage;
        this.registeredDate = registeredDate;
    }

    // created getter and setter methods
    // for all our variables.
    public String getpersonName() {
        return personName;
    }

    public void setpersonName(String personName) {
        this.personName = personName;
    }

    public String getpersonEmail() {
        return personEmail;
    }

    public void setpersonEmail(String personEmail) {
        this.personEmail = personEmail;
    }

    public String getPersonPassword() {
        return personPassword;
    }

    public void setPersonPassword(String personPassword) {
        this.personPassword = personPassword;
    }

    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

    public List<Student> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }
    public String getCoachId() {
        return coachId;
    }

    public void setCoachId(String coachId) {
        this.coachId = coachId;
    }
    public String getPersonProfileImage() {
        return personProfileImage;
    }

    public void setPersonProfileImage(String personProfileImage) {
        this.personProfileImage = personProfileImage;
    }

    public String getPersonEmail() {
        return personEmail;
    }

    public void setPersonEmail(String personEmail) {
        this.personEmail = personEmail;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(String registeredDate) {
        this.registeredDate = registeredDate;
    }
}
