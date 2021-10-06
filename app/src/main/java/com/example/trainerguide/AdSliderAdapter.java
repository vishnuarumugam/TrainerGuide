package com.example.trainerguide;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.trainerguide.models.Ad;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdSliderAdapter extends SliderViewAdapter<AdSliderAdapter.ViewHolder> {

    //int[] images;
    private List<Ad> adList = new ArrayList<>();
    private OnClickAdListener adListener;
    private Context context;

    /*public AdSliderAdapter(int[] images){
        this.images = images;
    }
*/
    public AdSliderAdapter(Context context, List<Ad> adList) {
         this.context = context;
         this.adList = adList;
    }

    /*public AdSliderAdapter(List<Ad> adList) {
        this.adList = adList;
    }*/


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ad_image_item,parent,false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(AdSliderAdapter.ViewHolder viewHolder, int position) {

        //viewHolder.imageView.setImageResource(images[position]);
        /*Picasso.get().load(adList.indexOf(position))
                .fit()
                .centerCrop()
                .into(viewHolder.imageView);*/
        Picasso.get().load(adList.get(position).getImage())
                .fit()
                .centerCrop()
                .into(viewHolder.imageView);

        viewHolder.sliderAdItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adList.get(position);
                adListener.onClickAd(position);
            }
        });
        viewHolder.adImageShimmer.stopShimmer();
    }

    @Override
    public int getCount() {
        //return images.length;
        return adList.size();
    }

    public class ViewHolder extends SliderViewAdapter.ViewHolder{
        ImageView imageView;
        ConstraintLayout sliderAdItem;
        ShimmerFrameLayout adImageShimmer;

        public ViewHolder(View itemView){
            super(itemView);

            imageView = itemView.findViewById(R.id.ad_image_item);
            sliderAdItem = itemView.findViewById(R.id.sliderAdItem);
            adImageShimmer = itemView.findViewById(R.id.ad_image_item_shimmer);

            adImageShimmer.startShimmer();
        }
    }

    public interface OnClickAdListener{

        void onClickAd(int position);
    }
    public void setOnClickAdListener(AdSliderAdapter.OnClickAdListener listener){
        adListener = listener;
    }




}
