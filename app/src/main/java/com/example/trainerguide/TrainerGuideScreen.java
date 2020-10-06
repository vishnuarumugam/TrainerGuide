package com.example.trainerguide;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class TrainerGuideScreen extends AppCompatActivity {
    private static int SPLASH_SCREEN = 5000;

    private Animation topAnim, bottomAnim;
    private ImageView img;
    private TextView logo, slogan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_guide_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_trainer_guide_screen);
        topAnim = AnimationUtils.loadAnimation( this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation( this,R.anim.bottom_animation);

        img = findViewById(R.id.imageSplash);
        logo = findViewById(R.id.txtlogo);
        slogan = findViewById(R.id.txtSlogan);

        img.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                Pair[] pair = new Pair[1];
                pair[0] = new Pair<View,String>(img,"logo_Img");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(TrainerGuideScreen.this,pair);
                    startActivity(intent,activityOptions.toBundle());

                }
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);
    }
}