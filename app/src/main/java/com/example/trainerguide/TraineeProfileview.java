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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trainerguide.models.Notification;
import com.example.trainerguide.models.Trainee;
import com.example.trainerguide.models.Trainer;
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

import java.util.stream.Collectors;

public class TraineeProfileview extends AppCompatActivity{

    TextView name, goal, bmi, weight, height, mobile, email, foodType;
    ImageView profileimg;
    Button createFoodChart;
    private String traineeuserId, path, navScreen;
    Animation buttonBounce;
    private Trainee user;
    RecyclerView healthInfo;
    RelativeLayout healthInfoRel, foodTypeRel;


    private ProgressDialog progressDialog;

    //Firestore
    private StorageReference storageReference;

    //Navigation view variables
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
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
        drawerLayout = findViewById(R.id.trainee_view_drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.tool_bar);
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
        healthInfo = findViewById(R.id.healthInfoRecycler);
        foodType = findViewById(R.id.txtTraineeFoodType);
        healthInfoRel = findViewById(R.id.healthInfoRel);
        foodTypeRel = findViewById(R.id.foodTypeRel);
        //requestbtn = findViewById(R.id.btnRequest);
        profileimg = findViewById(R.id.traineeImage);
        createFoodChart = findViewById(R.id.btnCreateFoodchart);
        traineeTabLayout = findViewById(R.id.traineesTabLayout);

        //Toolbar customisation
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.black));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        ActionBarDrawerToggle toggle = CommonNavigator.navigatorInitmethod(drawerLayout, navigationView, toolbar, this);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.themeColourTwo));

        PopulateUserDetails();

        healthInfo.setLayoutManager(new LinearLayoutManager(this));


        traineeTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getText().equals("Health Info"))
                {
                    healthInfoRel.setVisibility(View.VISIBLE);
                    foodTypeRel.setVisibility(View.GONE);
                }
                else
                {
                    healthInfoRel.setVisibility(View.GONE);
                    foodTypeRel.setVisibility(View.VISIBLE);
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

//Method to re-direct the page from menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_profile:
                        startActivity(new Intent(TraineeProfileview.this, ProfileScreen.class));
                        finish();
                        break;
                    /*case R.id.nav_trainees:
                        startActivity(new Intent(TraineeProfileview.this, TraineesScreen.class));
                        finish();
                        break;
                    case R.id.nav_trainer:
                        startActivity(new Intent(TraineeProfileview.this, TrainerScreen.class));
                        finish();
                        break;*/
                    case R.id.nav_logout:
                        startActivity(new Intent(TraineeProfileview.this, MainActivity.class));
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = settings.edit();
                        editor.remove("userId");
                        editor.remove("ProfileType");
                        editor.remove("IsLoggedIn");
                        //editor.putBoolean("IsLoggedIn",false);
                        editor.commit();
                        finish();
                        break;
                    default:
                        Toast.makeText(TraineeProfileview.this, "profile", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
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

                ProfileAdapter adapter = new ProfileAdapter(user.getHealthIssues().values().stream().collect(Collectors.toList()),getApplicationContext());
                healthInfo.setAdapter(adapter);
                //Dismiss Progress Dialog
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer((GravityCompat.START));
        } else {
            if(navScreen!=null && navScreen.equals("Notification")){
                startActivity(new Intent(TraineeProfileview.this, NotificationScreen.class));
                finish();
            }
            else {
                startActivity(new Intent(TraineeProfileview.this, TraineesScreen.class));
                finish();
            }
        }
    }


}