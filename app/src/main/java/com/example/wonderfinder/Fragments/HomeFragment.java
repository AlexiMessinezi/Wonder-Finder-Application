package com.example.wonderfinder.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.wonderfinder.Adapter.GoogleLandmarkAdapter;
import com.example.wonderfinder.Adapter.InfoWindowAdapter;
import com.example.wonderfinder.Constant.AllConstants;
import com.example.wonderfinder.DirectionActivity;
import com.example.wonderfinder.GoogleLandmarkModel;
import com.example.wonderfinder.LandmarkModel;
import com.example.wonderfinder.Model.GoogleLandmarkModel.GoogleResponseModel;
import com.example.wonderfinder.NearLocationInterface;
import com.example.wonderfinder.Permissions.AppPermissions;
import com.example.wonderfinder.R;
import com.example.wonderfinder.SavedLandmarkModel;
import com.example.wonderfinder.WebServices.RetrofitAPI;
import com.example.wonderfinder.WebServices.RetrofitClient;
import com.example.wonderfinder.databinding.FragmentHomeBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements OnMapReadyCallback,
GoogleMap.OnMarkerClickListener, NearLocationInterface {

    //Declare all variables as private variables with their respective types
    private FragmentHomeBinding binding;
    private GoogleMap mGoogleMap;
    private AppPermissions appPermissions;
    private boolean checkLocationPerms, checkTraffic;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;
    private FirebaseAuth firebaseAuth;
    private Marker currentMarker;
    private int radius = 5000;
    private RetrofitAPI retrofitAPI;
    private List<GoogleLandmarkModel> googleLandmarkModelList;
    private LandmarkModel selectedLandmarkModel;
    private GoogleLandmarkAdapter googleLandmarkAdapter;
    private InfoWindowAdapter infoWindowAdapter;
    private ArrayList<String> userSavedLocation;
    private DatabaseReference locationReference, userLocationReference, metricReference;
    private FirebaseUser user;

    //Empty constructor for the HomeFragment
    public HomeFragment() {
    }

    //Android studio onCreateView method
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        //Set the variables to their respective values
        appPermissions = new AppPermissions();
        firebaseAuth = FirebaseAuth.getInstance();
        retrofitAPI = RetrofitClient.getRetrofitClient().create(RetrofitAPI.class);
        googleLandmarkModelList = new ArrayList<>();
        userSavedLocation = new ArrayList<>();
        user = firebaseAuth.getCurrentUser();

        //Database references to fetch the users data from firebase
        locationReference = FirebaseDatabase.getInstance().getReference("Landmarks");
        userLocationReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseAuth.getUid()).child("Saved Locations");
        metricReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseAuth.getUid()).child("Metric");

        //Buttons for the different maptypes
        binding.btnMapType.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), view);
            popupMenu.getMenuInflater().inflate(R.menu.map_type_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.btnNormal:
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    case R.id.btnSatellite:
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case R.id.btnTerrain:
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;
                }
                return true;
            });

            popupMenu.show();
        });

        //OnClickListener for the enable traffic button
        binding.btnEnableTraffic.setOnClickListener(view -> {
            if (checkTraffic) {
                if (mGoogleMap != null) {
                    mGoogleMap.setTrafficEnabled(false);
                    checkTraffic = false;
                }
            } else {
                if (mGoogleMap != null) {
                    mGoogleMap.setTrafficEnabled(true);
                    checkTraffic = true;
                }
            }
        });

        //onClickListener for btnCurrentLocation to fetch the current location method
        binding.btnCurrentLocation.setOnClickListener(currentLocation -> getCurrentLocation());

        //onCheckedChangeListener for landmarkGroup to get the landmarks time
        binding.landmarkGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, int checkedId) {
                if (checkedId != -1) {
                    LandmarkModel landmarkModel = AllConstants.landmarksName.get(checkedId - 1);
                    binding.edtLandmarkName.setText(landmarkModel.getName());
                    selectedLandmarkModel = landmarkModel;
                    getLandmarks(landmarkModel.getLandmarkType());
                }
            }
        });

        return binding.getRoot();
    }

    //Android studio onViewCreated method
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.homeMap);

        mapFragment.getMapAsync(this);

        //Setting the constraints and values of the chip for landmarks
        for (LandmarkModel landmarkModel : AllConstants.landmarksName) {
            Chip chip = new Chip(requireContext());
            chip.setText(landmarkModel.getName());
            chip.setId(landmarkModel.getId());
            chip.setPadding(8, 8, 8, 8);
            chip.setTextColor(getResources().getColor(R.color.white, null));
            chip.setChipBackgroundColor(getResources().getColorStateList(R.color.primaryColor, null));
            chip.setChipIcon(ResourcesCompat.getDrawable(getResources(), landmarkModel.getDrawableId(), null));
            chip.setCheckable(true);
            chip.setCheckedIconVisible(false);

            binding.landmarkGroup.addView(chip);
        }

        //Fetching all the respective methods
        setUpRecyclerView();
        getUserSaveLocations();
    }

    //Googlemap onMapReady method which asks the user for locations permission
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if (appPermissions.checkLocation(requireContext())) {
            checkLocationPerms = true;
            googleMapSetup();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(requireContext()).setTitle("Location Permission")
                    .setMessage("Wonder Finder requires location permission")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestLocation();
                        }
                    }).create().show();
        } else {
            requestLocation();
        }
    }

    //Method to as the user to access their locaiton
    private void requestLocation() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.ACCESS_BACKGROUND_LOCATION}, AllConstants.LOCATION_REQUEST_CODE);
    }

    //Method to check if the permissions have been granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AllConstants.LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationPerms = true;
                googleMapSetup();
            } else {
                checkLocationPerms = false;
                Toast.makeText(requireContext(), "Location Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Method to setup the googlemap in the homefragment
    private void googleMapSetup() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            checkLocationPerms = false;
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(true);

        locationUpdate();
    }

    //Method to update the users location
    private void locationUpdate() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    for (Location location : locationResult.getLocations()) {
                        Log.d("TAG", "onLocationResult: " + location.getLongitude() + " " + location.getLatitude());
                    }
                }
                super.onLocationResult(locationResult);
            }
        };

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        startLocationUpdates();
    }

    //Method to start getting the users location
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            checkLocationPerms = false;
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Getting your location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        getCurrentLocation();
    }

    //Method to get the users current location
    private void getCurrentLocation() {
        FusedLocationProviderClient fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireContext());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            checkLocationPerms = false;
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                currentLocation = location;
                infoWindowAdapter = null;
                infoWindowAdapter = new InfoWindowAdapter(currentLocation, requireContext());
                mGoogleMap.setInfoWindowAdapter(infoWindowAdapter);
                moveCameraToLocation(location);
            }
        });
    }

    //Method to pan the camera to the users current location
    private void moveCameraToLocation(Location location) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new
                LatLng(location.getLatitude(), location.getLongitude()), 17);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("Current Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet(firebaseAuth.getCurrentUser().getDisplayName());

        if (currentMarker != null) {
            currentMarker.remove();
        }

        currentMarker = mGoogleMap.addMarker(markerOptions);
        currentMarker.setTag(703);
        mGoogleMap.animateCamera(cameraUpdate);

    }

    //Method to stop looking for the users location
    private void stopLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        Log.d("TAG", "stopLocationUpdate: Location Update stopped" );
    }

    //Android studio onPause method
    @Override
    public void onPause() {
        super.onPause();

        if (fusedLocationProviderClient != null)
            stopLocationUpdate();
    }

    //Android studio onResume method
    @Override
    public void onResume() {
        super.onResume();

        if (fusedLocationProviderClient != null) {
            startLocationUpdates();
            if (currentMarker != null) {
                currentMarker.remove();
            }
        }
    }

    //Method to get the nearby landmarks from the users location
    private void getLandmarks(String landmarkName) {
        if (checkLocationPerms) {
            binding.homeProgress.setVisibility(View.VISIBLE);
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                    + currentLocation.getLatitude() + "," + currentLocation.getLongitude()
                    + "&radius=" + radius + "&type=" + landmarkName + "&key=" +
                    getResources().getString(R.string.API_KEY);

            if (currentLocation != null) {
                retrofitAPI.getNearByPlaces(url).enqueue(new Callback<GoogleResponseModel>() {
                    @Override
                    public void onResponse(@NonNull Call<GoogleResponseModel> call, @NonNull Response<GoogleResponseModel> response) {
                        if (response.errorBody() == null) {
                            if (response.body() != null) {
                                if (response.body().getGoogleLandmarkModels() != null
                                && response.body().getGoogleLandmarkModels().size() > 0) {
                                    googleLandmarkModelList.clear();
                                    mGoogleMap.clear();
                                    for (int i = 0; i <response.body().getGoogleLandmarkModels().size(); i++) {

                                        if (userSavedLocation.contains(response.body().getGoogleLandmarkModels().get(i).getPlaceId())) {
                                            response.body().getGoogleLandmarkModels().get(i).setSaved(true);
                                        }
                                        googleLandmarkModelList.add(response.body().getGoogleLandmarkModels().get(i));
                                        addMarker(response.body().getGoogleLandmarkModels().get(i), i);
                                    }

                                    googleLandmarkAdapter.setGoogleLandmarkModels(googleLandmarkModelList);

                                } else {
                                    mGoogleMap.clear();
                                    googleLandmarkModelList.clear();
                                    googleLandmarkAdapter.setGoogleLandmarkModels(googleLandmarkModelList);
                                    radius += 1000;
                                    getLandmarks(landmarkName);
                                }
                            }
                        } else {
                            Log.d("TAG", "onResponse: " + response.errorBody());
                            Toast.makeText(requireContext(), "Error: " + response.errorBody(), Toast.LENGTH_SHORT).show();
                        }
                        binding.homeProgress.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailure(Call<GoogleResponseModel> call, Throwable t) {
                        Log.d("TAG", "onFailure: " + t );
                        binding.homeProgress.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }
    }

    //Method to add a marker to the map
    private void addMarker(GoogleLandmarkModel googleLandmarkModel, int position) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(googleLandmarkModel.getGeometry().getLocation().getLat(),
                        googleLandmarkModel.getGeometry().getLocation().getLng()))
                .title(googleLandmarkModel.getName())
                .snippet(googleLandmarkModel.getVicinity());
        markerOptions.icon(getCustomIcon());
        mGoogleMap.addMarker(markerOptions).setTag(position);
    }

    //Method to customize the marker of landmarks
    private BitmapDescriptor getCustomIcon() {
        Drawable background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_location);
        background.setTint(getResources().getColor(com.google.android.libraries.places.R.color.quantum_googred900, null));
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //Method to set up the landmarks recycler view for the home fragment
    private void setUpRecyclerView() {
        binding.landmarksRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.landmarksRecyclerView.setHasFixedSize(false);
        googleLandmarkAdapter = new GoogleLandmarkAdapter(this);
        binding.landmarksRecyclerView.setAdapter(googleLandmarkAdapter);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.landmarksRecyclerView);

        binding.landmarksRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                int position = linearLayoutManager.findFirstVisibleItemPosition();
                if (position > -1) {
                    GoogleLandmarkModel googleLandmarkModel = googleLandmarkModelList.get(position);
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(googleLandmarkModel.getGeometry().getLocation().getLat(),
                            googleLandmarkModel.getGeometry().getLocation().getLng()), 20));
                }
            }
        });
    }

    //Method for when the user clicks on a landmark marker
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        int markerTag = (int) marker.getTag();
        binding.landmarksRecyclerView.scrollToPosition(markerTag);
        return false;
    }

    //Method to save a users favorite landmark
    @Override
    public void onSaveClick(GoogleLandmarkModel googleLandmarkModel) {
        if (userSavedLocation.contains(googleLandmarkModel.getPlaceId())) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Remove Landmark")
                    .setMessage("Are you sure you want to remove this landmark?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removeLandmark(googleLandmarkModel);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create().show();
        } else {
            binding.homeProgress.setVisibility(View.VISIBLE);

            locationReference.child(googleLandmarkModel.getPlaceId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        SavedLandmarkModel savedLandmarkModel = new SavedLandmarkModel(googleLandmarkModel.getName(), googleLandmarkModel.getVicinity(),
                                googleLandmarkModel.getPlaceId(), googleLandmarkModel.getRating(),
                                googleLandmarkModel.getUserRatingsTotal().doubleValue(),
                                googleLandmarkModel.getGeometry().getLocation().getLat(),
                                googleLandmarkModel.getGeometry().getLocation().getLng());

                        savedLandmark(savedLandmarkModel);
                    }

                    saveUserLocation(googleLandmarkModel.getPlaceId());

                    int index = googleLandmarkModelList.indexOf(googleLandmarkModel);
                    googleLandmarkModelList.get(index).setSaved(true);
                    googleLandmarkAdapter.notifyDataSetChanged();
                    binding.homeProgress.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    //Method to remove a users favorite landmark
    private void removeLandmark(GoogleLandmarkModel googleLandmarkModel) {
        userSavedLocation.remove(googleLandmarkModel.getPlaceId());
        int index = googleLandmarkModelList.indexOf(googleLandmarkModel);
        googleLandmarkModelList.get(index).setSaved(false);
        googleLandmarkAdapter.notifyDataSetChanged();

        Snackbar.make(binding.getRoot(), "Landmark Removed", Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userSavedLocation.add(googleLandmarkModel.getPlaceId());
                        googleLandmarkModelList.get(index).setSaved(true);
                        googleLandmarkAdapter.notifyDataSetChanged();
                    }
                })
                .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);

                        userLocationReference.setValue(userSavedLocation);
                    }
                }).show();
    }

    //Method to get the location of the favorite landmark
    private void saveUserLocation(String placeId) {
        userSavedLocation.add(placeId);
        userLocationReference.setValue(userSavedLocation);
        Snackbar.make(binding.getRoot(), "Landmark Favorited", Snackbar.LENGTH_LONG).show();
    }

    //Method to get all the information regarding the favorite landmark and save it in the reference
    private void savedLandmark(SavedLandmarkModel savedLandmarkModel) {
        locationReference.child(savedLandmarkModel.getPlaceId()).setValue(savedLandmarkModel);
    }

    //Method for when a user clicks on the directions to the landmark
    @Override
    public void onDirectionClick(GoogleLandmarkModel googleLandmarkModel) {
        String placeId = googleLandmarkModel.getPlaceId();
        Double lat = googleLandmarkModel.getGeometry().getLocation().getLat();
        Double lng = googleLandmarkModel.getGeometry().getLocation().getLng();

        Intent intent = new Intent(requireContext(), DirectionActivity.class);
        intent.putExtra("placeId", placeId);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);

        startActivity(intent);
    }

    //Method to get all of the saved landmarks of a user
    private void getUserSaveLocations() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseAuth.getUid()).child("Saved Locations");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds:snapshot.getChildren()) {
                        String placeId = ds.getValue(String.class);
                        userSavedLocation.add(placeId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}