package com.example.wonderfinder.Utils;

//Code taken from stack overflow
//and adjusted to suit the scenario
//https://stackoverflow.com/questions/31877320/android-extends-application-and-make-an-instance-of-it
//Author: Kuffs
//Author profile: https://stackoverflow.com/users/1027277/kuffs

public class WonderFinderAPI {
    //Declare username as a private variable of type string
    private String username;
    //Declare userID as a private variable of type string
    private String userID;

    private String unit;
    //Creating an instance of the StashApi class
    private static WonderFinderAPI instance;

    //Method to return the instance
    public static WonderFinderAPI getInstance() {
        if (instance == null)
            instance = new WonderFinderAPI();
        return instance;
    }

    //Empty constructor of StashApi
    public WonderFinderAPI() {}

    //Getter and setter methods for the username and userID
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
