package com.example.wonderfinder.Model.GoogleLandmarkModel;

import com.example.wonderfinder.GoogleLandmarkModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GoogleResponseModel {

    @SerializedName("results")
    @Expose

    private List<GoogleLandmarkModel> googleLandmarkModels;

    public List<GoogleLandmarkModel> getGoogleLandmarkModels() {
        return googleLandmarkModels;
    }

    public void setGoogleLandmarkModels(List<GoogleLandmarkModel> googleLandmarkModels) {
        this.googleLandmarkModels = googleLandmarkModels;
    }
}
