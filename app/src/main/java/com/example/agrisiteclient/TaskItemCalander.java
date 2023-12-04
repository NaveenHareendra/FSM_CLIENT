package com.example.agrisiteclient;

public class TaskItemCalander {
    private String key;

    String title, startdate, enddate, description, taskstatus, fullName, VSDomainFromDB, userIDFromDB;

    public TaskItemCalander() {
    }

    public TaskItemCalander(String key, String title, String startdate, String enddate, String description, String taskstatus, String fullName, String VSDomainFromDB, String userIDFromDB) {
        this.key = key;
        this.title = title;
        this.startdate = startdate;
        this.enddate = enddate;
        this.description = description;
        this.taskstatus = taskstatus;
        this.fullName = fullName;
        this.VSDomainFromDB = VSDomainFromDB;
        this.userIDFromDB = userIDFromDB;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }
}
