package com.example.trainerguide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.trainerguide.models.Ad;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AdViewScreen extends AppCompatActivity {

    private LinearLayout adUserView;
    private ImageView adImage;
    private TextView adType, adEmail, adUrl, adAmount, adCreated, adPosted, adExpired;

    private Ad receivedAd;
    private String userEmail;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_view_screen);

        toolbar = findViewById(R.id.back_tool_bar);
        toolbar.setTitle("Ad details");

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdViewScreen.this, HomeScreen.class));
            }
        });


        adUserView  = findViewById(R.id.adUserView);
        adImage     = findViewById(R.id.adImage);

        //adType      = findViewById(R.id.adType);
        adEmail     = findViewById(R.id.adEmail);
        adUrl       = findViewById(R.id.adUrl);
        adAmount    = findViewById(R.id.adAmount);
        adCreated   = findViewById(R.id.adCreated);
        adPosted    = findViewById(R.id.adPosted);
        adExpired   = findViewById(R.id.adExpired);

        if (getIntent().hasExtra("ad")){
            receivedAd = (Ad) getIntent().getExtras().getSerializable("ad");
        }
        System.out.println(receivedAd.getAmount()+"amountad");
        if (getIntent().hasExtra("userEmail")){
            userEmail = getIntent().getExtras().getString("userEmail");
        }

        System.out.println(userEmail + receivedAd.getEmailAddress());
        if (!userEmail.equals(receivedAd.getEmailAddress())){
            adUserView.setVisibility(View.GONE);
        }
        populateAdDetails(receivedAd);



    }

    private void populateAdDetails(Ad receivedAd) {
        SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");

        Picasso.get().load(receivedAd.getImage())
                .fit()
                .centerCrop()
                .into(adImage);
        adEmail.setText(receivedAd.getEmailAddress());

        if (userEmail.equals(receivedAd.getEmailAddress())){
            adAmount.setText(receivedAd.getAmount().toString());
            adCreated.setText(formatDate.format(receivedAd.getCreateDate()));
            adPosted.setText(formatDate.format(receivedAd.getPostedDate()));
            adExpired.setText(formatDate.format(receivedAd.getExpiryDate()));

            if (receivedAd.getRedirectTo().equals("Web Page")){
                adUrl.setText(receivedAd.getUrl());
            }
        }

    }
}