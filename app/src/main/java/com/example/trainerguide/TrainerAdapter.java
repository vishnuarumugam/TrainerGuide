package com.example.trainerguide;

import android.content.Context;
import android.content.SyncAdapterType;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainerguide.models.Trainer;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TrainerAdapter extends  RecyclerView.Adapter<TrainerAdapter.ViewHolder>{
    private List<Trainer> trainers = new ArrayList<>();
    private Context context;
    private OnAddClickListener addlistener;

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        int[] colours = new int[]{Color.parseColor("#F6CEE3"), Color.parseColor("#A9E2F3"), Color.parseColor("#D8F6CE"), Color.parseColor("#E4E3E3")};
        holder.trainerConsItem.setBackgroundColor(colours[position%4]);

        holder.name.setText(trainers.get(position).getName());
        System.out.println("****   *****"+trainers.get(position).getName());
        if(trainers.get(position).getExperience() != null){
            holder.experience.setText(trainers.get(position).getExperience().toString() + " Year(s)");}

        if(trainers.get(position).getSubscriptionFees() != null){
            holder.fees.setText(trainers.get(position).getSubscriptionFees().toString());}

        Picasso.get().load(trainers.get(position).getImage())
                .fit()
                .placeholder(R.drawable.ic_share)
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
        TextView name, experience, fees;
        ImageView profileImage;
        MaterialCardView trainerProfileClick;
        RelativeLayout trainerItem;
        ConstraintLayout trainerConsItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.trainerItemName);
            experience = itemView.findViewById(R.id.trainerItemExp);
            fees = itemView.findViewById(R.id.trainerItemFee);
            profileImage = itemView.findViewById(R.id.trainerProfileImage);
            trainerProfileClick = itemView.findViewById(R.id.parent);
            trainerItem = itemView.findViewById(R.id.trainerItem);
            trainerConsItem = itemView.findViewById(R.id.trainerConsItem);
        }
    }

    public interface OnAddClickListener{
        void onAddclick(int position);
    }

    public void setOnAddClickListener(OnAddClickListener listener){
        addlistener = listener;
    }
}
