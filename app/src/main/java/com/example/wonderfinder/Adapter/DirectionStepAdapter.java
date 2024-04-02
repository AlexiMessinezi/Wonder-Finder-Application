package com.example.wonderfinder.Adapter;

import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wonderfinder.Model.DirectionLandmarkModel.DirectionStepModel;
import com.example.wonderfinder.databinding.StepItemLayoutBinding;

import java.util.List;

public class DirectionStepAdapter  extends RecyclerView.Adapter<DirectionStepAdapter.ViewHolder> {

    //Declare directionStepModels as a List of type DirectionStepModel
    private List<DirectionStepModel> directionStepModels;

    //Android onCreateViewHolder method
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        StepItemLayoutBinding binding = StepItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new ViewHolder(binding);
    }

    //Android onBindViewHolder method
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (directionStepModels != null) {
            DirectionStepModel stepModel = directionStepModels.get(position);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.binding.txtStepHtml.setText(Html.fromHtml(stepModel.getHtmlInstructions(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                holder.binding.txtStepHtml.setText(Html.fromHtml(stepModel.getHtmlInstructions()));
            }

            holder.binding.txtStepTime.setText(stepModel.getDuration().getText());
            holder.binding.txtStepDistance.setText(stepModel.getDistance().getText());
        }

    }

    //Method to get the number of items in the directionStepModels
    @Override
    public int getItemCount() {
        if (directionStepModels != null)
            return directionStepModels.size();
        else
            return 0;
    }

    //Method to set the direction of the steps needed to get to a destination
    public void setDirectionStepModels(List<DirectionStepModel> directionStepModels) {
        this.directionStepModels = directionStepModels;
        notifyDataSetChanged();
    }

    //Method to bind the ViewHolder to the RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder{
        private StepItemLayoutBinding binding;

        public ViewHolder(@NonNull StepItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
