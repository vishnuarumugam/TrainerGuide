package com.example.trainerguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trainerguide.models.Notification;
import com.example.trainerguide.models.Trainer;
import com.example.trainerguide.models.User;
import com.example.trainerguide.models.UserMetaData;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class TraineesScreen extends AppCompatActivity implements TraineeAdapter.OnAddClickListener, View.OnClickListener, TraineeAdapter.OnViewReportListener,NotificationAdapter.OnDeleteClickListener{

    //Navigation view variables
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Button toolBarNotification;
    private MenuItem profileMenu, logoutMenu, shareMenu, ratingMenu, traineeMenu;
    private EditText searchTxt;
    private Button searchClear;

    //Recycler view variables
    private SwipeRefreshLayout traineeRefresh;
    private RecyclerView traineeRecycler;
    private List<UserMetaData> traineesList = new ArrayList<>();
    private TraineeAdapter traineeAdapter;

    //Firebase variables
    private ValueEventListener valueEventListener;

    //User Detail variables
    //private FirebaseAuth userId;
    private String userId;
    private String path;
    private String userType, isAdmin;
    private SharedPreferences sp;


    //Common variables
    private Intent intent;
    private TextView noTraineeText;
    Animation buttonBounce;
    private BottomNavigationView homeScreenTabLayout;



    //Pagination
    NestedScrollView nestedScrollView;
    ProgressBar progressBar;
    int page =1,limit = 10;
    private String startAt="\"*\"";
    Boolean scroll = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainees_screen);

        // loading Animation from
        buttonBounce= AnimationUtils.loadAnimation(this, R.anim.button_bounce);

        //Common variables
        noTraineeText = findViewById(R.id.noTraineeText);
        noTraineeText.setVisibility(View.GONE);

        //Pagination
        nestedScrollView = findViewById(R.id.scroll_view);
        progressBar = findViewById(R.id.progress_bar);

        //Navigation view variables
        drawerLayout = findViewById(R.id.trainee_drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.tool_bar);
        toolBarNotification = findViewById(R.id.toolBarNotification);
        toolBarNotification.setOnClickListener(this);


        //Toolbar customisation
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.themeColourOne));
        toolbar.setTitleTextColor(getResources().getColor(R.color.themeColourThree));
        ActionBarDrawerToggle toggle = CommonNavigator.navigatorInitmethod(drawerLayout,navigationView,toolbar,this);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.themeColourTwo));

        //Search box initialize
        searchTxt = findViewById(R.id.searchNametxt);
        searchClear = findViewById(R.id.searchClear);
        searchClear.setVisibility(View.GONE);


        //Menu Item variables
        profileMenu = findViewById(R.id.nav_profile);
//        traineeMenu = findViewById(R.id.nav_trainees);

        //Method to re-direct the page from menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_profile:
                        intent=new Intent(TraineesScreen.this,ProfileScreen.class);
                        //intent.putExtra("UserId",userId);
                        startActivity(intent);
                        finish();
                        break;
                    /*case R.id.nav_trainees:
                        break;*/
                    case R.id.nav_notification:
                        startActivity(new Intent(TraineesScreen.this,NotificationScreen.class));
                        finish();
                        break;
                    /*case R.id.nav_trainer:
                        startActivity(new Intent(TraineesScreen.this,TrainerScreen.class));
                        finish();
                        break;*/
                    /*case R.id.nav_foodPrep:
                        startActivity(new Intent(TraineesScreen.this,PrepareFoodChart.class));
                        finish();
                        break;*/
                    case R.id.nav_logout:
                        startActivity(new Intent(TraineesScreen.this,MainActivity.class));
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
                        Toast.makeText(TraineesScreen.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });

        //Recycler view variables
        traineeRecycler = findViewById(R.id.traineeRecycler);
        //traineeRefresh = findViewById(R.id.traineeRefresh);
        traineeRecycler.setLayoutManager(new LinearLayoutManager(this));
        //traineeAdapter = new TraineeAdapter(TraineesScreen.this,traineesList);
        traineeRecycler.setAdapter(traineeAdapter);


        //userId = getIntent().getStringExtra("UserId");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        path = "Trainer/" + userId + "/usersList";
        //path = userId + "/usersList";
        System.out.println("***userId***"+userId+"******");
        //populateTraineesData();

        //Pagination Get Data
        System.out.println("initial");
        getData("\"$key\"",startAt,limit,false);
        nestedScrollView.isSmoothScrollingEnabled();


        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //Check Condition
                System.out.println("scroll");
                if(scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){
                    //When reach last item position

                    if (scroll == true){
                        scroll=false;
                        //Increase page Size
                        page++;
                        //Show Progress Bar
                        progressBar.setVisibility(View.VISIBLE);
                        //Call Method
                        System.out.println("second");
                        if(searchTxt.getText().length() > 0)
                        {
                            //getData("\"name\"", startAt, limit, true);
                        }
                        else {
                            getData("\"$key\"", startAt, limit, false);
                        }
                    }

                }
            }
        });


        searchTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if(searchTxt.getText().length() > 0) {
                        searchTxt.clearFocus();
                        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        in.hideSoftInputFromWindow(searchTxt.getWindowToken(), 0);

                        String firstLetStr = searchTxt.getText().toString().substring(0, 1);
                        // Get remaining letter using substring
                        String remLetStr = searchTxt.getText().toString().substring(1);

                        String userName = firstLetStr.toUpperCase() + remLetStr.toLowerCase();
                        getData("\"name\"", userName, limit, true);
                    }
                    return true;
                }
                return false;
            }
        });
        searchTxt.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() > 0)
                {
                    searchClear.setVisibility(View.VISIBLE);
                }
                else
                {
                    searchClear.setVisibility(View.GONE);
                }
            }
        });


        searchClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                traineesList.clear();
                searchTxt.getText().clear();
                getData("\"$key\"",startAt,limit, false);
            }
        });

        homeScreenTabLayout = findViewById(R.id.homeScreenTabLayout);

        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        userType = sp.getString("ProfileType",null);
        isAdmin = sp.getString("isAdmin",null);

        if (userType.equals("Trainer")){

        }
        else{
            if (isAdmin.equals("0") || isAdmin==null){
                homeScreenTabLayout.getMenu().removeItem(R.id.foodListTab);
            }
            homeScreenTabLayout.getMenu().removeItem(R.id.traineesTab);

            /*homeScreenTabLayout.getMenu().removeItem(R.id.foodListTab);
            homeScreenTabLayout.getMenu().removeItem(R.id.traineesTab);
*/
        }

        homeScreenTabLayout.setSelectedItemId(R.id.traineesTab);
        homeScreenTabLayout.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.homeTab:
                        startActivity(new Intent(TraineesScreen.this,HomeScreen.class));
                        overridePendingTransition(0,0);
                        finish();
                        break;
                    case R.id.trainersTab:
                        startActivity(new Intent(TraineesScreen.this,TrainerScreen.class));
                        overridePendingTransition(0,0);
                        finish();
                        break;
                    case R.id.traineesTab:
                        break;
                    case R.id.foodListTab:
                        startActivity(new Intent(TraineesScreen.this,FoodSourceListScreen.class));
                        overridePendingTransition(0,0);
                        finish();
                        break;
                    case R.id.profileTab:
                        startActivity(new Intent(TraineesScreen.this,ProfileScreen.class));
                        overridePendingTransition(0,0);
                        finish();
                        break;
                    default:
                        break;

                }
                return false;
            }
        });
    }

    private void getData(String orderBy, String startAt, int limit, boolean IsSearched) {

        //Initialize Retrofit
        System.out.println("us"+startAt);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://trainerguide-14d03.firebaseio.com/"+path+"/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        //Create interface
        PaginationInterface paginationInterface = retrofit.create((PaginationInterface.class));
        //Initialize Call
        Call<String> call;
        if(!IsSearched) {
            call = paginationInterface.STRING_CALL_Trainees(orderBy, startAt, limit);
        }else
        {
            call = paginationInterface.STRING_CALL_SearchTrainees("\"name\"", "\""+startAt+"\"", "\""+startAt+"\\uf8ff"+"\"", 100);
        }
        System.out.println("**orderBy  "+orderBy);
        System.out.println("**startAt  "+startAt);
        System.out.println("**limit  "+limit);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //Check Condition

                System.out.println(response);
                if(response.isSuccessful() && response.body() != null){
                    // When response is successful and not empty
                    //Hide progress bar
                    progressBar.setVisibility(View.GONE);
                    noTraineeText.setVisibility(View.GONE);
                    try {
                        JSONObject object = new JSONObject(response.body());

                        Iterator x = object.keys();
                        //Initialize JSON Array
                        JSONArray jsonArray = new JSONArray();

                        while (x.hasNext()){
                            String key = (String) x.next();
                            jsonArray.put(object.get(key));
                        }

                        //Parse JSON Array
                        if(!IsSearched)
                        {
                            parseResult(jsonArray, false);
                        }else
                        {
                            parseResult(jsonArray, true);
                        }
                        noTraineeText.setVisibility(View.VISIBLE);
                        noTraineeText.setText("Trainees List");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        noTraineeText.setVisibility(View.VISIBLE);
                        traineesList.clear();
                        traineeAdapter = new TraineeAdapter(TraineesScreen.this, traineesList, buttonBounce);
                        //Set Adapter
                        traineeRecycler.setAdapter(traineeAdapter);
                        noTraineeText.setText("No Trainees under you");
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                System.out.println("*********failure*********");
            }
        });

    }

    private void parseResult(JSONArray jsonArray, boolean IsSearched) {
        //Use for loop


        try {
            int limit_;
            limit_ = limit;
            if(IsSearched)
            {
                limit_ = 100;
                traineesList.clear();
            }
            if(jsonArray.length() > 0) {
                JSONObject jsonObject1;

                System.out.println("jsonArray.length()"+jsonArray.length());
                System.out.println("limit.length()"+limit_);

                if(jsonArray.length() == limit_){
                    for(int i=0; i<jsonArray.length()-1; i++){
                        System.out.println("inside for");

                        try {
                            //Initialize JSON object
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            //Initialize USER Data
                            UserMetaData userMetaData = new UserMetaData();
                            //SetImage
                            userMetaData.setImage(jsonObject.getString("image"));
                            userMetaData.setName(jsonObject.getString("name"));
                            userMetaData.setBmi(jsonObject.getDouble("bmi"));
                            userMetaData.setUserId(jsonObject.getString("userId"));
                            try{
                                JSONObject json = jsonObject.getJSONObject("subscriptionEndDate");
                                Long timestamp = json.getLong("time");
                                Date subscriptionEndDate = new Date(timestamp);
                                userMetaData.setSubscriptionDate(subscriptionEndDate);

                            }
                            catch (Exception e){
                                System.out.println(e.getMessage());
                                System.out.println("out subscriptionEnddate");
                            }



                            //Add Data
                            traineesList.add(userMetaData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (jsonArray.length() < limit_){
                    for(int i=0; i<jsonArray.length(); i++){
                        System.out.println("inside for");

                        try {
                            //Initialize JSON object
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            //Initialize USER Data
                            UserMetaData userMetaData = new UserMetaData();
                            //SetImage
                            userMetaData.setImage(jsonObject.getString("image"));
                            userMetaData.setName(jsonObject.getString("name"));
                            userMetaData.setBmi(jsonObject.getDouble("bmi"));
                            userMetaData.setUserId(jsonObject.getString("userId"));

                            try{
                                JSONObject json = jsonObject.getJSONObject("subscriptionEndDate");
                                Long timestamp = json.getLong("time");
                                Date subscriptionEndDate = new Date(timestamp);
                                userMetaData.setSubscriptionDate(subscriptionEndDate);

                            }
                            catch (Exception e){
                                System.out.println(e.getMessage());
                                System.out.println("out subscriptionEnddate");
                            }

                            //Add Data
                            traineesList.add(userMetaData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }





                    /*System.out.println("inside array");
                    jsonObject1 = jsonArray.getJSONObject(jsonArray.length()-1);
                    UserMetaData userMetaData = new UserMetaData();
                    System.out.println("user"+jsonObject1.getString("userId"));
                    //SetImage
                    userMetaData.setImage(jsonObject1.getString("image"));
                    userMetaData.setName(jsonObject1.getString("name"));
                    userMetaData.setBmi(jsonObject1.getDouble("bmi"));
                    userMetaData.setUserId(jsonObject1.getString("userId"));
*/
                    //Add Data
                    scroll=false;
                    //traineesList.add(userMetaData);
                }
                else{
                    scroll=true;
                    System.out.println("startAt1");
                    jsonObject1 = jsonArray.getJSONObject(jsonArray.length() - 1);
                    startAt = "\""+jsonObject1.getString("userId")+"\"";
                    System.out.println("startAt"+startAt);
                }

            }


        }
        catch (JSONException e1) {
            e1.printStackTrace();
        }


        try {
            if(jsonArray.length()-1 <0) {
                System.out.println("startAt2");
                JSONObject jsonObject = jsonArray.getJSONObject(jsonArray.length() - 1);
                startAt = "\""+jsonObject.getString("userId")+"\"";

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Initialize Adapter
        System.out.println("size"+traineesList.size());
        //sortTraineesList();
        traineeAdapter = new TraineeAdapter(TraineesScreen.this, traineesList, buttonBounce);
        //Set Adapter
        traineeRecycler.setAdapter(traineeAdapter);
        traineeAdapter.setOnAddClickListener(TraineesScreen.this);
        traineeAdapter.setOnViewReportListener(TraineesScreen.this);
        traineeAdapter.setOnDeleteClickListener(TraineesScreen.this);

    }

    //Method to populate Trainee data
    public void populateTraineesData(){
        System.out.println("******T**"+userId+"**S******"+path);
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);;
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("********OnDataChange*******");
                //Trainer trainerobj = snapshot.getValue(Trainer.class);
                //HashMap<String,UserMetaData> users =  trainerobj.getUsersList();
                System.out.println("********snap*******"+snapshot.getChildrenCount());
                for(DataSnapshot users : snapshot.getChildren()){

                    traineesList.add(users.getValue(UserMetaData.class));
                }
                //traineesList.addAll(users);
                traineeAdapter.notifyDataSetChanged();
                traineeRefresh.setRefreshing(false);

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
            Intent intent = new Intent(TraineesScreen.this,HomeScreen.class);
            startActivity(intent);
            finish();
        }
    }



    public void onAddclick(int position) {
        final UserMetaData trainee = traineesList.get(position);

        Intent intent = new Intent(TraineesScreen.this,TraineeProfileview.class);
        intent.putExtra("userId",trainee.getUserId());
        intent.putExtra("IsTrainer", false);
        intent.putExtra("ReadOnly", true);
        intent.putExtra("Screen", "TraineesScreen");
        startActivity(intent);
        finish();
    }

    @Override
    public void onViewReport(int position){
        final UserMetaData trainee = traineesList.get(position);
        Intent intent = new Intent(TraineesScreen.this,UserReport.class);
        intent.putExtra("userId",trainee.getUserId());
        intent.putExtra("IsTrainer", false);
        intent.putExtra("Screen", "TraineesScreen");
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View option) {
        switch (option.getId()) {

            case R.id.toolBarNotification:
                option.startAnimation(buttonBounce);
                startActivity(new Intent(TraineesScreen.this,NotificationScreen.class));
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDeleteclick(int position) {

        UserMetaData userMetaData = traineesList.get(position);

        AlertDialog dialog = new AlertDialog.Builder(TraineesScreen.this).create();
        dialog.setMessage(getResources().getString(R.string.deleteTrainee));
        dialog.setCancelable(true);

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
                DatabaseReference trainerDatabaseReference = FirebaseDatabase.getInstance().getReference("Trainer/"+userId);

                trainerDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Trainer trainer = snapshot.getValue(Trainer.class);

                        Notification notify = new Notification();
                        notify.setNotificationId(UUID.randomUUID().toString());
                        notify.setNotification(trainer.getName() + " requested for ending the subscription");
                        notify.setNotificationHeader("Remove subscription request notification");
                        notify.setAddedDate(Calendar.getInstance().getTime());
                        notify.setNotificationType("Remove");
                        notify.setTrainer(true);
                        notify.setUserId(trainer.getUserId());

                        DatabaseReference traineeDatabaseReference = FirebaseDatabase.getInstance().getReference("User/"+userMetaData.getUserId()+"/Notification/"+notify.getNotificationId());
                        traineeDatabaseReference.setValue(notify);

                        Toast.makeText(TraineesScreen.this,"Remove request sent to Trainee", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {

            }
        });
        dialog.show();

    }

    public void sortTraineesList(){

        Collections.sort(traineesList, Comparator.comparing(UserMetaData::getSubscriptionEndDate).reversed());

        System.out.println("sortTraineesList");
        /*notificationsList.sort(new Comparator<Notification>() {
            @Override
            public int compare(Notification o1, Notification o2) {
                return o1.getAddedDate().compareTo(o2.getAddedDate());
            }
        });*/
    }

}