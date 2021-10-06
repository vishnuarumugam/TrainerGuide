package com.example.trainerguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.example.trainerguide.models.Ad;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdListScreen extends AppCompatActivity implements AdAdapter.OnClickAdListener {

    private RecyclerView adRecycler;
    private AdAdapter adAdapter;
    private List<Ad> adList  = new ArrayList<>();
    private ProgressBar adProgressBar;
    private TextView noAdText;

    private SharedPreferences sp;
    private String isAdmin;
    private String userId;
    private String userEmail;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_list_screen);

        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userId = sp.getString("userId", null);
        isAdmin = sp.getString("isAdmin",null);
        if (getIntent().hasExtra("userEmail")){
            userEmail=getIntent().getExtras().getString("userEmail", "");
        }
        if (isAdmin.equals("1")){
            populateAds();
        }
        else {
            populateUserAds();
        }
        
        adProgressBar = findViewById(R.id.adProgressBar);
        adProgressBar.setVisibility(View.VISIBLE);
        noAdText = findViewById(R.id.noAdText);
        noAdText.setVisibility(View.GONE);
        adRecycler = findViewById(R.id.adRecycler);
        adRecycler.setLayoutManager(new LinearLayoutManager(this));
        adAdapter = new AdAdapter(adList,AdListScreen.this);
        adAdapter.setOnClickAdListener(this);
        adRecycler.setAdapter(adAdapter);

        //tool bar variables
        toolbar = findViewById(R.id.back_tool_bar);
        toolbar.setTitle("Ads Screen");
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdListScreen.this, ProfileScreen.class));
            }
        });

        
    }

    private void populateUserAds() {

        Query databaseReference = FirebaseDatabase.getInstance().getReference("Ad")
                .orderByChild("emailAddress")
                .equalTo(userEmail);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Ad ad = dataSnapshot.getValue(Ad.class);
                    adList.add(ad);
                }
                adAdapter.notifyDataSetChanged();
                adProgressBar.setVisibility(View.GONE);
                noAdText.setVisibility(View.VISIBLE);
                if (adList.isEmpty()){
                    noAdText.setText("No Ads");
                }
                else {
                    noAdText.setVisibility(View.VISIBLE);
                    noAdText.setText("Ad list");
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    private void populateAds() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Ad");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Ad ad = dataSnapshot.getValue(Ad.class);
                    adList.add(ad);
                }
                adAdapter.notifyDataSetChanged();
                adProgressBar.setVisibility(View.GONE);
                noAdText.setVisibility(View.VISIBLE);
                if (adList.isEmpty()){
                    noAdText.setText("No Ads");
                }
                else {
                    noAdText.setVisibility(View.VISIBLE);
                    noAdText.setText("Ad list");
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AdListScreen.this, ProfileScreen.class));

    }


    @Override
    public void onClickAd(int position) {
        Ad ad = adList.get(position);

        Intent intent = new Intent(AdListScreen.this, AdViewScreen.class);
        intent.putExtra("ad",ad);
        intent.putExtra("userEmail",userEmail);
        intent.putExtra("isAdmin",isAdmin);
        intent.putExtra("Screen","AdListScreen");
        startActivity(intent);
    }
}