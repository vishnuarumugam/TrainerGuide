package com.example.trainerguide;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainerguide.models.User;
import com.example.trainerguide.models.UserMetaData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TraineeAdapter extends RecyclerView.Adapter<TraineeAdapter.ViewHolder> {

    private List<UserMetaData> trainee = new ArrayList<>();
    private Context context;


    /*public ArrayList<User> getTrainee() {
        return trainee;
    }

    public void setTrainee(ArrayList<User> trainee){
        this.trainee = trainee;
        notifyDataSetChanged();
    }*/

    public TraineeAdapter(Context context,List<UserMetaData> trainee){
        this.context=context;
        this.trainee=trainee;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trainee_item,parent,false);

        ViewHolder holder = new ViewHolder(view);


        return holder;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull TraineeAdapter.ViewHolder holder, int position) {
        System.out.println("items Trainee");
        holder.name.setText(trainee.get(position).getName());
        holder.bmi.setText(trainee.get(position).getBmi().toString());
        Picasso.get().load(trainee.get(position).getImage())
                .placeholder(R.drawable.ic_share)
                .fit()
                .centerCrop()
                .into(holder.profilePic);

    }

    @Override
    public int getItemCount() {
        return trainee.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,bmi;
        ImageView profilePic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.traineeItemName);
            bmi = itemView.findViewById(R.id.traineeItemBmi);
            profilePic = itemView.findViewById(R.id.traineeProfileImage);

        }



    }
}
