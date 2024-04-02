package com.example.wonderfinder;

public class SettingModel {
    String kilometers, miles, unit;

    public SettingModel() {
    }

    public SettingModel(String kilometers, String miles, String unit) {
        this.kilometers = kilometers;
        this.miles = miles;
        this.unit = unit;
    }

    public String getKilometers() {
        return kilometers;
    }

    public void setKilometers(String kilometers) {
        this.kilometers = kilometers;
    }

    public String getMiles() {
        return miles;
    }

    public void setMiles(String miles) {
        this.miles = miles;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
