package com.example.trainerguide;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainerguide.models.Notification;
import com.example.trainerguide.models.UserMetaData;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>  {
    private List<Notification> notification = new ArrayList<>();
    private Context context;
    private NotificationAdapter.OnAddClickListener addlistener;
    private NotificationAdapter.OnApproveClickListener approvelistener;
    private NotificationAdapter.OnRejectClickListener rejectlistener;

    public NotificationAdapter(List<Notification> notification, Context context) {
        this.notification = notification;
        this.context = context;
    }
    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item,parent,false);
        NotificationAdapter.ViewHolder holder = new NotificationAdapter.ViewHolder(view);
        return holder;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, final int position) {
        System.out.println("items notification");
        holder.notification.setText(notification.get(position).getNotification());
        if(notification.get(position).getNotificationType().equals("Request"))
        {
            holder.rejectBtn.setVisibility(View.VISIBLE);
            holder.approvebtn.setVisibility(View.VISIBLE);
        }
        holder.notificationClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notification.get(position);
                System.out.println(position);
                addlistener.onAddclick(position);
            }
        });

        holder.approvebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notification.get(position);
                System.out.println(position);
                approvelistener.onApproveclick(position);
            }
        });
        holder.rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notification.get(position);
                System.out.println(position);
                rejectlistener.onRejectclick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notification.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView notification;
        Button notificationClick;
        Button approvebtn, rejectBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            notification = itemView.findViewById(R.id.notificationText);
            notificationClick = itemView.findViewById(R.id.infobtn);
            approvebtn = itemView.findViewById(R.id.approvebtn);
            rejectBtn = itemView.findViewById(R.id.rejectbtn);
        }
    }

    public interface OnAddClickListener{
        void onAddclick(int position);
    }
    public interface OnApproveClickListener{
        void onApproveclick(int position);
    }
    public interface OnRejectClickListener{
        void onRejectclick(int position);
    }

    public void setOnAddClickListener(NotificationAdapter.OnAddClickListener listener){
        addlistener = listener;
    }
    public void setOnApproveClickListener(NotificationAdapter.OnApproveClickListener listener){
        approvelistener = listener;
    }
    public void setOnRejectClickListener(NotificationAdapter.OnRejectClickListener listener){
        rejectlistener = listener;
    }
}
