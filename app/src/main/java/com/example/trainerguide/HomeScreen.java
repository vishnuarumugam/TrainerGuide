package com.example.trainerguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.trainerguide.models.Trainer;
import com.example.trainerguide.models.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.picasso.Picasso;

public class HomeScreen extends AppCompatActivity {

    //Homescreen variables
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuItem profileMenu, logoutMenu, shareMenu, ratingMenu, traineeMenu;
    Intent intent;

    //User Detail variables
    private String userId;
    private String path = "Trainer/" + userId;

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

        //Graph representation
        progressGraphView = findViewById(R.id.userProgressGraph);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        path = "Trainer/" + userId;
        System.out.println("******H**"+userId+"**S******");
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
                    case R.id.nav_trainer:
                        startActivity(new Intent(HomeScreen.this,TrainerScreen.class));
                        finish();
                        break;
                    case R.id.nav_logout:
                        startActivity(new Intent(HomeScreen.this,MainActivity.class));
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

    public void PopulateUserDetails(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);

        System.out.println(path);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("********HSOnDataChange*******");
                Trainer user = snapshot.getValue(Trainer.class);

                LineGraphSeries<DataPoint> progressDatapoint = new LineGraphSeries<>(new DataPoint[]{
                        new DataPoint(0,user.getBmi()),
                        new DataPoint(1,19),
                });

                progressGraphView.addSeries(progressDatapoint);

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