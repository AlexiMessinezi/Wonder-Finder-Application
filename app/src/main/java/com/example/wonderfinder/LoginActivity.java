package com.example.wonderfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wonderfinder.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextView tvRegister;
    private ActivityLoginBinding binding;
    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the layout to binding to call variables
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Hide action bar
        //Objects.requireNonNull(getSupportActionBar()).hide();

        //Take the user to the RegisterActivity when tvRegister is clicked
        binding.tvRegister.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        //Take the user to the ForgotPasswordActivity when tvForgotPass is clicked
        binding.tvForgotPass.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });

        binding.btnLogin.setOnClickListener(view -> {
            if (checkFields()) {
                loginUser();
            }
        });
    }

    private boolean checkFields() {
        email = binding.actvEmail.getText().toString().trim();
        password = binding.etPassword.getText().toString().trim();

        boolean flag = false;
        View requestView = null;

        if (email.isEmpty() || (!Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            binding.actvEmail.setError("Invalid email");
            flag = true;
            requestView = binding.actvEmail;
        } else if (password.isEmpty()) {
            binding.etPassword.setError("Password cannot be empty");
            flag = true;
            requestView = binding.etPassword;
        } else if (password.length() < 8) {
            binding.etPassword.setError("Minimum of 8 characters");
            flag = true;
            requestView = binding.etPassword;
        }

        if (flag) {
            requestView.requestFocus();
            return false;
        } else {
            return true;
        }

    }

    private void loginUser() {
        binding.loginProgress.setVisibility(View.VISIBLE);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    binding.loginProgress.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    binding.loginProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this, "Error : " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}