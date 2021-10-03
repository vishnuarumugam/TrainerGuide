package com.example.trainerguide;

import android.content.Context;
import android.content.SyncAdapterType;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainerguide.models.Trainer;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TrainerAdapter extends  RecyclerView.Adapter<TrainerAdapter.ViewHolder>{
    private List<Trainer> trainers = new ArrayList<>();
    private Context context;
    private OnAddClickListener addlistener;

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        int[] colours = new int[]{Color.parseColor("#F6CEE3"), Color.parseColor("#A9E2F3"), Color.parseColor("#D8F6CE"), Color.parseColor("#E4E3E3")};
        //holder.trainerConsItem.setBackgroundColor(colours[position%4]);

        holder.name.setText(trainers.get(position).getName());
        //System.out.println("****   *****"+trainers.get(position).getName());
        if(trainers.get(position).getExperience() != null){
            holder.experience.setText(trainers.get(position).getExperience().toString() + " Year(s)");}

        if(trainers.get(position).getSubscriptionFees() != null){
            holder.fees.setText(trainers.get(position).getSubscriptionFees().toString());}

        /*if(trainers.get(position).getRating()){
            holder.ratingBar.setRating((float)trainers.get(position).getRating());
            holder.ratingUserCount.setText("(" + String.valueOf((int) trainers.get(position).getRatedTraineescount()) + ")");
        }
        else
        {
            //ratingBar.setRating(5);
            holder.ratingUserCount.setText("-");
        }*/
        System.out.println(trainers.get(position).getName() + " " +trainers.get(position).getRating() + "rating" + trainers.get(position).getRatedTraineescount());
        holder.ratingBar.setRating((float)trainers.get(position).getRating());
        holder.ratingUserCount.setText("(" + String.valueOf((int) trainers.get(position).getRatedTraineescount()) + ")");

        holder.trainerImageShimmer.stopShimmer();
        Picasso.get().load(trainers.get(position).getImage())
                .fit()
                .centerCrop()
                .into(holder.profileImage);

        holder.trainerProfileClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trainers.get(position);
                System.out.println(position);
                addlistener.onAddclick(position);
            }
        });
        //holder.trainerImageShimmer.setVisibility(View.GONE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trainer_item,parent,false);
        TrainerAdapter.ViewHolder holder = new TrainerAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public int getItemCount() {
        return trainers.size();
    }

    public void filterList(ArrayList<Trainer> filteredList){
        trainers = filteredList;
        notifyDataSetChanged();
    }

    public TrainerAdapter(List<Trainer> trainers, Context context) {
        this.trainers = trainers;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, experience, fees, ratingUserCount;
        ImageView profileImage;
        MaterialCardView trainerProfileClick;
        RelativeLayout trainerItem;
        ConstraintLayout trainerConsItem;
        RatingBar ratingBar;
        ShimmerFrameLayout trainerImageShimmer;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.trainerItemName);
            experience = itemView.findViewById(R.id.trainerItemExp);
            fees = itemView.findViewById(R.id.trainerItemFee);
            profileImage = itemView.findViewById(R.id.trainerProfileImage);
            trainerProfileClick = itemView.findViewById(R.id.parent);
            trainerItem = itemView.findViewById(R.id.trainerItem);
            trainerConsItem = itemView.findViewById(R.id.trainerConsItem);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            ratingUserCount = itemView.findViewById(R.id.txtUserCount);
            trainerImageShimmer = itemView.findViewById(R.id.trainer_image_shimmer);
            ratingBar.setEnabled(false);
            ratingBar.setNumStars(5);
            trainerImageShimmer.startShimmer();
        }
    }

    public interface OnAddClickListener{
        void onAddclick(int position);
    }

    public void setOnAddClickListener(OnAddClickListener listener){
        addlistener = listener;
    }
}
