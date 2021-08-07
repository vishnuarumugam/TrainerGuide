package com.example.trainerguide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;

public class SelectProfileType extends AppCompatActivity implements View.OnClickListener{

    private MaterialCardView trainerCardView,traineeCardView;
    private ImageView userProfileCheck, trainerProfileCheck;
    private Button profileSelectionProceed;
    private String userType="";
    private RelativeLayout trainerCardViewLay, traineeCardViewLay;
    private HorizontalScrollView profileTypeHorizontalLay;

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

        profileTypeHorizontalLay=findViewById(R.id.profileTypeHorizontalLay);
        traineeCardViewLay = findViewById(R.id.traineeCardViewLay);
        trainerCardViewLay = findViewById(R.id.trainerCardViewLay);

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId())
        {
            case R.id.traineeCardView:
                userProfileCheck.setVisibility(View.VISIBLE);
                trainerProfileCheck.setVisibility(View.INVISIBLE);
                //traineeCardView.setCardBackgroundColor(getResources().getColor(R.color.themeProfileSelect));
                //trainerCardView.setCardBackgroundColor(getResources().getColor(R.color.adShadowOne));
                traineeCardViewLay.setBackgroundColor(getResources().getColor(R.color.themeProfileSelect));
                trainerCardViewLay.setBackgroundColor(getResources().getColor(R.color.adShadowOne));
                userType="User";
                profileTypeHorizontalLay.fullScroll(View.FOCUS_LEFT);
                break;

            case R.id.trainerCardView:
                userProfileCheck.setVisibility(View.INVISIBLE);
                trainerProfileCheck.setVisibility(View.VISIBLE);
                //trainerCardView.setCardBackgroundColor(getResources().getColor(R.color.themeProfileSelect));
                //traineeCardView.setCardBackgroundColor(getResources().getColor(R.color.adShadowOne));
                trainerCardViewLay.setBackgroundColor(getResources().getColor(R.color.themeProfileSelect));
                traineeCardViewLay.setBackgroundColor(getResources().getColor(R.color.adShadowOne));
                profileTypeHorizontalLay.fullScroll(View.FOCUS_RIGHT);
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