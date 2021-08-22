package com.example.trainerguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trainerguide.models.Notification;
import com.example.trainerguide.models.Trainee;
import com.example.trainerguide.models.Trainer;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Map;
import java.util.stream.Collectors;

public class TraineeProfileview extends AppCompatActivity{

    TextView name, goal, bmi, weight, height, mobile, email, foodType, otherHealthIssue, otherFoodAllergy;
    ImageView profileimg;
    Button createFoodChart, toolBarNotification;
    private String traineeuserId, path, navScreen;
    Animation buttonBounce;
    private Trainee user;
    RelativeLayout healthInfoRel, foodAllergyRel, contactRelLay;
    private MaterialCheckBox diabetesHealthIssue, cholesterolHealthIssue, thyroidHealthIssue, bpHealthIssue, heartHealthIssue, physicalInjuriesHealthIssue;
    private MaterialCheckBox diaryFoodAllergy, wheatFoodAllergy, nutsFoodAllergy, seaFoodAllergy, muttonFoodAllergy, chickenFoodAllergy;

    private ProgressDialog progressDialog;

    //Firestore
    private StorageReference storageReference;

    //Navigation view variables
    private Toolbar toolbar;
    private MenuItem profileMenu, logoutMenu, shareMenu, ratingMenu, traineeMenu;
    private TabLayout traineeTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainee_profileview);

        buttonBounce= AnimationUtils.loadAnimation(this, R.anim.button_bounce);

        traineeuserId = getIntent().getStringExtra("userId");
        navScreen = getIntent().getStringExtra("Screen");

        //Navigation view variables
        //drawerLayout = findViewById(R.id.trainee_view_drawer_layout);
        toolbar = findViewById(R.id.back_tool_bar);
        toolbar.setTitle("Trainee Profile");
        toolBarNotification = findViewById(R.id.toolBarNotification);
        toolBarNotification.setVisibility(View.GONE);
        //File Storage variables
        storageReference = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(this);

        path = "User/" + traineeuserId;

        name = findViewById(R.id.txtTraineeName);
        goal = findViewById(R.id.txtTraineeGoals);
        bmi = findViewById(R.id.txtTraineeBmi);
        weight = findViewById(R.id.txtTraineeWeight);
        height = findViewById(R.id.txtTraineeHeight);
        mobile = findViewById(R.id.txtTraineePhnNo);
        email = findViewById(R.id.txtTraineeEmail);
        foodType = findViewById(R.id.txtTraineeFoodType);
        healthInfoRel = findViewById(R.id.healthInfoRel);
        foodAllergyRel = findViewById(R.id.foodAllergyRelLay);
        contactRelLay = findViewById(R.id.contactRelLay);
        //requestbtn = findViewById(R.id.btnRequest);
        profileimg = findViewById(R.id.traineeImage);
        createFoodChart = findViewById(R.id.btnCreateFoodchart);
        traineeTabLayout = findViewById(R.id.traineesTabLayout);

        diaryFoodAllergy = findViewById(R.id.diaryFoodAllergy);
        wheatFoodAllergy = findViewById(R.id.wheatFoodAllergy);
        nutsFoodAllergy = findViewById(R.id.nutsFoodAllergy);
        seaFoodAllergy = findViewById(R.id.seaFoodAllergy);
        muttonFoodAllergy = findViewById(R.id.muttonFoodAllergy);
        chickenFoodAllergy = findViewById(R.id.chickenFoodAllergy);
        otherFoodAllergy = findViewById(R.id.otherFoodAllergy);

        diabetesHealthIssue = findViewById(R.id.diabetesHealthIssue);
        cholesterolHealthIssue = findViewById(R.id.cholesterolHealthIssue);
        thyroidHealthIssue = findViewById(R.id.thyroidHealthIssue);
        bpHealthIssue = findViewById(R.id.bpHealthIssue);
        heartHealthIssue = findViewById(R.id.heartHealthIssue);
        physicalInjuriesHealthIssue = findViewById(R.id.physicalInjuriesHealthIssue);
        otherHealthIssue = findViewById(R.id.otherHealthIssue);

        //Toolbar customisation
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.black));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        //ActionBarDrawerToggle toggle = CommonNavigator.navigatorInitmethod(drawerLayout, navigationView, toolbar, this);
        //toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.themeColourTwo));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnBackPressed();
            }
        });

        /*toolBarNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBarNotification.startAnimation(buttonBounce);
                startActivity(new Intent(TraineeProfileview.this,NotificationScreen.class));
                finish();
            }
        });*/

        PopulateUserDetails();

        traineeTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getText().equals("Health Issues"))
                {
                    healthInfoRel.setVisibility(View.VISIBLE);
                    foodAllergyRel.setVisibility(View.GONE);
                    contactRelLay.setVisibility(View.GONE);
                }
                else if(tab.getText().equals("Food Allergy"))
                {
                    healthInfoRel.setVisibility(View.GONE);
                    foodAllergyRel.setVisibility(View.VISIBLE);
                    contactRelLay.setVisibility(View.GONE);
                }
                else
                {
                    healthInfoRel.setVisibility(View.GONE);
                    foodAllergyRel.setVisibility(View.GONE);
                    contactRelLay.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //Menu Item variables
        profileMenu = findViewById(R.id.nav_profile);
        //traineeMenu = findViewById(R.id.nav_trainees);

        createFoodChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFoodChart.startAnimation(buttonBounce);
                Intent intent = new Intent(TraineeProfileview.this,PrepareFoodChart.class);
                intent.putExtra("userId",user.getUserId());
                intent.putExtra("userName", user.getName());
                intent.putExtra("totalCalories", user.getBmr().doubleValue());
                startActivity(intent);
                finish();
            }
        });
    }


    public void PopulateUserDetails() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);
        //Show Progress Dialog
        progressDialog.show();
        //Set Content
        progressDialog.setContentView(R.layout.progressdialog);
        //Set Transparent Background
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("********OnDataChange*******");
                user = snapshot.getValue(Trainee.class);
                System.out.println("********" + user + "*******");

                Picasso.get().load(user.getImage())
                        .placeholder(R.drawable.ic_share)
                        .fit()
                        .centerCrop()
                        .into(profileimg);

                foodType.setText(user.getFoodType());
                name.setText(user.getName());
                goal.setText(user.getSubscriptionType());
                bmi.setText(user.getBmi().toString());
                weight.setText(String.valueOf(user.getWeight()));
                height.setText(String.valueOf(user.getHeight()));
                mobile.setText(String.valueOf(user.getPhoneNumber()));
                email.setText(String.valueOf(user.getEmail()));

                //Health Issue Recycler View Data
                if(user.getHealthIssues()!=null) {

                    for (Map.Entry healthIssue : user.getHealthIssues().entrySet()) {

                        if (!"Others".equals(healthIssue.getKey())){

                            if (healthIssue.getValue().toString().equals("Diabetes")){
                                diabetesHealthIssue.setChecked(true);
                            }
                            if(healthIssue.getValue().toString().equals("Cholesterol")){
                                cholesterolHealthIssue.setChecked(true);
                            }
                            if(healthIssue.getValue().toString().equals("Thyroid")){
                                thyroidHealthIssue.setChecked(true);
                            }
                            if(healthIssue.getValue().toString().equals("Blood Pressure")){
                                bpHealthIssue.setChecked(true);
                            }
                            if(healthIssue.getValue().toString().equals("Heart Problems")){
                                heartHealthIssue.setChecked(true);
                            }
                            if(healthIssue.getValue().toString().equals("Physical Injuries")){
                                physicalInjuriesHealthIssue.setChecked(true);
                            }

                        }
                        else{
                            otherHealthIssue.setText(healthIssue.getValue().toString());
                        }


                    }
                }

                //Food Allergy Recycler View Data
                if(user.getFoodAllergy()!=null) {
                    for (Map.Entry  foodAllergyItem : user.getFoodAllergy().entrySet()) {

                        if (!"Others".equals(foodAllergyItem.getKey())){

                            if (foodAllergyItem.getValue().toString().equals("Diary")){
                                diaryFoodAllergy.setChecked(true);
                            }
                            if (foodAllergyItem.getValue().toString().equals("Wheat")){
                                wheatFoodAllergy.setChecked(true);
                            }
                            if (foodAllergyItem.getValue().toString().equals("Nuts")){
                                nutsFoodAllergy.setChecked(true);
                            }
                            if (foodAllergyItem.getValue().toString().equals("Sea Food")){
                                seaFoodAllergy.setChecked(true);
                            }
                            if (foodAllergyItem.getValue().toString().equals("Mutton")){
                                muttonFoodAllergy.setChecked(true);
                            }
                            if (foodAllergyItem.getValue().toString().equals("Chicken")){
                                chickenFoodAllergy.setChecked(true);
                            }

                        }
                        else{

                            otherFoodAllergy.setText(foodAllergyItem.getValue().toString());

                        }
                    }
                }
                //Dismiss Progress Dialog
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void OnBackPressed()
    {
        if(navScreen!=null && navScreen.equals("NotificationScreen")){
            startActivity(new Intent(TraineeProfileview.this, NotificationScreen.class));
            finish();
        }
        else {
            startActivity(new Intent(TraineeProfileview.this, TraineesScreen.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {

            OnBackPressed();
    }


}