package com.example.trainerguide;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainerguide.models.OnboardingItem;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>{

    private List<OnboardingItem> onboardingItems;

    public OnboardingAdapter(List<OnboardingItem> onboardingItems){
        this.onboardingItems = onboardingItems;
    }

    class OnboardingViewHolder extends RecyclerView.ViewHolder{
        private TextView onBoardingTitle, onBoardingSubTitle, onBoardingDescription;
        private ImageView onBoardingImage;

         OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            onBoardingTitle = itemView.findViewById(R.id.onBoardingTitle);
            onBoardingSubTitle = itemView.findViewById(R.id.onBoardingSubTitle);
            onBoardingDescription = itemView.findViewById(R.id.onBoardingDescription);
            onBoardingImage = itemView.findViewById(R.id.onBoardingImage);

        }
        void setOnboardingData(OnboardingItem onboardingItem){
             onBoardingTitle.setText(onboardingItem.getTitle());
             onBoardingSubTitle.setText(onboardingItem.getSubTitle());
             onBoardingDescription.setText(onboardingItem.getDescription());
             onBoardingImage.setImageResource(onboardingItem.getImage());
        }
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OnboardingViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_onboarding_container, parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingAdapter.OnboardingViewHolder holder, int position) {
        holder.setOnboardingData(onboardingItems.get(position));
    }

    @Override
    public int getItemCount() {
        return onboardingItems.size();
    }
}
