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
import android.widget.Button;
import android.widget.Toast;

import com.example.trainerguide.models.Notification;
import com.example.trainerguide.models.Trainee;
import com.example.trainerguide.models.Trainer;
import com.example.trainerguide.models.UserMetaData;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class NotificationScreen extends AppCompatActivity implements NotificationAdapter.OnAddClickListener,
        NotificationAdapter.OnApproveClickListener, NotificationAdapter.OnRejectClickListener {

    //Navigation view variables
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuItem profileMenu, logoutMenu, shareMenu, ratingMenu, traineeMenu;

    //Recycler view variables
    private RecyclerView notificationRecycler;
    private List<Notification> notificationsList = new ArrayList<>();

    private ProgressDialog progressDialog;

    private NotificationAdapter notificationAdapter;

    private String userId;
    private String path, userPath;

    //Firebase variables
    private DatabaseReference databaseReference;
    private FirebaseAuth fAuth;

    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_screen);

        //User Info variables
        userId = getIntent().getStringExtra("UserId");
        final SharedPreferences sp;
        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        fAuth = FirebaseAuth.getInstance();

        final String userType = sp.getString("ProfileType",null);
        path = userType+ "/" + fAuth.getCurrentUser().getUid() + "/Notification";
        userPath = userType+ "/" + fAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference(path);

        //Navigation view variables
        drawerLayout = findViewById(R.id.notification_drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.tool_bar);

        //Toolbar customisation
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.black));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        ActionBarDrawerToggle toggle = CommonNavigator.navigatorInitmethod(drawerLayout,navigationView,toolbar,this);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.yellow));

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
                        intent=new Intent(NotificationScreen.this,TraineesScreen.class);
                        //intent.putExtra("UserId",userId);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_notification:
                        break;
                    case R.id.nav_trainer:
                        startActivity(new Intent(NotificationScreen.this,TrainerScreen.class));
                        finish();
                        break;
                    case R.id.nav_logout:
                        startActivity(new Intent(NotificationScreen.this,MainActivity.class));
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
                        Toast.makeText(NotificationScreen.this, "profile", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });

        //Recycler view variables
        notificationRecycler = findViewById(R.id.notificationRecycler);
        notificationRecycler.setLayoutManager(new LinearLayoutManager(this));
        PopulateNotifications();
        notificationAdapter = new NotificationAdapter(notificationsList, NotificationScreen.this);
        notificationRecycler.setAdapter(notificationAdapter);
        notificationAdapter.setOnAddClickListener(NotificationScreen.this);
        notificationAdapter.setOnApproveClickListener(NotificationScreen.this);
        notificationAdapter.setOnRejectClickListener(NotificationScreen.this);
    }

    public void PopulateNotifications(){

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    Notification notification = dataSnapshot.getValue(Notification.class);
                    if(notification.getUserId()!=null){
                    notificationsList.add(notification);}
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
        if(trainer.isTrainer())
        {
            intent = new Intent(NotificationScreen.this,TrainerProfileView.class);
            intent.putExtra("TrainerUserId",trainer.getUserId());
            intent.putExtra("Screen","Notification");
        }
        else
        {
            intent = new Intent(NotificationScreen.this,TraineeProfileview.class);
            intent.putExtra("TraineeUserId",trainer.getUserId());
            intent.putExtra("Screen","Notification");
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onApproveclick(int position) {
        final Notification notification = notificationsList.get(position);
        final DatabaseReference databaseReferenceAdd = FirebaseDatabase.getInstance().getReference("User/"+ notification.getUserId());
        final DatabaseReference databaseReferenceUserList = FirebaseDatabase.getInstance().getReference(userPath);
        final DatabaseReference databaseReferenceNotification = FirebaseDatabase.getInstance().getReference("Trainer"+ "/" + fAuth.getCurrentUser().getUid() + "/");

        databaseReferenceAdd.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Trainee trainee = snapshot.getValue(Trainee.class);
                System.out.println("******()*****"+trainee.getTrainerId());
                if(trainee.getTrainerId() == null || trainee.getTrainerId().equals("") ) {
                    if (trainee.isTrainer() == false) {
                        UserMetaData traineeMetadata = new UserMetaData();
                        traineeMetadata.setUserId(trainee.getUserId());
                        traineeMetadata.setBmi(trainee.getBmi());
                        traineeMetadata.setName(trainee.getName());
                        traineeMetadata.setImage(trainee.getImage());
                        trainee.setTrainerId(fAuth.getCurrentUser().getUid());
                        HashMap<String, Object> trainerId = new HashMap<>();
                        trainerId.put("trainerId", fAuth.getCurrentUser().getUid());
                        Date currentDate = new Date();
                        // convert date to calendar
                        Calendar c = Calendar.getInstance();
                        c.setTime(currentDate);

                        c.add(Calendar.DATE,30);
                        trainerId.put("subscriptionEndDate", c.getTime());

                        Notification notify = new Notification();
                        notify.setNotificationId(UUID.randomUUID().toString());
                        notify.setNotification(traineeMetadata.getName()+" Added as Trainee");
                        notify.setAddedDate(Calendar.getInstance().getTime());
                        notify.setNotificationType("");
                        notify.setTrainer(false);
                        notify.setUserId(traineeMetadata.getUserId());

                       /* HashMap<String, Notification> notification = new HashMap<>();
                        HashMap hash= new HashMap();
                        notification.put(notify.getNotificationId(),notify);*/

                        //hash.put("Notification",notification);
                        databaseReference.child(notify.getNotificationId()).setValue(notify);
                        databaseReferenceAdd.updateChildren(trainerId);
                        databaseReferenceUserList.child( "/usersList/" + trainee.getUserId()).setValue(traineeMetadata);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        deleteNotification(notification.getNotificationId(), position);


    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer((GravityCompat.START));
        }
        else
        {
            Intent intent = new Intent(NotificationScreen.this,HomeScreen.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onRejectclick(int position) {

        final Notification notification = notificationsList.get(position);
        deleteNotification(notification.getNotificationId(), position);

        /*databaseReference.orderByKey().equalTo(notification.getNotificationId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postsnapshot :snapshot.getChildren()) {
                    postsnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }

    public void deleteNotification (String notificationId, final int position){

        databaseReference.orderByKey().equalTo(notificationId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postsnapshot :snapshot.getChildren()) {
                    postsnapshot.getRef().removeValue();
                    notificationsList.remove(position);
                    notificationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /*notificationsList = new ArrayList<>();
        PopulateNotifications();
        notificationAdapter = new NotificationAdapter(notificationsList, NotificationScreen.this);*/

        /*startActivity(new Intent( NotificationScreen.this , NotificationScreen.class));
        finish();*/
    }

}