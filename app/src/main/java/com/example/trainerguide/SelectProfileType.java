package com.example.trainerguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.card.MaterialCardView;

public class SelectProfileType extends AppCompatActivity implements View.OnClickListener{
MaterialCardView trainerCardView,traineeCardView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_profile_type);
        traineeCardView = findViewById(R.id.traineeCardView);
        trainerCardView = findViewById(R.id.trainerCardView);
        traineeCardView.setOnClickListener(this);
        trainerCardView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId())
        {
            case R.id.traineeCardView:
                intent = new Intent(getApplicationContext(),RegistrationForm.class);
                intent.putExtra("IsTrainer", false);
                startActivity(intent);
                finish();
                break;

            case R.id.trainerCardView:
                intent = new Intent(getApplicationContext(),RegistrationForm.class);
                intent.putExtra("IsTrainer", true);
                startActivity(intent);
                finish();
                break;

            default:
                break;
        }
    }
}