package com.example.wonderfinder.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wonderfinder.GoogleLandmarkModel;
import com.example.wonderfinder.NearLocationInterface;
import com.example.wonderfinder.R;
import com.example.wonderfinder.databinding.LandmarkItemLayoutBinding;

import java.util.List;

public class GoogleLandmarkAdapter extends RecyclerView.Adapter<GoogleLandmarkAdapter.ViewHolder> {

    //Declare all variables as private variables with their respective types
    private List<GoogleLandmarkModel> googleLandmarkModels;
    private NearLocationInterface nearLocationInterface;

    //Constructor method
    public GoogleLandmarkAdapter(NearLocationInterface nearLocationInterface) {
        this.nearLocationInterface = nearLocationInterface;
    }

    //Android studio onCreateViewHolder method
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LandmarkItemLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.landmark_item_layout, parent, false);
        return new ViewHolder(binding);
    }

    //Android studio onBindViewHolder method
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (googleLandmarkModels !=null) {
            GoogleLandmarkModel landmarkModel = googleLandmarkModels.get(position);
            holder.binding.setGoogleLandmarkModel(landmarkModel);
            holder.binding.setListener(nearLocationInterface);
        }
    }

    //Method to get the number of items in the googleLandmarkModels
    @Override
    public int getItemCount() {
        if (googleLandmarkModels != null)
            return googleLandmarkModels.size();
        else
        return 0;
    }

    //Method to set the direction of the landmarks on the map
    public void setGoogleLandmarkModels(List<GoogleLandmarkModel> googleLandmarkModels) {
        this.googleLandmarkModels = googleLandmarkModels;
        notifyDataSetChanged();
    }

    //Method to bind the ViewHolder to the RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder{
        private LandmarkItemLayoutBinding binding;
        public ViewHolder(@NonNull LandmarkItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
