package com.example.trainerguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.trainerguide.models.Trainer;
import com.example.trainerguide.models.User;
import com.example.trainerguide.models.UserMetaData;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TraineesScreen extends AppCompatActivity {

    //Navigation view variables
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuItem profileMenu, logoutMenu, shareMenu, ratingMenu, traineeMenu;

    //Recycler view variables
    private RecyclerView traineeRecycler;
    private List<UserMetaData> traineesList = new ArrayList<>();
    private TraineeAdapter traineeAdapter;

    //Firebase variables
    private ValueEventListener valueEventListener;

    //User Detail variables
    private String userId;
    private String path;

    //Common variables
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainees_screen);

        //Navigation view variables
        drawerLayout = findViewById(R.id.trainee_drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.tool_bar);
        System.out.println("InsideTrainess");
        //Toolbar customisation
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.black));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        ActionBarDrawerToggle toggle = CommonNavigator.navigatorInitmethod(drawerLayout,navigationView,toolbar,this);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.appleGreen));

        //Menu Item variables
        profileMenu = findViewById(R.id.nav_profile);
        traineeMenu = findViewById(R.id.nav_trainees);

        //Method to re-direct the page from menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_profile:
                        intent = new Intent(TraineesScreen.this,ProfileScreen.class);
                        intent.putExtra("UserId",userId);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_trainees:
                        break;
                    case R.id.nav_trainer:
                        startActivity(new Intent(TraineesScreen.this,TrainerScreen.class));
                        finish();
                        break;
                    case R.id.nav_logout:
                        startActivity(new Intent(TraineesScreen.this,MainActivity.class));
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        //Recycler view variables
        traineeRecycler = findViewById(R.id.traineeRecycler);
        traineeRecycler.setLayoutManager(new LinearLayoutManager(this));
        traineeAdapter = new TraineeAdapter(TraineesScreen.this,traineesList);

        userId = getIntent().getStringExtra("UserId");
        path = "Trainer/" + userId + "/usersList";
        //path = "Trainer/" + userId;
        System.out.println("******"+userId+"******");
        populateTraineesData();


    }

    //Method to populate Trainee data
    public void populateTraineesData(){
        System.out.println("******T**"+userId+"**S******");
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);;
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("********OnDataChange*******");
                //Trainer trainerobj = snapshot.getValue(Trainer.class);
                //HashMap<String,UserMetaData> users =  trainerobj.getUsersList();

                for(DataSnapshot users : snapshot.getChildren()){
                    traineesList.add(users.getValue(UserMetaData.class));
                }
                //traineesList.addAll(users);
                traineeAdapter.notifyDataSetChanged();

                /*UserMetaData user = new UserMetaData("Satha" + System.currentTimeMillis(), "Satha", 0.00, "image");
                *//*trainerobj.setUser(user);
                databaseReference.setValue(trainerobj);*//*
                HashMap hash= new HashMap();
                hash.put(user.getUserId(),user);
                databaseReference.child(user.getUserId()).setValue(user);*/
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("********onCancelled*******");

            }
        });
            traineeRecycler.setAdapter(traineeAdapter);
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