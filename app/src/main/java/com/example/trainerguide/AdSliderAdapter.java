package com.example.trainerguide;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.trainerguide.models.Ad;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdSliderAdapter extends SliderViewAdapter<AdSliderAdapter.ViewHolder> {

    //int[] images;
    private List<Ad> adList = new ArrayList<>();

    /*public AdSliderAdapter(int[] images){
        this.images = images;
    }
*/
    public AdSliderAdapter(List<Ad> adList) {
        this.adList = adList;
    }

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
    }

    @Override
    public int getCount() {
        //return images.length;
        return adList.size();
    }

    public class ViewHolder extends SliderViewAdapter.ViewHolder{
        ImageView imageView;

        public ViewHolder(View itemView){
            super(itemView);

            imageView = itemView.findViewById(R.id.ad_image_item);
        }
    }



}
