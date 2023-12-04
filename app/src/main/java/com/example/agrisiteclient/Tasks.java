package com.example.agrisiteclient;

import java.util.Date;

public class Tasks {
    private String key;

    String title, startdate, enddate, description, taskStatus, fullName, VSDomainFromDB, userIDFromDB;

    public Tasks(String uniqueKey, String title, String description, String startdate, String enddate, String taskStatus, String fullName, String  VSDomainFromDB, String userIDFromDB/*, String division,String location,String FOLatitude ,String FOLongitude*/) {

        this.key = uniqueKey;
        this.title = title;
        this.description = description;
        this.startdate = startdate;
        this.enddate = enddate;
        this.taskStatus = taskStatus;
        this.fullName = fullName;
        this.VSDomainFromDB =  VSDomainFromDB;
        this.userIDFromDB = userIDFromDB;
    }

    // Required no-argument constructor
    public Tasks() {
        // Default constructor required for calls to DataSnapshot.getValue(Tasks.class)
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public String getStartdate() {

        return startdate;
    }

    public void setStartdate(String startdate) {

        this.startdate = startdate;
    }

    public String getEnddate() {

        return enddate;
    }

    public void setEnddate(String enddate) {

        this.enddate = enddate;
    }

    public String getTaskStatus() {

        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {

        this.taskStatus = taskStatus;
    }

    public String getKey() {

        return key;
    }

    public void setKey(String uniqueKey) {
        this.key = uniqueKey;

    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getVSDomainFromDB() {
        return VSDomainFromDB;
    }

    public void setVSDomainFromDB(String VSDomainFromDB) {
        this.VSDomainFromDB =  VSDomainFromDB;
    }


    public String getUserIDFromDB() {
        return userIDFromDB;
    }

    public void setUserIDFromDB(String userIDFromDB) {
        this.userIDFromDB = userIDFromDB;
    }

}