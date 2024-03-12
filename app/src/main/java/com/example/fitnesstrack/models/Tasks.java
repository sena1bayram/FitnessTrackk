package com.example.fitnesstrack.models;

import java.io.Serializable;

public class Tasks implements Serializable {
    private String taskName;
    private String taskInformation;
    private String taskRegisteredDate;
    public Tasks(String taskName, String taskInformation, String taskRegisteredDate) {
        this.taskName = taskName;
        this.taskInformation = taskInformation;
        this.taskRegisteredDate = taskRegisteredDate;
    }
    public Tasks() {
        // Boş yapıcı metot gereklidir
    }
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskInformation() {
        return taskInformation;
    }

    public void setTaskInformation(String taskInformation) {
        this.taskInformation = taskInformation;
    }

    public String getTaskRegisteredDate() {
        return taskRegisteredDate;
    }

    public void setTaskRegisteredDate(String taskRegisteredDate) {
        this.taskRegisteredDate = taskRegisteredDate;
    }
}
