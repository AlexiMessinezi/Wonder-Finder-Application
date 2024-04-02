package com.example.wonderfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    //Declare all variables as private variables with their respective types
    private ImageView imgWonderFinderLogo;
    private TextView txtWonderFinder;
    private FirebaseAuth firebaseAuth;

    //Declare animImage and animText as variables of type Animation
    Animation animImage, animText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //Hide action bar
        //Objects.requireNonNull(getSupportActionBar()).hide();

        //Get the instance of the firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        //Set imgWonderFinderLogo to its corresponding ImageView in activity_splash.xml
        imgWonderFinderLogo = findViewById(R.id.imgWonderFinderLogo);
        //Set txtWonderFinder to its corresponding TextView in activity_splash.xml
        txtWonderFinder = findViewById(R.id.txtWonderFinder);

        //Set animImage to load its animation from the img_animation.xml code in the anim folder
        animImage = AnimationUtils.loadAnimation(this, R.anim.img_animation);
        //Set animText to load its animation from the txt_animation.xml code in the anim folder
        animText = AnimationUtils.loadAnimation(this, R.anim.txt_animation);

        //Set imgSplashLogo's animation to animImage
        imgWonderFinderLogo.setAnimation(animImage);
        //Set txtSplash's animation to animText
        txtWonderFinder.setAnimation(animText);

        //Create a countdown timer for the animation
        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                //If the user is logged in start the main screen after the splash screen has completed the animation
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
                //Otherwise take the user to the login activity
                else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }


            }
        }.start();
    }
}