package com.example.wonderfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.wonderfinder.Adapter.DirectionStepAdapter;
import com.example.wonderfinder.Constant.AllConstants;
import com.example.wonderfinder.Model.DirectionLandmarkModel.DirectionDistanceModel;
import com.example.wonderfinder.Model.DirectionLandmarkModel.DirectionLegModel;
import com.example.wonderfinder.Model.DirectionLandmarkModel.DirectionResponseModel;
import com.example.wonderfinder.Model.DirectionLandmarkModel.DirectionRouteModel;
import com.example.wonderfinder.Model.DirectionLandmarkModel.DirectionStepModel;
import com.example.wonderfinder.Model.GoogleLandmarkModel.GoogleResponseModel;
import com.example.wonderfinder.Permissions.AppPermissions;
import com.example.wonderfinder.WebServices.RetrofitAPI;
import com.example.wonderfinder.WebServices.RetrofitClient;
import com.example.wonderfinder.databinding.ActivityDirectionBinding;
import com.example.wonderfinder.databinding.BottomSheetLayoutBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DirectionActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityDirectionBinding binding;
    private GoogleMap mGoogleMap;
    private AppPermissions appPermissions;
    private boolean checkLocationPerm, isTrafficEnable;
    private BottomSheetBehavior<RelativeLayout> bottomSheetBehavior;
    private BottomSheetLayoutBinding bottomSheetLayoutBinding;
    private RetrofitAPI retrofitAPI;
    private Location currentLocation;
    private Double endLat, endLng;
    private String placeId;
    private int currentMode;
    private DirectionStepAdapter adapter;
    private DatabaseReference metricReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDirectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();


        endLat = getIntent().getDoubleExtra("lat", 0.0);
        endLng = getIntent().getDoubleExtra("lng", 0.0);
        placeId = getIntent().getStringExtra("placeId");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appPermissions = new AppPermissions();

        retrofitAPI = RetrofitClient.getRetrofitClient().create(RetrofitAPI.class);

        bottomSheetLayoutBinding = binding.bottomSheet;
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayoutBinding.getRoot());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        adapter = new DirectionStepAdapter();

        bottomSheetLayoutBinding.stepRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bottomSheetLayoutBinding.stepRecyclerView.setAdapter(adapter);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.directionMap);

        mapFragment.getMapAsync(this);

        binding.enableTraffic.setOnClickListener(view -> {
            if (isTrafficEnable) {
                if (mGoogleMap != null) {
                    mGoogleMap.setTrafficEnabled(false);
                    isTrafficEnable = false;
                }
            } else {
                if (mGoogleMap != null) {
                    mGoogleMap.setTrafficEnabled(true);
                    isTrafficEnable = true;
                }
            }
        });


        binding.travelMode.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, int checkedId) {
                if (checkedId != -1) {
                    switch (checkedId) {
                        case R.id.btnChipDriving:
                            getDirection("driving");
                            break;
                        case R.id.btnChipWalking:
                            getDirection("walking");
                            break;
                        case R.id.btnChipBike:
                            getDirection("cycling");
                            break;
                        case R.id.btnChipTrain:
                            getDirection("transit");
                            break;
                    }
                }
            }
        });
    }

    private void getDirection(String mode) {
        if (checkLocationPerm) {
            binding.directionProgress.setVisibility(View.VISIBLE);
            String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() +
                    "&destination=" + endLat + "," + endLng +
                    "&mode=" + mode +
                    "&key=" + getResources().getString(R.string.API_KEY);

            retrofitAPI.getDirection(url).enqueue(new Callback<DirectionResponseModel>() {
                @Override
                public void onResponse(Call<DirectionResponseModel> call, Response<DirectionResponseModel> response) {
                    Gson gson = new Gson();
                    String res = gson.toJson(response.body());
                    Log.d("TAG", "onResponse: " + res);

                    if (response.errorBody() == null) {
                        if (response.body() != null) {
                            clearUI();

                            if (response.body().getDirectionRouteModels().size() > 0) {
                                DirectionRouteModel routeModel = response.body().getDirectionRouteModels().get(0);

                                getSupportActionBar().setTitle(routeModel.getSummary());

                                DirectionLegModel legModel = routeModel.getLegs().get(0);
                                binding.txtStartLocation.setText(legModel.getStartAddress());
                                binding.txtEndLocation.setText(legModel.getEndAddress());

                                bottomSheetLayoutBinding.txtSheetTime.setText(legModel.getDuration().getText());



                                String[] distance = (legModel.getDistance().getText()).split("\\s");
                                //0.8 mi
                                double dist = Double.parseDouble(distance[0]);
                                firebaseAuth = FirebaseAuth.getInstance();

                                user = firebaseAuth.getCurrentUser();

                                metricReference = FirebaseDatabase.getInstance().getReference("Users")
                                        .child(firebaseAuth.getUid()).child("Metric");

                                metricReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            String mu = snapshot.getValue(String.class);
                                            if (mu.equals("Kilometers")) {
                                                double kilometers = dist * 1.609344;
                                                bottomSheetLayoutBinding.txtSheetDistance.setText((Math.round(kilometers * 100) / 100.0) + " km");
                                            } else {
                                                double miles = dist;// / 621.371;
                                                bottomSheetLayoutBinding.txtSheetDistance.setText(legModel.getDistance().getText());
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                //bottomSheetLayoutBinding.txtSheetDistance.setText(legModel.getDistance().getText());

                                mGoogleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(legModel.getEndLocation().getLat(), legModel.getEndLocation().getLng()))
                                        .title("End Location"));

                                mGoogleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(legModel.getStartLocation().getLat(), legModel.getStartLocation().getLng()))
                                        .title("Start Location"));

                                adapter.setDirectionStepModels(legModel.getSteps());

                                List<LatLng> stepList = new ArrayList<>();

                                PolylineOptions options = new PolylineOptions()
                                        .width(25)
                                        .color(Color.BLUE)
                                        .geodesic(true)
                                        .clickable(true)
                                        .visible(true);

                                List<PatternItem> pattern;
                                if (mode.equals("walking")) {
                                    pattern = Arrays.asList(
                                            new Dot(), new Gap(10));

                                    options.jointType(JointType.ROUND);
                                } else {
                                    pattern = Arrays.asList(
                                            new Dash(30));
                                }

                                options.pattern(pattern);

                                for (DirectionStepModel stepModel : legModel.getSteps()) {
                                    List<com.google.maps.model.LatLng> decodedLatLng = decode(stepModel.getPolyline().getPoints());
                                    for (com.google.maps.model.LatLng latLng : decodedLatLng) {
                                        stepList.add(new LatLng(latLng.lat, latLng.lng));
                                    }
                                }

                                options.addAll(stepList);

                                Polyline polyline = mGoogleMap.addPolyline(options);

                                LatLng startLocation = new LatLng(legModel.getStartLocation().getLat(), legModel.getStartLocation().getLng());
                                LatLng endLocation = new LatLng(legModel.getStartLocation().getLat(), legModel.getStartLocation().getLng());

                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(startLocation, endLocation), 17));
                            } else {
                                Toast.makeText(DirectionActivity.this, "No route find", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DirectionActivity.this, "No route find", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("TAG", "onResponse: " + response);
                    }
                    binding.directionProgress.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFailure(Call<DirectionResponseModel> call, Throwable t) {

                }
            });
        }
    }

    //Method to clear the user interface
    private void clearUI() {
        mGoogleMap.clear();
        binding.txtStartLocation.setText("");
        binding.txtEndLocation.setText("");
        getSupportActionBar().setTitle("");
        bottomSheetLayoutBinding.txtSheetDistance.setText("");
        bottomSheetLayoutBinding.txtSheetTime.setText("");
    }

    //Google onMapReady method
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if (appPermissions.checkLocation(this)) {
            checkLocationPerm = true;
            setupGoogleMap();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission")
                        .setMessage("Wonder Finder requires permission to show you near by places")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                appPermissions.requestLocationPermission(DirectionActivity.this);
                            }
                        })
                        .create().show();
            } else {
                appPermissions.requestLocationPermission(DirectionActivity.this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == AllConstants.LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationPerm = true;
                setupGoogleMap();
            } else {
                checkLocationPerm = false;
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupGoogleMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setCompassEnabled(false);

        getCurrentLocation();
    }

    private void getCurrentLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;

                    getDirection("driving");
                } else {
                    Toast.makeText(DirectionActivity.this, "Location Not Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else

        super.onBackPressed();
    }

    private List<com.google.maps.model.LatLng> decode(String points) {

        int len = points.length();

        final List<com.google.maps.model.LatLng> path = new ArrayList<>(len / 2);
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int result = 1;
            int shift = 0;
            int b;
            do {
                b = points.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            result = 1;
            shift = 0;
            do {
                b = points.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            path.add(new com.google.maps.model.LatLng(lat * 1e-5, lng * 1e-5));
        }

        return path;

    }
}