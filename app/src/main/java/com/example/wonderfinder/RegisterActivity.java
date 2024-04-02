package com.example.wonderfinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.wonderfinder.Constant.AllConstants;
import com.example.wonderfinder.Permissions.AppPermissions;
import com.example.wonderfinder.databinding.ActivityLoginBinding;
import com.example.wonderfinder.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    //Declare all variables as private variables with their respective types
    private ActivityRegisterBinding binding;
    private Uri imageUri;
    private AppPermissions appPermissions;
    private String email, username, password, confirmPassword;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the layout to binding to call variables
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //Hide action bar
        //Objects.requireNonNull(getSupportActionBar()).hide();

        //Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            finish();
            return;
        }

        //Take the user back to the loginActivity
        binding.btnRegBack.setOnClickListener(view -> {
            onBackPressed();
        });

        //Take the user back to the loginActivity
        binding.tvLogin.setOnClickListener(view -> {
            onBackPressed();
        });

        //Register a new user if the fields are valid
        binding.btnRegister.setOnClickListener(view -> {
            registerUser();
        });

    }


    private void registerUser() {
        email = binding.actvEmail.getText().toString().trim();
        username = binding.etUsername.getText().toString().trim();
        password = binding.etPassword.getText().toString().trim();
        confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        if (username.isEmpty()) {
            binding.etUsername.setError("Username cannot be empty");
            binding.etUsername.requestFocus();
        } else if (email.isEmpty() || (!Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            binding.actvEmail.setError("Invalid email");
            binding.actvEmail.requestFocus();
        } else if (password.isEmpty()) {
            binding.etPassword.setError("Password cannot be empty");
            binding.etPassword.requestFocus();
        } else if (password.length() < 8) {
            binding.etPassword.setError("Minimum of 8 characters");
            binding.etPassword.requestFocus();
        } else if (!password.equals(confirmPassword)) {
            binding.etPassword.setError("Passwords do not match");
            binding.etPassword.requestFocus();
        } else {
            createUserEmailAccount();
        }
    }

    private void createUserEmailAccount() {
        binding.registerProgress.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            UserModel user = new UserModel(email, username);
                            databaseReference
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    binding.registerProgress.setVisibility(View.INVISIBLE);
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                }
                            });
                        } else {
                            binding.registerProgress.setVisibility(View.INVISIBLE);
                            Toast.makeText(RegisterActivity.this, "Authentication Failed", Toast.LENGTH_LONG).show();
                            Log.d("TAG", "onComplete: Error" + task.getException());
                        }
                    }
                });
    }
}