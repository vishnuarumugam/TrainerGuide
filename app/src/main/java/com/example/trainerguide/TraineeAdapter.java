package com.example.trainerguide;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainerguide.models.User;
import com.example.trainerguide.models.UserMetaData;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.time.temporal.ChronoUnit;


public class TraineeAdapter extends RecyclerView.Adapter<TraineeAdapter.ViewHolder> {

    private List<UserMetaData> trainee = new ArrayList<>();
    private Context context;
    private Animation buttonBounce;
    private OnAddClickListener addlistener;
    private OnViewReportListener viewReportListener;
    private NotificationAdapter.OnDeleteClickListener deletelistener;

    /*public ArrayList<User> getTrainee() {
        return trainee;
    }

    public void setTrainee(ArrayList<User> trainee){
        this.trainee = trainee;
        notifyDataSetChanged();
    }*/

    public TraineeAdapter(Context context, List<UserMetaData> trainee, Animation animation){
        this.context=context;
        this.trainee=trainee;
        this.buttonBounce=animation;
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
    public void onBindViewHolder(@NonNull TraineeAdapter.ViewHolder holder, final int position) {
        //int[] colours = new int[]{Color.parseColor(String.valueOf(R.color.themeColourOne)), Color.parseColor("#A9E2F3"), Color.parseColor("#D8F6CE"), Color.parseColor("#E4E3E3")};

        int[] colours = new int[]{R.color.themeColourOne, R.color.themeColourTwo, R.color.themeColourFour};
        //holder.traineeItemThemeLine.setBackgroundColor(colours[position%3]);
        holder.name.setText(trainee.get(position).getName());
        holder.bmi.setText(trainee.get(position).getBmi().toString());
        Picasso.get().load(trainee.get(position).getImage())
                .placeholder(R.drawable.profile)
                .fit()
                .centerCrop()
                .into(holder.profilePic);
        holder.traineeProfileClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trainee.get(position);
                addlistener.onAddclick(position);
            }
        });

        holder.traineeReportView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.findViewById(R.id.traineeReportView).startAnimation(buttonBounce);
                trainee.get(position);
                viewReportListener.onViewReport(position);

            }
        });
        holder.deleteTrainee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.findViewById(R.id.deleteTrainee).startAnimation(buttonBounce);
                trainee.get(position);
                deletelistener.onDeleteclick(position);
            }
        });
        System.out.println("trainee.get(position).getSubscriptionEndDate()"+ trainee.get(position).getSubscriptionEndDate());
        String subscriptionStatus = findSubscriptionStatus(trainee.get(position).getSubscriptionEndDate());

        //int[] subscriptionStatusColours = new int[]{Color.parseColor(String.valueOf(R.color.subscriptionGreen)),Color.parseColor(String.valueOf(R.color.subscriptionRed)), Color.parseColor(String.valueOf(R.color.subscriptionOrange))};
        int[] subscriptionStatusColours = new int[]{Color.parseColor("#F44336"), Color.parseColor("#FFBF00"), Color.parseColor("#1D3C45")};
        holder.traineeSubscriptionStatus.setText(subscriptionStatus);
        if (subscriptionStatus.equals("Active")){
            holder.traineeSubscriptionStatus.setTextColor(subscriptionStatusColours[2]);
            //holder.traineeSubscriptionStatus.setTextColor(R.color.themeColourFour);
        }

        else if(subscriptionStatus.equals("Inactive")){
            holder.traineeSubscriptionStatus.setTextColor(subscriptionStatusColours[0]);
        }

        else{
            holder.traineeSubscriptionStatus.setTextColor(subscriptionStatusColours[1]);
        }



        /*holder.traineeSubscriptionStatus.setBackgroundColor();
        holder.traineeSubscriptionStatus.setText();
*/
    }

    @Override
    public int getItemCount() {
        return trainee.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,bmi,traineeReportView, traineeSubscriptionStatus;
        ImageView profilePic;
        MaterialCardView traineeProfileClick;
        RelativeLayout traineeItem;
        CardView traineeItemThemeLine;
        ImageButton deleteTrainee;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.traineeItemName);
            bmi = itemView.findViewById(R.id.traineeItemBmi);
            profilePic = itemView.findViewById(R.id.traineeProfileImage);
            traineeProfileClick = itemView.findViewById(R.id.trainee_item_parent);
            traineeItem = itemView.findViewById(R.id.traineeItem);
            traineeItemThemeLine = itemView.findViewById(R.id.traineeItemThemeLine);
            traineeReportView = itemView.findViewById(R.id.traineeReportView);
            deleteTrainee = itemView.findViewById(R.id.deleteTrainee);
            traineeSubscriptionStatus = itemView.findViewById(R.id.traineeSubscriptionStatus);

        }

    }

    public interface OnAddClickListener{
        void onAddclick(int position);
    }

    public void setOnAddClickListener(TraineeAdapter.OnAddClickListener listener){
        addlistener = listener;
    }

    public interface OnViewReportListener{
        void onViewReport(int position);
    }

    public void setOnViewReportListener(TraineeAdapter.OnViewReportListener listener){
        viewReportListener = listener;
    }

    public interface OnDeleteClickListener{
        void onDeleteclick(int position);
    }

    public void setOnDeleteClickListener(NotificationAdapter.OnDeleteClickListener listener){
        deletelistener = listener;
    }
    public String findSubscriptionStatus(Date subscriptionEndDate){
        System.out.println(subscriptionEndDate);
        if (subscriptionEndDate !=null){
            System.out.println(subscriptionEndDate);
            Long noOfDays = ChronoUnit.DAYS.between(Calendar.getInstance().toInstant(), subscriptionEndDate.toInstant());

            if (noOfDays>=0 && noOfDays<=7){
                System.out.println(noOfDays);
                if (noOfDays == 0){
                    return "Expires Today";
                }
                else{
                    return "Expires in "+ noOfDays + " days";
                }

            }
            else if(noOfDays>7){
                return "Active";
            }
            else
                return "Inactive";
        }
        return "NA";

    }

}
