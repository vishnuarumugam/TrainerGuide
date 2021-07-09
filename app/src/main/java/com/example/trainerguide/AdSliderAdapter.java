package com.example.trainerguide;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smarteist.autoimageslider.SliderViewAdapter;

public class AdSliderAdapter extends SliderViewAdapter<AdSliderAdapter.ViewHolder> {

    int[] images;

    public AdSliderAdapter(int[] images){
        this.images = images;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ad_image_item,parent,false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(AdSliderAdapter.ViewHolder viewHolder, int position) {

        viewHolder.imageView.setImageResource(images[position]);
    }

    @Override
    public int getCount() {
        return images.length;
    }

    public class ViewHolder extends SliderViewAdapter.ViewHolder{
        ImageView imageView;

        public ViewHolder(View itemView){
            super(itemView);

            imageView = itemView.findViewById(R.id.ad_image_item);
        }
    }



}
