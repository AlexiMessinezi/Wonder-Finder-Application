package com.example.wonderfinder.Permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.wonderfinder.Constant.AllConstants;

public class AppPermissions {

    //TASK 3
    //Boolean method to check if storage for photos has been granted
    public boolean checkStorage (Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    //TASK 3
    //Method to ask the user for storage permission
    public void requestStoragePermission (Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE}, AllConstants.STORAGE_REQUEST_CODE);
    }

    //Boolean method to check if location permission has been granted
    public boolean checkLocation (Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    //Method to ask the user for location permissions
    public void requestLocationPermission (Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, AllConstants.LOCATION_REQUEST_CODE);
    }
}
