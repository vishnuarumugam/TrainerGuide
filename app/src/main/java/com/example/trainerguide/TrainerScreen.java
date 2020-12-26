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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.trainerguide.models.Trainee;
import com.example.trainerguide.models.Trainer;
import com.example.trainerguide.models.User;
import com.example.trainerguide.models.UserMetaData;
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

public class TrainerScreen extends AppCompatActivity implements TrainerAdapter.OnAddClickListener {

    //Navigation view variables
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuItem profileMenu, logoutMenu, shareMenu, ratingMenu, traineeMenu;

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
    int page =1,limit = 5;
    private String startAt="\"\"";
    Boolean scroll = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_screen);

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

        //Menu Item variables
        profileMenu = findViewById(R.id.nav_profile);
        traineeMenu = findViewById(R.id.nav_trainees);

        //Pagination
        nestedScrollView = findViewById(R.id.scroll_view);
        progressBar = findViewById(R.id.progress_bar);

        //Method to re-direct the page from menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_profile:
                        startActivity(new Intent(TrainerScreen.this,ProfileScreen.class));
                        finish();
                        break;
                    case R.id.nav_trainees:
                        startActivity(new Intent(TrainerScreen.this,TraineesScreen.class));
                        break;
                    case R.id.nav_trainer:
                        break;
                    case R.id.nav_logout:
                        startActivity(new Intent(TrainerScreen.this,MainActivity.class));
                        break;
                    default:
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
        getData("\"$key\"",startAt,limit);
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
                        getData("\"$key\"",startAt,limit);
                    }

                }
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
    }

    private void getData(String orderBy, String startAt, int limit) {
        //Initialize Retrofit
        System.out.println("us"+startAt);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://trainerguide-14d03.firebaseio.com")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        //Create interface
        PaginationInterface paginationInterface = retrofit.create((PaginationInterface.class));
        //Initialize Call
        Call<String> call = paginationInterface.STRING_CALL(orderBy, startAt,limit);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //Check Condition

                if(response.isSuccessful() && response.body() != null){
                    // When response is successful and not empty
                    //Hide progress bar
                    progressBar.setVisibility(View.GONE);

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
                        parseResult(jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
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

        for(int i=0; i<jsonArray.length()-1; i++){
            try {
                //if(i <= trainersList.size()+limit && i >= trainersList.size() && trainersList.size() <= jsonArray.length()){
                //Initialize JSON object
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //Initialize Trainer Data
                Trainer trainer = new Trainer();
                //SetImage
                trainer.setUserId(jsonObject.getString("userId"));
                trainer.setImage(jsonObject.getString("image"));
                trainer.setExperience(jsonObject.getString("email"));
                //Add Data
                trainersList.add(trainer);
                //}


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            if(jsonArray.length() > 0) {
                JSONObject jsonObject1;
                System.out.println("jsonArray.length()"+jsonArray.length());

                if (jsonArray.length() < limit){
                    System.out.println("inside array");
                    jsonObject1 = jsonArray.getJSONObject(jsonArray.length()-1);
                    Trainer trainer = new Trainer();
                    System.out.println("user"+jsonObject1.getString("userId"));
                    //SetImage
                    trainer.setImage(jsonObject1.getString("image"));
                    //trainer.setDescription(jsonObject.getString("email"));
                    trainer.setExperience(jsonObject1.getString("email"));
                    trainer.setUserId(jsonObject1.getString("userId"));
                    //trainer.setFees(jsonObject.getString(""));

                    //Add Data
                    scroll=false;
                    trainersList.add(trainer);
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
        System.out.println("size"+trainersList.size());
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
            super.onBackPressed();
        }
    }

    @Override
    public void onAddclick(int position) {
        final Trainer trainer = trainersList.get(position);

        System.out.println("***"+trainer.getUserId()+"*****"+position);
        System.out.println("***"+trainersList.size()+"***** size");

        Intent intent = new Intent(TrainerScreen.this,TrainerProfileView.class);
        intent.putExtra("TrainerUserId",trainer.getUserId());
        startActivity(intent);
        finish();
/*
        databaseReferenceAdd.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Trainee trainee = snapshot.getValue(Trainee.class);
                if(trainee.getTrainerId() == "") {
                    if (trainee.isTrainer() == false) {
                        UserMetaData traineeMetadata = new UserMetaData();
                        traineeMetadata.setUserId(trainee.getUserId());
                        traineeMetadata.setBmi(trainee.getBmi());
                        traineeMetadata.setName(trainee.getName());
                        traineeMetadata.setImage(trainee.getImage());
                        trainee.setTrainerId(trainer.getUserId());
                        HashMap<String, Object> trainerId = new HashMap<>();
                        trainerId.put("trainerId", trainer.getUserId());
                        databaseReferenceAdd.updateChildren(trainerId);
                        databaseReference.child(trainer.getUserId() + "/usersList/" + trainee.getUserId()).setValue(trainee);
                    }
                }
                else
                {
                    AlertDialogBox alertDialogBox = new AlertDialogBox();
                    alertDialogBox.show(getSupportFragmentManager(),"Alert");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }

}