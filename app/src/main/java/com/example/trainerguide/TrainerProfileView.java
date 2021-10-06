package com.example.trainerguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.MailTo;
import android.net.Uri;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.trainerguide.models.Notification;
import com.example.trainerguide.models.Trainee;
import com.example.trainerguide.models.Trainer;
import com.example.trainerguide.models.UserMetaData;
import com.facebook.shimmer.ShimmerFrameLayout;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TrainerProfileView extends AppCompatActivity {

    private TextView name, experience, ratingUserCount, description, email, mobile, yourTrainer, traineesCount, trainerRatings, ratingSubmit, fees;
    private ImageButton makeCall, makeEmail;
    private RatingBar ratingBar;
    private ImageView profileimg;
    private Button requestbtn;
    private ImageButton editRatingBtn;
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
    private ShimmerFrameLayout trainerImageShimmer;
    private View trainerShimmerView;

    static int PERMISSION_CODE=100;

    private RequestQueue requestQueue;
    private String notificationURl = "https://fcm.googleapis.com/fcm/send";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_profile_view);

        //notification
        requestQueue = Volley.newRequestQueue(this);

        trainerImageShimmer = findViewById(R.id.trainer_image_shimmer);
        trainerShimmerView = findViewById(R.id.trainer_image_shimmer_view);
        trainerImageShimmer.startShimmer();

        traineruserId = getIntent().getStringExtra("userId");
        navScreen = getIntent().getStringExtra("Screen");

        // loading Animation from
        buttonBounce= AnimationUtils.loadAnimation(this, R.anim.button_bounce);

        //Navigation view variables
        //drawerLayout = findViewById(R.id.trainer_view_drawer_layout);
        //navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.back_tool_bar);

        toolbar.setTitle("Trainer Profile");
        //File Storage variables
        storageReference = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(this);

        path = "Trainer/" + (traineruserId != null ? traineruserId : FirebaseAuth.getInstance().getUid());

        name = findViewById(R.id.txtName);
        experience = findViewById(R.id.txtExperience);
        fees = findViewById(R.id.txtFees);
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
        editRatingBtn = findViewById(R.id.editRatingbtn);

        makeCall = findViewById(R.id.makeCall);
        makeEmail = findViewById(R.id.makeEmail);

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
        /*toolbar.setBackgroundColor(getResources().getColor(R.color.themeColourOne));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));*/
        //ActionBarDrawerToggle toggle = CommonNavigator.navigatorInitmethod(drawerLayout,navigationView,toolbar,this);
        //toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.themeColourTwo));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnBackPressed();
            }
        });



        PopulateUserDetails();
        editRatingBtn.setVisibility(View.GONE);



        makeCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(TrainerProfileView.this,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(TrainerProfileView.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CODE);
                }
                else{
                    makeCall();
                }

            }
        });
        makeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

        editRatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trainee != null && trainee.getTrainerId() != null && trainee.getTrainerId().equals(traineruserId)) {
                    if (trainee.getLastRatedDttm() == null) {
                        ratingBar.setEnabled(true);
                        editRatingBtn.setVisibility(View.GONE);
                        ratingSubmit.setVisibility(View.VISIBLE);
                    } else {
                        long difference = Calendar.getInstance().getTimeInMillis() - trainee.getLastRatedDttm().getTime();
                        int days = (int) (difference / (1000 * 60 * 60 * 24));
                        if (days > 2) {
                            ratingBar.setEnabled(true);
                            editRatingBtn.setVisibility(View.GONE);
                            ratingSubmit.setVisibility(View.VISIBLE);
                        } else {
                            if(days - 2 != 0) {
                                Toast.makeText(TrainerProfileView.this, "You can rate " + trainer.getName() + " in " + (2 - days) + " days", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(TrainerProfileView.this, "You can rate " + trainer.getName() + " tomorrow", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        });

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
                                sendNotification(trainer.getName(), "New subscription request notification",trainee.getName() + " requested for joining as your trainee" );
                                Toast.makeText(TrainerProfileView.this, "Request sent to Trainer", Toast.LENGTH_SHORT).show();
                                requestbtn.setEnabled(false);
                                requestbtn.setBackgroundColor(getResources().getColor(R.color.themeColourFour));
                            }
                        } else {
                            /*AlertDialogBox alertDialogBox = new AlertDialogBox();
                            alertDialogBox.show(getSupportFragmentManager(), "Alert");*/
                            CustomDialogClass customDialogClass = new CustomDialogClass(TrainerProfileView.this, "Multiple Trainer Alert!", "Hey! You cannot be under two trainers", "Normal");
                            customDialogClass.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            customDialogClass.show();
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
                ratingBar.setEnabled(false);
                editRatingBtn.setVisibility(View.VISIBLE);
            }

        });

        //Method to re-direct the page from menu
        /*navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_profile:
                        startActivity(new Intent(TrainerProfileView.this,ProfileScreen.class));
                        finish();
                        break;
                    *//*case R.id.nav_trainees:
                        startActivity(new Intent(TrainerProfileView.this,TraineesScreen.class));
                        finish();
                        break;
                    case R.id.nav_trainer:
                        startActivity(new Intent(TrainerProfileView.this,TrainerScreen.class));
                        finish();
                        break;*//*
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
        });*/

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
                        .fit()
                        .centerCrop()
                        .into(profileimg);

                name.setText(trainer.getName());
                fees.setText(trainer.getSubscriptionFees() == null ? "-" : String.valueOf(trainer.getSubscriptionFees()));
                experience.setText(String.valueOf(trainer.getExperience() == null ? "-" : trainer.getExperience() +" Yrs"));
                description.setText(trainer.getSubscriptionDescription() != null ? trainer.getSubscriptionDescription() : "Description not provided");
                mobile.setText(String.valueOf(trainer.getPhoneNumber()));
                email.setText(trainer.getEmail());
                ratingBar.setNumStars(5);
                ratingBar.setEnabled(false);
                if(trainer !=null) {
                    ratingBar.setRating((float)trainer.getRating());
                    DecimalFormat df = new DecimalFormat("#.#");
                    System.out.println("Rating    "+(df.format(trainer.getRating())));
                    trainerRatings.setText(trainer.getRating() <= 0 ? "-" : df.format(trainer.getRating()));
                    ratingUserCount.setText("(" + String.valueOf((int) trainer.getRatedTraineescount()) + ")");
                    if(trainer.getUsersList() != null) {
                        traineesCount.setText(String.valueOf((int) trainer.getUsersList().size() == 0 ? "-" : (int) trainer.getUsersList().size()));
                    }
                    else
                    {
                        traineesCount.setText("-");
                    }
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
                                editRatingBtn.setVisibility(View.VISIBLE);
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
                trainerImageShimmer.stopShimmer();
                trainerShimmerView.setBackgroundColor(getResources().getColor(R.color.transparent));
                //traineeImageShimmer.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void OnBackPressed()
    {
        if(navScreen!=null && navScreen.equals("NotificationScreen")){

            System.out.println(navScreen.toString());
            startActivity(new Intent(TrainerProfileView.this,NotificationScreen.class));
            finish();}
        else if (navScreen.equals("FindTrainer")){
            startActivity(new Intent(TrainerProfileView.this, FindTrainer.class));
            finish();
        }
        else {
            startActivity(new Intent(TrainerProfileView.this, TrainerScreen.class));
            finish();
        }
    }

    public void makeCall(){
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:"+mobile.getText()));
        startActivity(intent);

    }
    public void sendEmail(){
        String emailSend = email.getText().toString();


        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email.getText().toString()});
        intent.setType("text/plain");


        startActivity(Intent.createChooser(intent, "Choose an Email client :"));


    }
    @Override
    public void onBackPressed()
    {
            OnBackPressed();
    }

    public void sendNotification(String userName, String title, String body){
        JSONObject messageObject = new JSONObject();
        try {
            messageObject.put("to","/topics/"+ userName);
            JSONObject notificationObject = new JSONObject();
            notificationObject.put("title",title);
            notificationObject.put("body",body);
            notificationObject.put("icon",R.drawable.app_logo);
            messageObject.put("notification",notificationObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, notificationURl,
                    messageObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println("response" + response);
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("error" + error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=AAAAQPsfOgo:APA91bG8Xy-sfM3ua8fd5CycH0ucemi7AgOUQ4-b-EwzpQutlXanKM_pHWpybDqIutacxItiuplb-MYRK28MwaRqzVyfhLLER7TYMKjHYfF_KYd4s1n2wWcRSIRSqB1VfzjQDn5f86Xj");
                    return header;
                }
            };

            requestQueue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}