package com.example.trainerguide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;

public class SelectProfileType extends AppCompatActivity implements View.OnClickListener{

    private MaterialCardView trainerCardView,traineeCardView;
    private ImageView userProfileCheck, trainerProfileCheck;
    private Button profileSelectionProceed;
    private String userType="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_profile_type);
        traineeCardView = findViewById(R.id.traineeCardView);
        trainerCardView = findViewById(R.id.trainerCardView);
        traineeCardView.setOnClickListener(this);
        trainerCardView.setOnClickListener(this);

        userProfileCheck = findViewById(R.id.userProfileCheck);
        trainerProfileCheck = findViewById(R.id.trainerProfileCheck);
        profileSelectionProceed = findViewById(R.id.profileSelectionProceed);
        profileSelectionProceed.setOnClickListener(this);
        userProfileCheck.setVisibility(View.INVISIBLE);
        trainerProfileCheck.setVisibility(View.INVISIBLE);
        traineeCardView.setCardBackgroundColor(getResources().getColor(R.color.adShadowOne));
        trainerCardView.setCardBackgroundColor(getResources().getColor(R.color.adShadowOne));

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId())
        {
            case R.id.traineeCardView:
                userProfileCheck.setVisibility(View.VISIBLE);
                trainerProfileCheck.setVisibility(View.INVISIBLE);
                traineeCardView.setCardBackgroundColor(getResources().getColor(R.color.themeColourOne));
                trainerCardView.setCardBackgroundColor(getResources().getColor(R.color.adShadowOne));
                userType="User";
                break;

            case R.id.trainerCardView:
                userProfileCheck.setVisibility(View.INVISIBLE);
                trainerProfileCheck.setVisibility(View.VISIBLE);
                trainerCardView.setCardBackgroundColor(getResources().getColor(R.color.themeColourOne));
                traineeCardView.setCardBackgroundColor(getResources().getColor(R.color.adShadowOne));
                userType="Trainer";
                break;
            case R.id.profileSelectionProceed:
                intent = new Intent(getApplicationContext(),RegistrationForm.class);
                if (userType.equals("Trainer")){
                    intent.putExtra("IsTrainer", true);
                    startActivity(intent);
                    finish();
                    break;
                }
                else if (userType.equals("User")){
                    intent.putExtra("IsTrainer", false);
                    startActivity(intent);
                    finish();
                    break;
                }
                else {
                    Toast.makeText(SelectProfileType.this,"Please select a profile to proceed",Toast.LENGTH_SHORT).show();
                }
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SelectProfileType.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}