package com.example.wonderfinder.Fragments;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.wonderfinder.Adapter.GoogleLandmarkAdapter;
import com.example.wonderfinder.DirectionActivity;
import com.example.wonderfinder.R;
import com.example.wonderfinder.SavedLandmarkModel;
import com.example.wonderfinder.SavedLocationInterface;
import com.example.wonderfinder.databinding.FragmentFavoritePlacesBinding;
import com.example.wonderfinder.databinding.SavedItemLayoutBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FavoritePlacesFragment extends Fragment implements SavedLocationInterface{

    //Declare all variables as private variables with their respective types
    private FragmentFavoritePlacesBinding binding;
    private FirebaseAuth firebaseAuth;
    private ArrayList<SavedLandmarkModel> savedLandmarkModelArrayList;
    private FirebaseRecyclerAdapter<String, ViewHolder> firebaseRecyclerAdapter;
    private SavedLocationInterface savedLocationInterface;

    //Android Studio onCreateView method
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Binds the inflater
        binding = FragmentFavoritePlacesBinding.inflate(inflater, container, false);
        savedLocationInterface = this;
        firebaseAuth = FirebaseAuth.getInstance();
        savedLandmarkModelArrayList = new ArrayList<>();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Favorite Places");
        return binding.getRoot();
    }

    //Android onViewCreated method
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.savedRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(binding.savedRecyclerView);

        //Calls the getSavedLandmarks method
        getSavedLandmarks();
    }

    //Method to get the users favorited landmarks from the database
    private void getSavedLandmarks() {
        binding.favProgress.setVisibility(View.VISIBLE);
        Query query = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseAuth.getUid()).child("Saved Locations");

        FirebaseRecyclerOptions<String> options = new FirebaseRecyclerOptions.Builder<String>()
                .setQuery(query, String.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<String, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull String savePlaceId) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Landmarks").child(savePlaceId);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            SavedLandmarkModel savedLandmarkModel = snapshot.getValue(SavedLandmarkModel.class);
                            holder.binding.setSaveLandmarkModel(savedLandmarkModel);
                            holder.binding.setListener(savedLocationInterface);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                SavedItemLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()),
                        R.layout.saved_item_layout, parent, false);
                return new ViewHolder(binding);
            }
        };

        binding.savedRecyclerView.setAdapter(firebaseRecyclerAdapter);
        binding.favProgress.setVisibility(View.INVISIBLE);

    }

    //Android onResume method to tell the firebaseRecyclerAdapter to start listening
    @Override
    public void onResume() {
        super.onResume();
        firebaseRecyclerAdapter.startListening();
    }

    //Android onPause method to tell the firebaseRecyclerAdapter to stop listening
    @Override
    public void onPause() {
        super.onPause();
        firebaseRecyclerAdapter.stopListening();
    }

    //Method to get the placeId, latitude and longitude and store it in an intent
    @Override
    public void onLocationClick(SavedLandmarkModel savedLandmarkModel) {
        if (savedLandmarkModel.getLat() != null && savedLandmarkModel.getLng() != null) {
            Intent intent = new Intent(requireContext(), DirectionActivity.class);
            intent.putExtra("placeId", savedLandmarkModel.getPlaceId());
            intent.putExtra("lat", savedLandmarkModel.getLat());
            intent.putExtra("lng", savedLandmarkModel.getLng());

            startActivity(intent);
        } else {
            Toast.makeText(requireContext(), "Location Not Found", Toast.LENGTH_SHORT).show();
        }
    }

    //Method to bind the ViewHolder to the RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder{
        private SavedItemLayoutBinding binding;
        public ViewHolder(@NonNull SavedItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}