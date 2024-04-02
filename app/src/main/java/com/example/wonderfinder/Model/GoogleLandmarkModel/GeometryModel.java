package com.example.wonderfinder.Model.GoogleLandmarkModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//This class returns the location of the landmark
public class GeometryModel {

    @SerializedName("location")
    @Expose
    private LocationModel location;


    public LocationModel getLocation() {
        return location;
    }

    public void setLocation(LocationModel location) {
        this.location = location;
    }

}
