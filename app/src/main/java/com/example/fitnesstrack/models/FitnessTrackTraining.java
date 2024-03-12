package com.example.fitnesstrack.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FitnessTrackTraining implements Serializable {
    private String trainingId;
    private String coachId;
    private String groupName;
    private Tasks tasks;
    private String startDate;
    private String endDate;
    private boolean isCompleted;

    public FitnessTrackTraining() {
        // Firebase Realtime Database gereksinimleri için boş bir yapıcı metod gereklidir.
    }
    public FitnessTrackTraining(String trainingId, String coachId, String groupName,
                                Tasks tasks, String startDate,
                                String endDate, boolean isCompleted) {
        this.trainingId = trainingId;
        this.coachId = coachId;
        this.groupName = groupName;
        this.tasks = tasks;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isCompleted = isCompleted;
    }

    public String getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(String trainingId) {
        this.trainingId = trainingId;
    }

    public String getCoachId() {
        return coachId;
    }

    public void setCoachId(String coachId) {
        this.coachId = coachId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Tasks getTasks() {
        return tasks;
    }

    public void setTasks(Tasks tasks) {
        this.tasks = tasks;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate.toString();
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate.toString();
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
