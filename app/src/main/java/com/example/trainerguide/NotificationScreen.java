package com.example.trainerguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import com.example.trainerguide.models.Notification;
import com.example.trainerguide.models.Trainer;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationScreen extends AppCompatActivity implements NotificationAdapter.OnAddClickListener {

    //Navigation view variables
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuItem profileMenu, logoutMenu, shareMenu, ratingMenu, traineeMenu;

    //Recycler view variables
    private RecyclerView notificationRecycler;
    private List<Notification> notificationsList = new ArrayList<>();
    private NotificationAdapter notificationAdapter;

    private String userId;
    private String path;

    //Firebase variables
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_screen);

        //User Info variables
        userId = getIntent().getStringExtra("UserId");
        final SharedPreferences sp;
        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        final String userType = sp.getString("ProfileType",null);
        path = userType+ "/" + userId + "/notifications";
        databaseReference = FirebaseDatabase.getInstance().getReference(path);

        //Navigation view variables
        drawerLayout = findViewById(R.id.trainer_drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.tool_bar);

        //Toolbar customisation
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.black));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        ActionBarDrawerToggle toggle = CommonNavigator.navigatorInitmethod(drawerLayout,navigationView,toolbar,this);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.appleGreen));

        //Method to re-direct the page from menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_profile:
                        startActivity(new Intent(NotificationScreen.this,ProfileScreen.class));
                        finish();
                        break;
                    case R.id.nav_trainees:
                        startActivity(new Intent(NotificationScreen.this,TraineesScreen.class));
                        break;
                    case R.id.nav_notification:
                        break;
                    case R.id.nav_trainer:
                        break;
                    case R.id.nav_logout:
                        startActivity(new Intent(NotificationScreen.this,MainActivity.class));
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        //Recycler view variables
        notificationRecycler = findViewById(R.id.trainerRecycler);
        notificationRecycler.setLayoutManager(new LinearLayoutManager(this));
        notificationAdapter = new NotificationAdapter(notificationsList, NotificationScreen.this);
        notificationRecycler.setAdapter(notificationAdapter);

    }

    public void PopulateNotifications(String userType){

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    Notification notification = snapshot.getValue(Notification.class);
                    notificationsList.add(notification);
                }
                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onAddclick(int position) {
        final Notification trainer = notificationsList.get(position);
        Intent intent;
        final SharedPreferences sp;
        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userType = sp.getString("ProfileType",null);
        if(userType == "Trainer")
        {
            intent = new Intent(NotificationScreen.this,TrainerProfileView.class);
            intent.putExtra("TrainerUserId",trainer.getUserId());
        }
        else
        {
            intent = new Intent(NotificationScreen.this,TraineeProfileview.class);
            intent.putExtra("TraineeUserId",trainer.getUserId());
        }
        startActivity(intent);
        finish();
    }
}