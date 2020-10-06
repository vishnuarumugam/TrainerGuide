package com.example.trainerguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.card.MaterialCardView;

public class ProfileSelect extends AppCompatActivity implements View.OnClickListener{

    private MaterialCardView trainerCardView, traineeCardView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_select);

        traineeCardView = findViewById(R.id.traineeCardView);
        trainerCardView = findViewById(R.id.trainerCardView);

        trainerCardView.setOnClickListener(this);
        traineeCardView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
    switch (v.getId())
    {
        case R.id.traineeCardView:
            intent = new Intent(getApplicationContext(),RegistrationForm.class);
            intent.putExtra("IsTrainer",false);
            break;
        case R.id.trainerCardView:
            intent = new Intent(getApplicationContext(),RegistrationForm.class);
            intent.putExtra("IsTrainer",true);
            break;
        default:
            break;
    }
    }
}