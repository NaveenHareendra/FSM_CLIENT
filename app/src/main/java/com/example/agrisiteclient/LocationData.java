package com.example.agrisiteclient;

public class LocationData {
    private String key, VSDomainFromDB;
    private double latitude;
    private double longitude;
    String locationName,formattedDateTime;
    private long timestamp;
    public LocationData() {
        // Default constructor required for Firebase
    }


    public LocationData(String uniqueKey, String VSDomainFromDB, double latitude, double longitude, String locationName,long timestamp, String formattedDateTime) {

        this.key = uniqueKey;
        this. VSDomainFromDB =  VSDomainFromDB;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
        this.timestamp = timestamp;
        this.formattedDateTime = formattedDateTime;
    }

    public String getKey() {

        return key;
    }

    public void setKey(String uniqueKey) {
        this.key = uniqueKey;

    }

    public String getVSDomainFromDB() {
        return VSDomainFromDB;
    }

    public void setVSDomainFromDB(String VSDomainFromDB) {
        this.VSDomainFromDB = VSDomainFromDB;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFormattedDateTime() {
        return formattedDateTime;
    }

    public void setFormattedDateTime(String formattedDateTime) {
        this.formattedDateTime = formattedDateTime;
    }
}
