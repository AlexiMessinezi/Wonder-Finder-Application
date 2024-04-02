package com.example.wonderfinder.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wonderfinder.LoginActivity;
import com.example.wonderfinder.R;
import com.example.wonderfinder.SettingModel;
import com.example.wonderfinder.UserModel;
import com.example.wonderfinder.Utils.WonderFinderAPI;
import com.example.wonderfinder.databinding.FragmentSettingsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettingsFragment extends Fragment {

    //Declare all variables as private variables with their respective types
    private FragmentSettingsBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private Boolean switchKM, switchMI;
    private DatabaseReference metricReference;
    private FirebaseDatabase firebaseDatabase;
    private String metUnit;
    private ArrayList<String> userMetricUnit;

    //Android studio onCreateView method
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Binds the inflater
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        //Set the variables to their respective values
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        //Database references to fetch the users data from firebase
        metricReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseAuth.getUid()).child("Metric");

        //Logs the user out using firebase
        binding.txtLogout.setOnClickListener(view ->{
            if (user != null && firebaseAuth != null) {
                firebaseAuth.signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

        //Sets the kilometer switch to true and the miles switch to false
        binding.metricSwitchKM.setOnClickListener(view -> {
            switchKM = true;
            binding.metricSwitchKM.setChecked(true);
            binding.metricSwitchMI.setChecked(false);
            switchMI = false;
            metricReference.setValue("Kilometers");
        });

        //Sets the miles switch to true and the kilometer switch to false
        binding.metricSwitchMI.setOnClickListener(view -> {
            switchMI = true;
            binding.metricSwitchKM.setChecked(false);
            binding.metricSwitchMI.setChecked(true);
            switchKM = false;
            //binding.settingsProgress.setVisibility(View.VISIBLE);
            metricReference.setValue("Miles");
        });

        //POE TASK 3 CODE
        //POE TASK 3 CODE
        //POE TASK 3 CODE
        //POE TASK 3 CODE
        //POE TASK 3 CODE
        binding.txtUsername.setOnClickListener(username -> {
            usernameDialog();
        });

        return binding.getRoot();
    }

    //Android studio onViewCreated method
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Settings");

        //Calls the methods
        getUserData();
        getUserMetrics();
    }

    //Method to populate the users username and email in the settings fragment
    private void getUserData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    binding.txtUsername.setText(userModel.getUsername());
                    binding.txtEmail.setText(userModel.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //POE TASK 3 CODE
    //POE TASK 3 CODE
    //POE TASK 3 CODE
    //POE TASK 3 CODE
    //POE TASK 3 CODE
    private void usernameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.username_dialog_layout, null, false);
        builder.setView(view);
        TextInputEditText edtUsername = view.findViewById(R.id.edtDialogUsername);

        builder.setTitle("Edit Username");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                binding.settingsProgress.setVisibility(View.VISIBLE);
                String username = edtUsername.getText().toString().trim();
                if (!username.isEmpty()) {
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build();
                    firebaseAuth.getCurrentUser().updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                binding.settingsProgress.setVisibility(View.INVISIBLE);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                                Map<String, Object> map = new HashMap<>();
                                map.put("username", username);
                                databaseReference.child(firebaseAuth.getUid()).updateChildren(map);

                                binding.txtUsername.setText(username);
                                Toast.makeText(getContext(), "Username has been updated", Toast.LENGTH_SHORT).show();
                            } else {
                                binding.settingsProgress.setVisibility(View.INVISIBLE);
                                Log.d("TAG", "onComplete: "+task.getException());
                                Toast.makeText(getContext(), ""+task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    binding.settingsProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "Username cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                binding.settingsProgress.setVisibility(View.INVISIBLE);
            }
        }).create().show();
    }

    //Method to get the users metric that they have selected
    private void getUserMetrics() {
        metricReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String mu = snapshot.getValue(String.class);
                    binding.txtUnit.setText(mu);

                    if (binding.txtUnit.getText().equals("Kilometers")) {
                        binding.metricSwitchKM.setChecked(true);
                        binding.metricSwitchMI.setChecked(false);
                    } else {
                        binding.metricSwitchKM.setChecked(false);
                        binding.metricSwitchMI.setChecked(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}