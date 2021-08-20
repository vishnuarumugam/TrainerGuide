package com.example.trainerguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    private Animation topAnimation, bottomAnimation;
    private ImageView appLogo;
    private TextView appName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        topAnimation= AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnimation= AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        appLogo = findViewById(R.id.app_logo_image);
        appName = findViewById(R.id.app_name);

        appLogo.startAnimation(topAnimation);
        appName.startAnimation(bottomAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                finish();
            }
        },3000);

    }
}