package com.example.trainerguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.trainerguide.models.Trainer;
import com.example.trainerguide.models.User;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;

public class HomeScreen extends AppCompatActivity implements View.OnClickListener {

    //Homescreen variables
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuItem profileMenu, logoutMenu, shareMenu, ratingMenu, traineeMenu;
    Intent intent;


    //Dashboard variables
    private MaterialCardView profileDashboard, reportDashboard, trainerDashboard, traineeDashboard, foodDashboard, pdf_dashboard;

    //User Detail variables
    private String userId, path, userType;


    //Progress Graph view
    private GraphView progressGraphView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);

        toolbar = findViewById(R.id.tool_bar);

        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.black));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        ActionBarDrawerToggle toggle = CommonNavigator.navigatorInitmethod(drawerLayout,navigationView,toolbar,this);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.appleGreen));

        //Menu Items
        profileMenu = findViewById(R.id.nav_profile);
        traineeMenu = findViewById(R.id.nav_trainees);

        //Dashboard variables

        profileDashboard = findViewById(R.id.profile_dashboard);
        reportDashboard = findViewById(R.id.report_dashboard);
        trainerDashboard = findViewById(R.id.trainer_dashboard);
        traineeDashboard = findViewById(R.id.trainee_dashboard);
        foodDashboard = findViewById(R.id.food_dashboard);
        //pdf_dashboard = findViewById(R.id.pdf_dashboard);

        profileDashboard.setOnClickListener(this);
        reportDashboard.setOnClickListener(this);
        trainerDashboard.setOnClickListener(this);
        traineeDashboard.setOnClickListener(this);
        foodDashboard.setOnClickListener(this);
        //pdf_dashboard.setOnClickListener(this);

        //Graph representation
        //progressGraphView = findViewById(R.id.userProgressGraph);

        /*userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        path = "Trainer/" + userId;
        System.out.println("******H**"+userId+"**S******");*/

        //User Info variables
        //userId = getIntent().getStringExtra("UserId");
        final SharedPreferences sp;
        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        userType = sp.getString("ProfileType",null);
        userId = sp.getString("userId",null);
        path = userType+ "/" + userId;

        PopulateUserDetails();


        //Method to re-direct the page from menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_profile:
                        intent=new Intent(HomeScreen.this,ProfileScreen.class);
                        intent.putExtra("UserId",userId);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_trainees:
                        intent=new Intent(HomeScreen.this,TraineesScreen.class);
                        intent.putExtra("UserId",userId);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_notification:
                        startActivity(new Intent(HomeScreen.this,NotificationScreen.class));
                        finish();
                        break;
                    case R.id.nav_trainer:
                        startActivity(new Intent(HomeScreen.this,TrainerScreen.class));
                        finish();
                        break;
                    case R.id.nav_foodPrep:
                        startActivity(new Intent(HomeScreen.this,PrepareFoodChart.class));
                        finish();
                        break;
                    case R.id.nav_logout:
                        startActivity(new Intent(HomeScreen.this,MainActivity.class));
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
                        Toast.makeText(HomeScreen.this, "profile", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });


    }

    public void onClick(View option) {

        switch (option.getId()) {

            case R.id.profile_dashboard:
                startActivity(new Intent(HomeScreen.this,ProfileScreen.class));
                finish();
                break;
            case R.id.report_dashboard:
                break;
            case R.id.trainer_dashboard:
                startActivity(new Intent(HomeScreen.this,TrainerScreen.class));
                finish();
                break;
            case R.id.trainee_dashboard:
                startActivity(new Intent(HomeScreen.this,TraineesScreen.class));
                finish();
                break;
            case R.id.food_dashboard:
                startActivity(new Intent(HomeScreen.this,FoodSourceListScreen.class));
                finish();
                break;
            /*case R.id.pdf_dashboard:
                startActivity(new Intent(HomeScreen.this, PdfCreation.class));
                finish();
                break;*/
            default:
                break;
        }
    }

    public void PopulateUserDetails(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("********HSOnDataChange*******");
                if (userType.equals("Trainer")){
                    System.out.println("Trainer"+snapshot.getValue(Trainer.class));
                    System.out.println("out");
                    Trainer user = snapshot.getValue(Trainer.class);
                }

                else{
                    System.out.println(snapshot.getValue(User.class));
                    //User user = snapshot.getValue(User.class);

                }

                /*LineGraphSeries<DataPoint> progressDatapoint = new LineGraphSeries<>(new DataPoint[]{
                        new DataPoint(0,user.getBmi()),
                        new DataPoint(1,19),
                });

                progressGraphView.addSeries(progressDatapoint);*/

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    @Override
    public void onBackPressed()
    {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer((GravityCompat.START));
        }
        else
        {
            super.onBackPressed();
        }
    }


}