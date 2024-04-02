package com.example.wonderfinder;

public class LandmarkModel {
    int id, drawableId;
    String name;
    String landmarkType;

    public LandmarkModel() {
    }

    public LandmarkModel(int id, int drawableId, String name, String landmarkType) {
        this.id = id;
        this.drawableId = drawableId;
        this.name = name;
        this.landmarkType = landmarkType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLandmarkType() {
        return landmarkType;
    }

    public void setLandmarkType(String landmarkType) {
        this.landmarkType = landmarkType;
    }
}
