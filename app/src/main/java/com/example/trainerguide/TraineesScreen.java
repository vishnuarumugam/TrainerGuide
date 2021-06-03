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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trainerguide.models.Trainer;
import com.example.trainerguide.models.User;
import com.example.trainerguide.models.UserMetaData;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class TraineesScreen extends AppCompatActivity implements TraineeAdapter.OnAddClickListener, View.OnClickListener, TraineeAdapter.OnViewReportListener {

    //Navigation view variables
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Button toolBarNotification;
    private MenuItem profileMenu, logoutMenu, shareMenu, ratingMenu, traineeMenu;

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

    //Common variables
    private Intent intent;
    private TextView noTraineeText;

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
        getData(userId,"\"$key\"",startAt,limit);
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
                        getData(userId,"\"$key\"",startAt,limit);
                    }

                }
            }
        });
    }

    private void getData(String userId, String orderBy, String startAt, int limit) {

        //Initialize Retrofit
        System.out.println("us"+startAt);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://trainerguide-14d03.firebaseio.com/"+path+"/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        //Create interface
        PaginationInterface paginationInterface = retrofit.create((PaginationInterface.class));
        //Initialize Call
        Call<String> call = paginationInterface.STRING_CALL_Trainees(orderBy,startAt,limit);
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
                        parseResult(jsonArray);
                        noTraineeText.setVisibility(View.VISIBLE);
                        noTraineeText.setText("Trainees List");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        noTraineeText.setVisibility(View.VISIBLE);
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

    private void parseResult(JSONArray jsonArray) {
        //Use for loop


        try {
            if(jsonArray.length() > 0) {
                JSONObject jsonObject1;

                System.out.println("jsonArray.length()"+jsonArray.length());
                System.out.println("limit.length()"+limit);

                if(jsonArray.length() == limit){
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

                            //Add Data
                            traineesList.add(userMetaData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (jsonArray.length() < limit){
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
        traineeAdapter = new TraineeAdapter(TraineesScreen.this, traineesList);
        //Set Adapter
        traineeRecycler.setAdapter(traineeAdapter);
        traineeAdapter.setOnAddClickListener(TraineesScreen.this);
        traineeAdapter.setOnViewReportListener(TraineesScreen.this);

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

    @Override
    public void onAddclick(int position) {
        final UserMetaData trainee = traineesList.get(position);

        Intent intent = new Intent(TraineesScreen.this,ProfileScreen.class);
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
                startActivity(new Intent(TraineesScreen.this,NotificationScreen.class));
                finish();
                break;
            default:
                break;
        }
    }
}