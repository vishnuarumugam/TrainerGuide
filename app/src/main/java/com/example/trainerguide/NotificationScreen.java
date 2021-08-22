package com.example.trainerguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class NotificationScreen extends AppCompatActivity implements NotificationAdapter.OnAddClickListener,
        NotificationAdapter.OnApproveClickListener, NotificationAdapter.OnRejectClickListener, NotificationAdapter.OnDeleteClickListener {

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
    private String path, userPath, userType;

    //Firebase variables
    private DatabaseReference databaseReference;
    private FirebaseAuth fAuth;

    //Common variables
    Intent intent;
    TextView noNotificationText;
    ProgressBar progressBar;
    SwipeRefreshLayout notificationRefresh;
    private String navigationScreen ="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_screen);

        //User Info variables
        userId = getIntent().getStringExtra("UserId");
        final SharedPreferences sp;
        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        fAuth = FirebaseAuth.getInstance();

        userType = sp.getString("ProfileType",null);
        path = userType+ "/" + fAuth.getCurrentUser().getUid() + "/Notification";
        userPath = userType+ "/" + fAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference(path);
        if (getIntent().hasExtra("Screen")){
            navigationScreen = getIntent().getExtras().getString("Screen", "");
        }

        //Navigation view variables
        drawerLayout = findViewById(R.id.notification_drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.tool_bar);

        //Common variables
        noNotificationText = findViewById(R.id.noNotificationText);
        progressBar = findViewById(R.id.progress_bar);
        noNotificationText.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        notificationRefresh = findViewById(R.id.notificationRefresh);

        notificationRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                PopulateNotifications();
            }
        });

        //Toolbar customisation
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.themeColourOne));
        toolbar.setTitleTextColor(getResources().getColor(R.color.themeColourThree));
        ActionBarDrawerToggle toggle = CommonNavigator.navigatorInitmethod(drawerLayout,navigationView,toolbar,this);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.themeColourTwo));

        //Method to re-direct the page from menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_profile:
                        startActivity(new Intent(NotificationScreen.this,ProfileScreen.class));
                        finish();
                        break;
                    /*case R.id.nav_trainees:
                        intent=new Intent(NotificationScreen.this,TraineesScreen.class);
                        //intent.putExtra("UserId",userId);
                        startActivity(intent);
                        finish();
                        break;*/
                    case R.id.nav_notification:
                        break;
                    /*case R.id.nav_trainer:
                        startActivity(new Intent(NotificationScreen.this,TrainerScreen.class));
                        finish();
                        break;*/
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
                        Toast.makeText(NotificationScreen.this, "Coming Soon", Toast.LENGTH_SHORT).show();
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
        notificationAdapter.setOnDeleteClickListener(this);
    }

    public void sortNotification(){

        Collections.sort(notificationsList, Comparator.comparing(Notification::getAddedDate).reversed());
        /*notificationsList.sort(new Comparator<Notification>() {
            @Override
            public int compare(Notification o1, Notification o2) {
                return o1.getAddedDate().compareTo(o2.getAddedDate());
            }
        });*/
    }


    public void PopulateNotifications(){
        if (! notificationsList.isEmpty()){
            notificationsList.clear();
            notificationAdapter.notifyDataSetChanged();
        }
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    Notification notification = dataSnapshot.getValue(Notification.class);
                    if(notification.getUserId()!=null){
                    notificationsList.add(notification);}
                    System.out.println(notification);
                }
                sortNotification();
                notificationAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                notificationRefresh.setRefreshing(false);
                EnableNoNotification();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //EnableNoNotification();
    }
    public void EnableNoNotification(){

        if (notificationsList.isEmpty()){
            noNotificationText.setText("No New Request notifications");
            noNotificationText.setVisibility(View.VISIBLE);
        }
        else {
            noNotificationText.setText("New Request notifications");
            noNotificationText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAddclick(int position) {
        final Notification trainer = notificationsList.get(position);
        Intent intent;

        if (trainer.getUserId()!=null && trainer.getUserId().length()>1 ){
            if(trainer.isTrainer())
            {
                 intent = new Intent(NotificationScreen.this,TrainerProfileView.class);
                intent.putExtra("userId",trainer.getUserId());
                /*intent.putExtra("IsTrainer", true);
                intent.putExtra("ReadOnly", true);*/
                intent.putExtra("Screen", "NotificationScreen");
            }
            else
            {
                intent = new Intent(NotificationScreen.this,TraineeProfileview.class);
                intent.putExtra("userId",trainer.getUserId());
                /*intent.putExtra("IsTrainer", false);
                intent.putExtra("ReadOnly", true);*/
                intent.putExtra("Screen", "NotificationScreen");
          }

            startActivity(intent);
            finish();

        }

    }

    @Override
    public void onApproveclick(int position) {
        Notification notification = notificationsList.get(position);

        if (notification.isTrainer()){
            DatabaseReference databaseReferenceTrainer = FirebaseDatabase.getInstance().getReference("Trainer"+ "/" +notification.getUserId());
            DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference("User/"+ fAuth.getCurrentUser().getUid());
            final Trainee[] trainee = new Trainee[1];
            final Trainer[] trainer = new Trainer[1];
            if (notification.getNotificationType().equals("Remove")){

                databaseReferenceTrainer.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        trainer[0] = snapshot.getValue(Trainer.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        trainee[0] = snapshot.getValue(Trainee.class);
                        if((trainee[0].getTrainerId() != null && !trainee[0].getTrainerId().equals("")) ){
                            Notification notifyTrainee = new Notification();
                            notifyTrainee.setNotificationId(UUID.randomUUID().toString());
                            notifyTrainee.setNotification("You are removed as a trainee from " + trainer[0].getName() + ".");
                            notifyTrainee.setAddedDate(Calendar.getInstance().getTime());
                            notifyTrainee.setNotificationHeader("Notification");
                            notifyTrainee.setNotificationType("");
                            notifyTrainee.setTrainer(false);
                            notifyTrainee.setUserId("");

                            Notification notifyTrainer = new Notification();
                            notifyTrainer.setNotificationId(UUID.randomUUID().toString());
                            notifyTrainer.setNotification(trainee[0].getName()  + " removed as Trainee.");
                            notifyTrainer.setAddedDate(Calendar.getInstance().getTime());
                            notifyTrainer.setNotificationHeader("Notification");
                            notifyTrainer.setNotificationType("");
                            notifyTrainer.setTrainer(false);
                            notifyTrainer.setUserId("");


                            databaseReferenceTrainer.child("/Notification/" +notifyTrainer.getNotificationId()).setValue(notifyTrainer);
                            databaseReferenceTrainer.child("/usersList/").orderByKey().equalTo(trainee[0].getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot postsnapshot :snapshot.getChildren()) {
                                        postsnapshot.getRef().removeValue();

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            //deleteNotification(notification.getNotificationId(), position);
                            databaseReferenceUser.child("trainerId").removeValue();
                            databaseReferenceUser.child("subscriptionEndDate").removeValue();
                            databaseReferenceUser.child("/Notification/" +notifyTrainee.getNotificationId()).setValue(notifyTrainee);
                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            else {
                CustomDialogClass customDialogClass = new CustomDialogClass(NotificationScreen.this, "Attention", "Requested user has been already removed from your subscription", "Normal");
                customDialogClass.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                customDialogClass.show();
            }



        }
        else{
            DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference("User"+ "/" +notification.getUserId());
            DatabaseReference databaseReferenceTrainer = FirebaseDatabase.getInstance().getReference("Trainer/"+ fAuth.getCurrentUser().getUid());
            boolean removeAll = false;
            databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Trainee trainee = snapshot.getValue(Trainee.class);
                    if((trainee.getTrainerId() == null || trainee.getTrainerId().equals("")) ) {
                        if(notification.getNotificationType().equals("Request")) {
                            //deleteNotification(notification.getNotificationId(), position);

                            Date currentDate = new Date();
                            // convert date to calendar
                            Calendar c = Calendar.getInstance();
                            c.setTime(currentDate);

                            c.add(Calendar.DATE, 30);
                            UserMetaData traineeMetadata = new UserMetaData();
                            traineeMetadata.setUserId(trainee.getUserId());
                            traineeMetadata.setBmi(trainee.getBmi());
                            traineeMetadata.setName(trainee.getName());
                            traineeMetadata.setImage(trainee.getImage());
                            traineeMetadata.setSubscriptionDate(trainee.getSubscriptionEndDate());
                            trainee.setTrainerId(fAuth.getCurrentUser().getUid());


                            //HashMap<String, Object> trainerId = new HashMap<>();
                            HashMap trainerId= new HashMap();
                            trainerId.put("trainerId", fAuth.getCurrentUser().getUid());
                            trainerId.put("subscriptionEndDate", c.getTime());

                            System.out.println("subscriptionEndDate" + trainerId.get("subscriptionEndDate"));
                            System.out.println("subscriptionEndDate" + trainerId.get("trainerId"));
                            System.out.println(trainerId);


                            Notification notify = new Notification();
                            notify.setNotificationId(UUID.randomUUID().toString());
                            notify.setNotification(traineeMetadata.getName() + " Added as Trainee");
                            notify.setAddedDate(Calendar.getInstance().getTime());
                            notify.setNotificationType("");
                            notify.setNotificationHeader("Notification");
                            notify.setTrainer(false);
                            notify.setUserId(traineeMetadata.getUserId());

                            Notification notifyTrainee = new Notification();
                            notifyTrainee.setNotificationId(UUID.randomUUID().toString());
                            notifyTrainee.setNotification("Your request has been accepted. Please check your Profile." );
                            notifyTrainee.setAddedDate(Calendar.getInstance().getTime());
                            notifyTrainee.setNotificationType("");
                            notifyTrainee.setNotificationHeader("Notification");
                            notifyTrainee.setTrainer(false);
                            notifyTrainee.setUserId(trainee.getUserId());

                            databaseReferenceTrainer.child("/Notification/" +notify.getNotificationId()).setValue(notify);
                            databaseReferenceTrainer.child("/usersList/" + trainee.getUserId()).setValue(traineeMetadata);
                            databaseReferenceUser.updateChildren(trainerId);
                            databaseReferenceUser.child("/Notification/" +notifyTrainee.getNotificationId()).setValue(notifyTrainee);
                        }
                    }
                    else if (notification.getNotificationType().equals("Extend")){
                        //deleteNotification(notification.getNotificationId(), position);

                        if((trainee.getTrainerId() != null && !trainee.getTrainerId().equals("")) ) {
                            HashMap hash= new HashMap();
                            Date currentDate = trainee.getSubscriptionEndDate();
                            // convert date to calendar
                            Calendar c = Calendar.getInstance();
                            c.setTime(currentDate);

                            c.add(Calendar.DATE,30);
                            hash.put("subscriptionEndDate",c.getTime());


                            Notification notify = new Notification();
                            notify.setNotificationId(UUID.randomUUID().toString());
                            notify.setNotification(trainee.getName() + " has been extended as your Trainee for 30 Days");
                            notify.setAddedDate(Calendar.getInstance().getTime());
                            notify.setNotificationHeader("Notification");
                            notify.setNotificationType("");
                            notify.setTrainer(false);
                            notify.setUserId(trainee.getUserId());

                            Notification notifyTrainee = new Notification();
                            notifyTrainee.setNotificationId(UUID.randomUUID().toString());
                            notifyTrainee.setNotification(" Your subscription has been extended for 30 Days");
                            notifyTrainee.setAddedDate(Calendar.getInstance().getTime());
                            notifyTrainee.setNotificationHeader("Notification");
                            notifyTrainee.setNotificationType("");
                            notifyTrainee.setTrainer(false);
                            notifyTrainee.setUserId(trainee.getUserId());

                            databaseReferenceTrainer.child("/Notification/" +notify.getNotificationId()).setValue(notify);
                            databaseReferenceTrainer.child("/usersList/" + trainee.getUserId()).updateChildren(hash);
                            databaseReferenceUser.child("/Notification/" +notifyTrainee.getNotificationId()).setValue(notifyTrainee);
                            databaseReferenceUser.updateChildren(hash);

                        }
                        else {
                            CustomDialogClass customDialogClass = new CustomDialogClass(NotificationScreen.this, "Attention", "Requested user has been already removed from your subscription", "Normal");
                            customDialogClass.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            customDialogClass.show();
                        }


                    }

                    else if (notification.getNotificationType().equals("Remove")){
                        if((trainee.getTrainerId() != null && !trainee.getTrainerId().equals("")) ){
                            /*HashMap<String, Object> trainerId = new HashMap<>();
                            //trainerId.put("trainerId", null);
                            Date currentDate = new Date();
                            // convert date to calendar
                            Calendar c = Calendar.getInstance();
                            c.setTime(currentDate);

                            c.add(Calendar.DATE, 30);*/
                            //trainerId.put("subscriptionEndDate", c.getTime());

                            Notification notify = new Notification();
                            notify.setNotificationId(UUID.randomUUID().toString());
                            notify.setNotification(trainee.getName()  + " removed as Trainee.");
                            notify.setAddedDate(Calendar.getInstance().getTime());
                            notify.setNotificationHeader("Notification");
                            notify.setNotificationType("");
                            notify.setTrainer(false);
                            notify.setUserId("");

                            Notification notifyTrainee = new Notification();
                            notifyTrainee.setNotificationId(UUID.randomUUID().toString());
                            notifyTrainee.setNotification("Your remove request has been accepted by Trainer." );
                            notifyTrainee.setAddedDate(Calendar.getInstance().getTime());
                            notifyTrainee.setNotificationType("");
                            notifyTrainee.setNotificationHeader("Notification");
                            notifyTrainee.setTrainer(false);
                            notifyTrainee.setUserId(trainee.getUserId());

                            databaseReferenceTrainer.child("/Notification/" +notify.getNotificationId()).setValue(notify);
                            databaseReferenceTrainer.child("/usersList/").orderByKey().equalTo(notification.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot postsnapshot :snapshot.getChildren()) {
                                        postsnapshot.getRef().removeValue();

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            //deleteNotification(notification.getNotificationId(), position);
                            databaseReferenceUser.child("trainerId").removeValue();
                            databaseReferenceUser.child("subscriptionEndDate").removeValue();
                            databaseReferenceUser.child("/Notification/" +notifyTrainee.getNotificationId()).setValue(notifyTrainee);
                        }
                        else{
                            CustomDialogClass customDialogClass = new CustomDialogClass(NotificationScreen.this, "Attention", "Requested user has been already removed from your subscription", "Normal");
                            customDialogClass.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            customDialogClass.show();

                        }

                    }
                    else {

                            CustomDialogClass customDialogClass = new CustomDialogClass(NotificationScreen.this, "Attention", "Requested user has been already assigned to a trainer", "Normal");
                            customDialogClass.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            customDialogClass.show();


                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        DatabaseReference databaseReferenceAdd = FirebaseDatabase.getInstance().getReference("User/"+ notification.getUserId());
        DatabaseReference databaseReferenceUserList = FirebaseDatabase.getInstance().getReference(userPath);
        DatabaseReference databaseReferenceNotification = FirebaseDatabase.getInstance().getReference("Trainer"+ "/" + fAuth.getCurrentUser().getUid() + "/");

       /* databaseReferenceAdd.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Trainee trainee = snapshot.getValue(Trainee.class);
                System.out.println("******()*****"+trainee.getTrainerId());
                if((trainee.getTrainerId() == null || trainee.getTrainerId().equals("")) ) {
                    if (trainee.isTrainer() == false) {
                        if(notification.getNotificationType().equals("Request")) {
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

                            c.add(Calendar.DATE, 30);
                            trainerId.put("subscriptionEndDate", c.getTime());

                            Notification notify = new Notification();
                            notify.setNotificationId(UUID.randomUUID().toString());
                            notify.setNotification(traineeMetadata.getName() + " Added as Trainee");
                            notify.setAddedDate(Calendar.getInstance().getTime());
                            notify.setNotificationType("");
                            notify.setTrainer(false);
                            notify.setUserId(traineeMetadata.getUserId());

                            Notification notifyTrainee = new Notification();
                            notify.setNotificationId(UUID.randomUUID().toString());
                            notify.setNotification("Your request has been accepted. Please check your Profile." );
                            notify.setAddedDate(Calendar.getInstance().getTime());
                            notify.setNotificationType("");
                            notify.setTrainer(false);
                            notify.setUserId(trainee.getUserId());

                       *//* HashMap<String, Notification> notification = new HashMap<>();
                        HashMap hash= new HashMap();
                        notification.put(notify.getNotificationId(),notify);*//*

                            //hash.put("Notification",notification);
                            databaseReference.child(notify.getNotificationId()).setValue(notify);
                            databaseReferenceAdd.updateChildren(trainerId);
                            databaseReferenceUserList.child("/usersList/" + trainee.getUserId()).setValue(traineeMetadata);
                        }

                    }
                }
                if(notification.getNotificationType().equals("Extend")){
                    DatabaseReference databaseReferenceUserNotification = FirebaseDatabase.getInstance().getReference("User/"+ notification.getUserId()+"/Notification");
                    HashMap hash= new HashMap();
                    Date currentDate = trainee.getSubscriptionEndDate();
                    // convert date to calendar
                    Calendar c = Calendar.getInstance();
                    c.setTime(currentDate);

                    c.add(Calendar.DATE,30);
                    hash.put("subscriptionEndDate",c.getTime());


                    Notification notify = new Notification();
                    notify.setNotificationId(UUID.randomUUID().toString());
                    notify.setNotification(trainee.getName() + " has been extended as your Trainee for 30 Days");
                    notify.setAddedDate(Calendar.getInstance().getTime());
                    notify.setNotificationType("");
                    notify.setTrainer(false);
                    notify.setUserId(trainee.getUserId());

                    Notification notifyTrainee = new Notification();
                    notifyTrainee.setNotificationId(UUID.randomUUID().toString());
                    notifyTrainee.setNotification(" Your subscription has been extended for 30 Days");
                    notifyTrainee.setAddedDate(Calendar.getInstance().getTime());
                    notifyTrainee.setNotificationType("");
                    notifyTrainee.setTrainer(false);
                    notifyTrainee.setUserId(trainee.getUserId());


                    databaseReference.child(notify.getNotificationId()).setValue(notify);
                    databaseReferenceUserNotification.child(notify.getNotificationId()).setValue(notifyTrainee);
                    databaseReferenceAdd.updateChildren(hash);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("failed" + error.getDetails());
            }
        });*/
        if (notification.getNotificationType().equals("Remove")){
            System.out.println("deleteAllNotification");
            deleteAllNotification(fAuth.getCurrentUser().getUid(), notification.getUserId());
        }
        else{
            deleteNotification(notification.getNotificationId(), position);
        }

        /*try {
            Thread.sleep(1000);

        }catch (Exception e){
            e.printStackTrace();
        }
        PopulateNotifications();
*/
    }

    private void deleteAllNotification(String loggedInId, String toBeDeletedId) {

        if (userType.equals("Trainer")){
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot :snapshot.getChildren()) {
                        Notification notification = postSnapshot.getValue(Notification.class);

                        if (notification.getUserId().equals(toBeDeletedId)){
                            postSnapshot.getRef().removeValue();
                        }
                    }
                    PopulateNotifications();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            DatabaseReference traineeDatabaseReference = FirebaseDatabase.getInstance().getReference("User" + "/" + toBeDeletedId + "/Notification");

            traineeDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot :snapshot.getChildren()) {
                        Notification notification = postSnapshot.getValue(Notification.class);

                        if (notification.getUserId().equals(loggedInId)){
                            postSnapshot.getRef().removeValue();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        else{
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot :snapshot.getChildren()) {
                        Notification notification = postSnapshot.getValue(Notification.class);

                        if (notification.getUserId().equals(toBeDeletedId)){
                            postSnapshot.getRef().removeValue();
                        }
                    }
                    PopulateNotifications();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            DatabaseReference trainerDatabaseReference = FirebaseDatabase.getInstance().getReference("Trainer" + "/" + toBeDeletedId + "/Notification");

            trainerDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot :snapshot.getChildren()) {
                        Notification notification = postSnapshot.getValue(Notification.class);

                        if (notification.getUserId().equals(loggedInId)){
                            postSnapshot.getRef().removeValue();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }



    }


    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer((GravityCompat.START));
        }
        else
        {
            {
                Intent intent;
                switch(navigationScreen){

                    case "TraineesScreen":
                        intent = new Intent(NotificationScreen.this,TraineesScreen.class);
                        startActivity(intent);
                        finish();
                        break;
                    case "TrainerScreen":
                        intent = new Intent(NotificationScreen.this,TrainerScreen.class);
                        startActivity(intent);
                        finish();
                        break;
                    case  "NotificationScreen":
                        intent = new Intent(NotificationScreen.this,NotificationScreen.class);
                        startActivity(intent);
                        finish();
                        break;
                    case "FoodSourceListScreen":
                        intent = new Intent(NotificationScreen.this,FoodSourceListScreen.class);
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        intent = new Intent(NotificationScreen.this,HomeScreen.class);
                        startActivity(intent);
                        finish();
                        break;
                }

            }
        }
    }

    @Override
    public void onRejectclick(int position) {

        final Notification notification = notificationsList.get(position);
        deleteNotification(notification.getNotificationId(), position);

    }

    @Override
    public void onDeleteclick(int position) {
        final Notification notification = notificationsList.get(position);
        deleteNotification(notification.getNotificationId(), position);
    }

    public void deleteNotification (String notificationId, final int position){
        notificationsList.remove(position);
        notificationAdapter.notifyDataSetChanged();
        databaseReference.orderByKey().equalTo(notificationId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postsnapshot :snapshot.getChildren()) {
                    postsnapshot.getRef().removeValue();

                    notificationAdapter.notifyDataSetChanged();
                }
                PopulateNotifications();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


}