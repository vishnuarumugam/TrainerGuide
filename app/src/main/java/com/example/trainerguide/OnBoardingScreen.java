package com.example.trainerguide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.trainerguide.models.OnboardingItem;

import java.util.ArrayList;
import java.util.List;

public class OnBoardingScreen extends AppCompatActivity {

    private OnboardingAdapter onboardingAdapter;
    private LinearLayout onBoardingPageIndicator;
    private ViewPager2 onBoardingPage;
    private Button onBoardingNextButton, onBoardingStartButton;
    private TextView onBoardingScreenSkip;
    private Animation buttonBounce, topAnimation, bottomAnimation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding_screen);

        buttonBounce= AnimationUtils.loadAnimation(this, R.anim.button_bounce);
        topAnimation= AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnimation= AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        setupOnBoardingItems();
        onBoardingPage = findViewById(R.id.onBoardingPage);
        onBoardingPage.setAdapter(onboardingAdapter);
        onBoardingPageIndicator = findViewById(R.id.onBoardingPageIndicator);
        onBoardingNextButton = findViewById(R.id.onBoardingNextButton);
        onBoardingStartButton = findViewById(R.id.onBoardingStartButton);
        onBoardingStartButton.setVisibility(View.GONE);
        onBoardingScreenSkip = findViewById(R.id.onBoardingScreenSkip);

        setupOnboardingIndicator();
        setCurrentOnBoardingIndicator(0);
        onBoardingPage.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentOnBoardingIndicator(position);
            }
        });

        onBoardingNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBoardingNextButton.startAnimation(buttonBounce);

                if (onBoardingPage.getCurrentItem()+1 < onboardingAdapter.getItemCount() ){
                    onBoardingPage.setCurrentItem(onBoardingPage.getCurrentItem()+1);
                }else {

                }
                onBoardingNextButton.clearAnimation();
            }
        });
        onBoardingStartButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBoardingStartButton.startAnimation(buttonBounce);
                startActivity(new Intent(OnBoardingScreen.this, SelectProfileType.class));
                finish();
                onBoardingStartButton.clearAnimation();
            }
        });

        onBoardingScreenSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OnBoardingScreen.this, SelectProfileType.class));
                finish();
            }
        });


    }
    private void setupOnBoardingItems(){
        List<OnboardingItem> onboardingItems = new ArrayList<>();

        OnboardingItem onBoardContent = new OnboardingItem();
        onBoardContent.setTitle("Customised");
        onBoardContent.setSubTitle("Diet Plan");
        onBoardContent.setDescription("Get your Diet plan according to your Food preference and Health issues.");
        onBoardContent.setImage(R.mipmap.onboarding_image_one);

        OnboardingItem onBoardContent1 = new OnboardingItem();
        onBoardContent1.setTitle("Fitness");
        onBoardContent1.setSubTitle("Progress");
        onBoardContent1.setDescription("Get your weight progress and daily nutrients intake level.");
        onBoardContent1.setImage(R.mipmap.onboarding_image_two);

        OnboardingItem onBoardContent2 = new OnboardingItem();
        onBoardContent2.setTitle("Become an");
        onBoardContent2.setSubTitle("Inspiration");
        onBoardContent2.setImage(R.drawable.app_logo);

        onboardingItems.add(onBoardContent);
        onboardingItems.add(onBoardContent1);
        onboardingItems.add(onBoardContent2);

        onboardingAdapter = new OnboardingAdapter(onboardingItems);
    }

    private void setupOnboardingIndicator(){
        ImageView[] indicators = new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8,0,8,0);

        for (int i = 0; i<indicators.length; i++){
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.onboarding_nonactive_indicator
            ));
            indicators[i].setLayoutParams(layoutParams);
            onBoardingPageIndicator.addView(indicators[i]);
        }
    }

    private void setCurrentOnBoardingIndicator(int index){
        int childCount = onBoardingPageIndicator.getChildCount();

        for (int i=0; i<childCount; i++){
            ImageView imageView = (ImageView) onBoardingPageIndicator.getChildAt(i);

            if (i == index){
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(),
                        R.drawable.onboarding_active_indicator
                ));
            }
            else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(),
                        R.drawable.onboarding_nonactive_indicator
                ));
            }

        }
        if (index == onboardingAdapter.getItemCount() - 1){

            onBoardingNextButton.setVisibility(View.GONE);
            onBoardingPageIndicator.setVisibility(View.GONE);
            onBoardingScreenSkip.setVisibility(View.GONE);
            onBoardingStartButton.setVisibility(View.VISIBLE);
            onBoardingPage.startAnimation(topAnimation);
            onBoardingStartButton.startAnimation(bottomAnimation);
            onBoardingPage.setUserInputEnabled(false);

        }else {
            onBoardingStartButton.setVisibility(View.GONE);
            onBoardingNextButton.setVisibility(View.VISIBLE);
            onBoardingPageIndicator.setVisibility(View.VISIBLE);
            onBoardingScreenSkip.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(OnBoardingScreen.this, MainActivity.class));
        finish();
    }
}
