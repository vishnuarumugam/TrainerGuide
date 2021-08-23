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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.example.trainerguide.models.Trainee;
import com.example.trainerguide.models.Trainer;
import com.example.trainerguide.models.User;
import com.example.trainerguide.models.UserMetaData;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
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

public class TrainerScreen extends AppCompatActivity implements TrainerAdapter.OnAddClickListener, View.OnClickListener {

    //Navigation view variables
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Button toolBarNotification;
    private MenuItem profileMenu, logoutMenu, shareMenu, ratingMenu, traineeMenu;
    private EditText searchTxt;
    private Button searchClear;

    //Common variables
    Intent intent;
    TextView noTrainerText;
    Animation buttonBounce;
    private String userType, isAdmin;
    private BottomNavigationView homeScreenTabLayout;
    private SharedPreferences sp;



    //Recycler view variables
    private RecyclerView trainerRecycler;
    private List<Trainer> trainersList = new ArrayList<>();
    private TrainerAdapter trainerAdapter;

    //Firebase variables
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Trainer");
    private DatabaseReference databaseReferenceAdd = FirebaseDatabase.getInstance().getReference("User/"+ FirebaseAuth.getInstance().getCurrentUser().getUid());

    private ValueEventListener valueEventListener;

    //Pagination
    NestedScrollView nestedScrollView;
    ProgressBar progressBar;
    int page =1,limit = 10;
    private String startAt="\"*\"";
    Boolean scroll = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_screen);
        // loading Animation from
        buttonBounce= AnimationUtils.loadAnimation(this, R.anim.button_bounce);


        //Navigation view variables
        drawerLayout = findViewById(R.id.trainer_drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.tool_bar);
        toolBarNotification = findViewById(R.id.toolBarNotification);
        toolBarNotification.setOnClickListener(this);

        //Common variables
        noTrainerText = findViewById(R.id.noTrainerText);
        noTrainerText.setVisibility(View.GONE);

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

        //Pagination
        nestedScrollView = findViewById(R.id.scroll_view);
        progressBar = findViewById(R.id.progress_bar);

        //Method to re-direct the page from menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_profile:
                        intent=new Intent(TrainerScreen.this,ProfileScreen.class);
                        //intent.putExtra("UserId",userId);
                        startActivity(intent);
                        finish();
                        break;
                    /*case R.id.nav_trainees:
                        intent=new Intent(TrainerScreen.this,TraineesScreen.class);
                        //intent.putExtra("UserId",userId);
                        startActivity(intent);
                        finish();
                        break;*/
                    case R.id.nav_notification:
                        startActivity(new Intent(TrainerScreen.this,NotificationScreen.class));
                        finish();
                        break;
                    /*case R.id.nav_trainer:
                        break;*/
                    /*case R.id.nav_foodPrep:
                        startActivity(new Intent(TrainerScreen.this,PrepareFoodChart.class));
                        finish();
                        break;*/
                    case R.id.nav_logout:
                        startActivity(new Intent(TrainerScreen.this,MainActivity.class));
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
                        Toast.makeText(TrainerScreen.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });

        //Recycler view variables
        trainerRecycler = findViewById(R.id.trainerRecycler);
        trainerRecycler.setLayoutManager(new LinearLayoutManager(this));
        trainerRecycler.setAdapter(trainerAdapter);

        //Pagination Get Data
        System.out.println("initial");
        getData("\"$key\"",startAt,limit, false);
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

        searchTxt.setOnEditorActionListener(new OnEditorActionListener() {
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

        // Search Data

        searchClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trainersList.clear();
                searchTxt.getText().clear();
                getData("\"$key\"",startAt,limit, false);
            }
        });

        /*valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Trainer trainer = dataSnapshot.getValue(Trainer.class);
                    trainersList.add(trainer);
                }
                trainerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
*/

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

        }

        homeScreenTabLayout.setSelectedItemId(R.id.trainersTab);
        homeScreenTabLayout.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.homeTab:
                        startActivity(new Intent(TrainerScreen.this,HomeScreen.class));
                        overridePendingTransition(0,0);
                        finish();
                        break;
                    case R.id.trainersTab:
                        break;
                    case R.id.traineesTab:
                        startActivity(new Intent(TrainerScreen.this,TraineesScreen.class));
                        overridePendingTransition(0,0);
                        finish();
                        break;
                    case R.id.foodListTab:startActivity(new Intent(TrainerScreen.this,FoodSourceListScreen.class));
                        overridePendingTransition(0,0);
                        finish();
                        break;
                    case R.id.profileTab:
                        startActivity(new Intent(TrainerScreen.this,ProfileScreen.class));
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
                .baseUrl("https://trainerguide-14d03.firebaseio.com")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        System.out.println("retrofit"+retrofit.toString());
        System.out.println("retrofit1");
        //Create interface
        PaginationInterface paginationInterface = retrofit.create((PaginationInterface.class));
        //Initialize Call
        Call<String> call;
        if(!IsSearched) {
            call = paginationInterface.STRING_CALL(orderBy, startAt, limit);
        }else
        {
            call = paginationInterface.STRING_CALL_SearchTrainer("\"name\"", "\""+startAt+"\"", "\""+startAt+"\\uf8ff"+"\"", 100);
        }

        System.out.println(call);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //Check Condition
                System.out.println(response);
                if(response.isSuccessful() && response.body() != null){
                    // When response is successful and not empty
                    //Hide progress bar
                    progressBar.setVisibility(View.GONE);
                    noTrainerText.setVisibility(View.GONE);

                    try {
                        JSONObject object = new JSONObject(response.body());

                        Iterator x = object.keys();

                        JSONArray jsonArray = new JSONArray();

                        while (x.hasNext()){
                            String key = (String) x.next();
                            jsonArray.put(object.get(key));

                        }

                        //jsonArray.getJSONObject(1)
                        //JSONArray Jarray  = (JSONArray) object.getJSONArray("Trainer");
                        //Initialize JSON Array
                        //JSONArray jsonArray = new JSONArray(response.body());
                        //Parse JSON Array
                        if(!IsSearched)
                        {
                            parseResult(jsonArray, false);
                        }else
                        {
                            parseResult(jsonArray, true);
                        }

                        noTrainerText.setVisibility(View.VISIBLE);
                        noTrainerText.setText("Trainers List");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        noTrainerText.setVisibility(View.VISIBLE);
                        trainersList.clear();
                        trainerAdapter = new TrainerAdapter(trainersList,TrainerScreen.this);
                        //Set Adapter
                        trainerRecycler.setAdapter(trainerAdapter);
                        noTrainerText.setText("No trainers available");
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
                trainersList.clear();
            }
            if(jsonArray.length() > 0) {
                JSONObject jsonObject1;

                if(jsonArray.length() == limit_){
                    for(int i=0; i<jsonArray.length()-1; i++){

                        System.out.println("i" + i);
                        try {
                            //if(i <= trainersList.size()+limit && i >= trainersList.size() && trainersList.size() <= jsonArray.length()){
                            //Initialize JSON object
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            //Initialize Trainer Data
                            Trainer trainer = new Trainer();
                            //SetImage
                            trainer.setUserId(jsonObject.getString("userId"));
                            trainer.setImage(jsonObject.getString("image"));
                            trainer.setName(jsonObject.getString("name"));

                            try{
                                trainer.setExperience(jsonObject.getDouble("experience")) ;
                            }
                            catch (Exception e){

                            }
                            try {
                                trainer.setSubscriptionFees(jsonObject.getDouble("subscriptionFees"));
                            }
                            catch (Exception e){

                            }
                            try {
                                trainer.setRating(jsonObject.getDouble("rating"));
                                trainer.setRatedTraineescount(jsonObject.getDouble("ratedTraineescount"));
                            }
                            catch (Exception e){

                            }
                            //Add Data
                            trainersList.add(trainer);
                            //}


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }


                if (jsonArray.length() < limit_){

                    for(int i=0; i<jsonArray.length(); i++){
                        try {
                            //if(i <= trainersList.size()+limit && i >= trainersList.size() && trainersList.size() <= jsonArray.length()){
                            //Initialize JSON object
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            //Initialize Trainer Data
                            Trainer trainer = new Trainer();
                            //SetImage
                            trainer.setUserId(jsonObject.getString("userId"));
                            trainer.setImage(jsonObject.getString("image"));
                            trainer.setName(jsonObject.getString("name"));

                            try{
                                trainer.setExperience(jsonObject.getDouble("experience")) ;
                                trainer.setSubscriptionFees(jsonObject.getDouble("subscriptionFees"));

                            }
                            catch (Exception e){
                                trainer.setExperience(0.0) ;
                                trainer.setSubscriptionFees(0.0);
                            }
                            //Add Data
                            trainersList.add(trainer);
                            //}


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    //Add Data
                    scroll=false;
                    //trainersList.add(trainer);
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

        //Initialize Adapter
        trainerAdapter = new TrainerAdapter(trainersList,TrainerScreen.this);
        //Set Adapter
        trainerRecycler.setAdapter(trainerAdapter);
        trainerAdapter.setOnAddClickListener(TrainerScreen.this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (valueEventListener != null){
            databaseReference.removeEventListener(valueEventListener);
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
            Intent intent = new Intent(TrainerScreen.this,HomeScreen.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onAddclick(int position) {
        final Trainer trainer = trainersList.get(position);

        final SharedPreferences sp;
        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userId = sp.getString("userId", null);

        Intent intent = new Intent(TrainerScreen.this,TrainerProfileView.class);
        intent.putExtra("Screen", "TrainerScreen");

        if(trainer.getUserId().equals(userId)){
            intent.putExtra("IsTrainer", true);
        }
        else {
            intent.putExtra("userId", trainer.getUserId());
            intent.putExtra("IsTrainer", true);
            intent.putExtra("ReadOnly", true);
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View option) {
        switch (option.getId()) {

            case R.id.toolBarNotification:
                option.startAnimation(buttonBounce);
                startActivity(new Intent(TrainerScreen.this,NotificationScreen.class));
                finish();
                break;
            default:
                break;
        }
    }

}