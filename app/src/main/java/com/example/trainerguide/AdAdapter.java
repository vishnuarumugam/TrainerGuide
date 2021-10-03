package com.example.trainerguide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainerguide.models.Ad;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.ViewHolder> {
    private List<Ad> adList = new ArrayList<>();
    private Context context;

    public AdAdapter(List<Ad> adList, Context context) {
        this.adList = adList;
        this.context = context;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_item,parent,false);
        AdAdapter.ViewHolder holder = new AdAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull  AdAdapter.ViewHolder holder, int position) {
        //holder.adEmail.setText(adList.get(position).getEmailAddress());
        holder.adAmount.setText(adList.get(position).getAmount().toString());

        Date expiryDate = adList.get(position).getExpiryDate();
        SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
        //holder.adExpiry.setText(expiryDate.toString());


        holder.adExpiry.setText(formatDate.format(expiryDate));
        Picasso.get().load(adList.get(position).getImage())
                .fit()
                .centerCrop()
                .into(holder.adImage);

        holder.adImageShimmer.stopShimmer();
    }

    @Override
    public int getItemCount() {
        return adList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView adEmail, adExpiry, adAmount;
        ImageView adImage;
        ShimmerFrameLayout adImageShimmer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //adEmail = itemView.findViewById(R.id.adEmail);
            adExpiry = itemView.findViewById(R.id.adExpiry);
            adAmount = itemView.findViewById(R.id.adAmount);
            adImage = itemView.findViewById(R.id.adImage);
            adImageShimmer = itemView.findViewById(R.id.ad_image_shimmer);
            adImageShimmer.startShimmer();
        }
    }
}
