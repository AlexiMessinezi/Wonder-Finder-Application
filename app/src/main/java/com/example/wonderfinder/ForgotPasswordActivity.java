package com.example.wonderfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.wonderfinder.databinding.ActivityForgotPasswordBinding;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {

    //Declare all variables as private variables with their respective types
    private ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the layout to binding to call variables
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Hide action bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        binding.btnForgBack.setOnClickListener(view -> {
            //startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            onBackPressed();
        });
    }
}