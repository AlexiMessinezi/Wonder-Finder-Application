package com.example.wonderfinder.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.wonderfinder.databinding.FragmentHomeBinding;
import com.example.wonderfinder.databinding.FragmentSettingsBinding;
import com.example.wonderfinder.databinding.InfoWindowLayoutBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    //Declare all variables as private variables with their respective types
    private InfoWindowLayoutBinding binding;
    private Location location;
    private Context context;
    private FragmentSettingsBinding settingsBinding;
    private SharedPreferences preferences;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private DatabaseReference metricReference;

    //InfoWindowAdapter constructor to bind the layout
    public InfoWindowAdapter(Location location, Context context) {
        this.location = location;
        this.context = context;

        binding = InfoWindowLayoutBinding.inflate(LayoutInflater.from(context), null, false);
    }

    //Android studio view method to get contents
    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        binding.txtLocationName.setText(marker.getTitle());

        double distance = SphericalUtil.computeDistanceBetween(new LatLng(location.getLatitude(), location.getLongitude()),
                marker.getPosition());

        firebaseAuth = FirebaseAuth.getInstance();

        user = firebaseAuth.getCurrentUser();

        metricReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseAuth.getUid()).child("Metric");

        //Checks the users metric choice for the distance calculation
        metricReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String mu = snapshot.getValue(String.class);
                    if (mu.equals("Kilometers")) {
                        if (distance > 1000) {
                            double kilometers = distance / 1000;
                            binding.txtLocationDistance.setText((Math.round(kilometers * 100) / 100.0) + " KM");
                        } else {
                            binding.txtLocationDistance.setText((Math.round(distance * 100) / 100.0) + " Meters");
                        }
                    } else {
                        if (distance > 1000) {
                            double miles = distance / 621.371;
                            binding.txtLocationDistance.setText((Math.round(miles * 100) / 100.0) + " MI");
                        } else {
                            binding.txtLocationDistance.setText((Math.round(distance * 100) / 100.0) + " Meters");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        float speed = location.getSpeed();

        //Calculates the time to the location
        if (speed > 0) {
            BigDecimal time = new BigDecimal(distance / speed);
            //binding.txtLocationTime.setText(( + " Minutes");
            //binding.txtLocationTime.setText((Math.round(time * 100.0) / 100.0) + " Minutes");
            //double mins = time / 60;
            //binding.txtLocationTime.setText((Math.round(mins * 100.0) / 100.0) + " Minutes");
            //double newMin = mins * 100 / 100;
            //binding.txtLocationTime.setText(String.format("%.2f",newMin) + " Minutes");
        } else {
            binding.txtLocationTime.setText("N/A");
        }
        return binding.getRoot();
    }

    //Android studio method to get the information from the window
    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        binding.txtLocationName.setText(marker.getTitle());

        double distance = SphericalUtil.computeDistanceBetween(new LatLng(location.getLatitude(), location.getLongitude()),
                marker.getPosition());

        firebaseAuth = FirebaseAuth.getInstance();

        user = firebaseAuth.getCurrentUser();

        metricReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseAuth.getUid()).child("Metric");

        //Checks the users metric choice for the distance calculation
        metricReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String mu = snapshot.getValue(String.class);
                    if (mu.equals("Kilometers")) {
                        if (distance > 1000) {
                            double kilometers = distance / 1000;
                            binding.txtLocationDistance.setText((Math.round(kilometers * 100) / 100.0) + " KM");
                        } else {
                            binding.txtLocationDistance.setText((Math.round(distance * 100) / 100.0) + " Meters");
                        }
                    } else {
                        if (distance > 1000) {
                            double miles = distance / 621.371;
                            binding.txtLocationDistance.setText((Math.round(miles * 100) / 100.0) + " MI");
                        } else {
                            binding.txtLocationDistance.setText((Math.round(distance * 100) / 100.0) + " Meters");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        float speed = location.getSpeed();

        //Calculates the time to the location
        if (speed > 0) {
            double time = distance / speed;
            binding.txtLocationTime.setText((Math.round(time * 100.0) / 100.0) + " Minutes");
            //double mins = time / 60;
            //binding.txtLocationTime.setText((Math.round(mins * 100.0) / 100.0) + " Minutes");
            //double newMin = mins * 100 / 100;
            //binding.txtLocationTime.setText(String.format("%.2f",newMin) + " Minutes");
        } else {
            binding.txtLocationTime.setText("N/A");
        }
        return binding.getRoot();
    }

}
