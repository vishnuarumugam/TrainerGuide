package com.example.trainerguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TrainerProfileView extends AppCompatActivity {

    TextView name, experience, ratingUserCount, description, email, mobile, yourTrainer, traineesCount, trainerRatings, ratingSubmit;
    RatingBar ratingBar;
    ImageView profileimg;
    Button requestbtn;
    private String traineruserId, path,navScreen, userType;

    Animation buttonBounce;

    private ProgressDialog progressDialog;

    //Firestore
    private StorageReference storageReference;

    //Navigation view variables
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuItem profileMenu, logoutMenu, shareMenu, ratingMenu, traineeMenu;
    private Trainee trainee;
    private Trainer trainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_profile_view);

        traineruserId = getIntent().getStringExtra("userId");
        navScreen = getIntent().getStringExtra("Screen");

        // loading Animation from
        buttonBounce= AnimationUtils.loadAnimation(this, R.anim.button_bounce);

        //Navigation view variables
        drawerLayout = findViewById(R.id.trainer_view_drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.tool_bar);

        //File Storage variables
        storageReference = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(this);

        path = "Trainer/" + (traineruserId != null ? traineruserId : FirebaseAuth.getInstance().getUid());

        name = findViewById(R.id.txtName);
        experience = findViewById(R.id.txtExperience);
        trainerRatings = findViewById(R.id.trainerRatings);
        description = findViewById(R.id.txtDescription);
        ratingUserCount = findViewById(R.id.txtUserCount);
        traineesCount = findViewById(R.id.trainerTraineesCount);
        requestbtn = findViewById(R.id.btnRequest);
        profileimg = findViewById(R.id.trainerimage);
        ratingBar = findViewById(R.id.ratingBar);
        email = findViewById(R.id.txtEmail);
        mobile = findViewById(R.id.txtPhnNo);
        ratingSubmit = findViewById(R.id.ratingSubmit);
        yourTrainer = findViewById(R.id.yourTrainerText);

        ratingBar.setEnabled(false);

        final SharedPreferences sp;
        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        userType = sp.getString("ProfileType",null);

        if(userType.equals("Trainer"))
        {
            requestbtn.setVisibility(View.GONE);
        }

        //Toolbar customisation
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.black));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        ActionBarDrawerToggle toggle = CommonNavigator.navigatorInitmethod(drawerLayout,navigationView,toolbar,this);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.themeColourTwo));

        PopulateUserDetails();

        //Menu Item variables
        profileMenu = findViewById(R.id.nav_profile);

        requestbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference databaseReferenceTrainer = FirebaseDatabase.getInstance().getReference(path + "/Notification");
                final DatabaseReference databaseReferenceTrainee = FirebaseDatabase.getInstance().getReference("User/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

                requestbtn.setEnabled(false);
                requestbtn.setBackgroundColor(getResources().getColor(R.color.themeColourFour));
                requestbtn.startAnimation(buttonBounce);
                databaseReferenceTrainee.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Trainee trainee = snapshot.getValue(Trainee.class);
                        System.out.println(trainee.getTrainerId());
                        if (trainee.getTrainerId() == null || trainee.getTrainerId().equals("")) {
                            if (trainee.isTrainer() == false) {
                                //Create Notification for Trainer
                                Notification notify = new Notification();
                                notify.setNotificationId(UUID.randomUUID().toString());
                                notify.setNotification(trainee.getName() + " requested for joining as your trainee");
                                notify.setNotificationHeader("New subscription request notification");
                                notify.setAddedDate(Calendar.getInstance().getTime());
                                notify.setNotificationType("Request");
                                notify.setTrainer(false);
                                notify.setUserId(trainee.getUserId());

                                databaseReferenceTrainer.child(notify.getNotificationId()).setValue(notify);

                                Toast.makeText(TrainerProfileView.this, "Request sent to Trainer", Toast.LENGTH_SHORT).show();
                                requestbtn.setEnabled(false);
                                requestbtn.setBackgroundColor(getResources().getColor(R.color.themeColourFour));
                            }
                        } else {
                            AlertDialogBox alertDialogBox = new AlertDialogBox();
                            alertDialogBox.show(getSupportFragmentManager(), "Alert");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        ratingSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                //Getting the rating and displaying it on the toast
                double userRating=ratingBar.getRating();
                Toast.makeText(getApplicationContext(), String.valueOf(userRating)+" Rating Given to Trainer " + trainer.getName(), Toast.LENGTH_SHORT).show();

                final DatabaseReference databaseReferenceTrainer = FirebaseDatabase.getInstance().getReference(path);
                DatabaseReference databaseReferenceTrainee = FirebaseDatabase.getInstance().getReference("User/" + FirebaseAuth.getInstance().getUid());

                HashMap hash= new HashMap();
                HashMap hashRating= new HashMap();

                hash.put("lastRatedDttm",Calendar.getInstance().getTime());
                double totalRating = 0;
                int usersCount = 0;

                if(trainer != null && trainer.getRating() > 0) {
                    //Rating Calculation
                    totalRating = trainer.getRating();
                    usersCount = (int) trainer.getRatedTraineescount();
                    totalRating = ((totalRating * usersCount) + userRating) / (usersCount + 1);
                }
                else
                {
                    totalRating = userRating;
                }
                hashRating.put("rating", totalRating);
                hashRating.put("ratedTraineescount", usersCount+1);

                //Update Ratings
                databaseReferenceTrainer.updateChildren(hashRating);

                //Update LastModDttm
                databaseReferenceTrainee.updateChildren(hash);

                PopulateUserDetails();

                ratingSubmit.setVisibility(View.GONE);
            }

        });

        //Method to re-direct the page from menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_profile:
                        startActivity(new Intent(TrainerProfileView.this,ProfileScreen.class));
                        finish();
                        break;
                    /*case R.id.nav_trainees:
                        startActivity(new Intent(TrainerProfileView.this,TraineesScreen.class));
                        finish();
                        break;
                    case R.id.nav_trainer:
                        startActivity(new Intent(TrainerProfileView.this,TrainerScreen.class));
                        finish();
                        break;*/
                    case R.id.nav_logout:
                        startActivity(new Intent(TrainerProfileView.this,MainActivity.class));
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
                        Toast.makeText(TrainerProfileView.this, "profile", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });

    }


    public void PopulateUserDetails(){

        DatabaseReference databaseReferenceTrainer = FirebaseDatabase.getInstance().getReference(path);
        DatabaseReference databaseReferenceTrainee = FirebaseDatabase.getInstance().getReference("User/" + FirebaseAuth.getInstance().getUid());
        //Show Progress Dialog
        progressDialog.show();
        //Set Content
        progressDialog.setContentView(R.layout.progressdialog);
        //Set Transparent Background
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        databaseReferenceTrainer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("********OnDataChange*******");
                trainer = snapshot.getValue(Trainer.class);
                System.out.println("********"+trainer+"*******");

                Picasso.get().load(trainer.getImage())
                        .placeholder(R.drawable.ic_share)
                        .fit()
                        .centerCrop()
                        .into(profileimg);
                name.setText(trainer.getName());
                experience.setText(String.valueOf(trainer.getExperience() == null ? "-" : trainer.getExperience() +" Yrs"));
                description.setText(trainer.getSubscriptionDescription() != null ? trainer.getSubscriptionDescription() : "Description not provided");
                mobile.setText(String.valueOf(trainer.getPhoneNumber()));
                email.setText(trainer.getEmail());
                ratingBar.setNumStars(5);
                if(trainer !=null) {
                    ratingBar.setRating((float)trainer.getRating());
                    DecimalFormat df = new DecimalFormat("#.#");
                    System.out.println("Rating    "+(df.format(trainer.getRating())));
                    trainerRatings.setText(trainer.getRating() <= 0 ? "-" : df.format(trainer.getRating()));
                    ratingUserCount.setText("(" + String.valueOf((int) trainer.getRatedTraineescount()) + ")");
                    traineesCount.setText( String.valueOf((int) trainer.getRatedTraineescount() == 0 ? "-" : (int) trainer.getRatedTraineescount()));
                }
                else
                {
                    //ratingBar.setRating(5);
                    ratingUserCount.setText("-");
                    traineesCount.setText("-");
                }
                //Dismiss Progress Dialog
                progressDialog.dismiss();

                if(! userType.equals("Trainer")) {
                    databaseReferenceTrainee.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            System.out.println("********OnDataChange*******");
                            trainee = snapshot.getValue(Trainee.class);
                            if (trainee != null && trainee.getTrainerId() != null && trainee.getTrainerId().equals(traineruserId)) {
                                System.out.println(Calendar.getInstance().getTime().getDate());

                                long difference = Calendar.getInstance().getTimeInMillis() - trainee.getLastRatedDttm().getTime();
                                int days = (int) (difference / (1000 * 60 * 60 * 24));
                                if (trainee.getLastRatedDttm() != null) {
                                    ratingSubmit.setVisibility(View.VISIBLE);
                                    ratingBar.setEnabled(true);
                                }

                                if (trainer != null && trainer.getUserId().equals(trainee.getTrainerId())) {
                                    requestbtn.setVisibility(View.GONE);
                                    yourTrainer.setVisibility(View.VISIBLE);
                                    yourTrainer.setText("Your Trainer !!!");
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
            if(navScreen!=null && navScreen.equals("Notification")){

                System.out.println(navScreen.toString());
                startActivity(new Intent(TrainerProfileView.this,NotificationScreen.class));
                finish();}
            else {
                startActivity(new Intent(TrainerProfileView.this, TrainerScreen.class));
                finish();
            }
        }
    }

}