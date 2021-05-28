package com.example.trainerguide;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import java.util.List;

public class PieChart extends View {
    float start=0;
    int width;
    List<Integer> data;
    int cx,cy;
    int numberOfparts;//it tells many data or item will be placed in chart
    private int[] color;

    public PieChart(Context context, int numOfItems, List<Integer> data, int[] color) {
        super(context);
        setFocusable(true);
        this.numberOfparts=numOfItems;
        this.data=data;
        this.color=color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawColor(getResources().getColor(R.color.lightGrey));
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.RED);
        p.setStyle(Paint.Style.FILL);
        p.setStrokeWidth(0);
        p.setStyle(Paint.Style.FILL);
        float[] scaledValues = scale();

        //RectF rectF = new RectF(180,65,getWidth()-180,650);
        RectF rectF = new RectF(20,10,getWidth()-50,getWidth()-50);

        //RectF rectF = new RectF(10,10,80,80);

        p.setColor(Color.BLACK);
        for(int i=0;i<numberOfparts;i++){
            p.setColor(color[i]);
            p.setStyle(Paint.Style.FILL);

            canvas.drawArc(rectF,start,scaledValues[i],true,p);
            start=start+scaledValues[i];
        }

        Paint cenPaint=new Paint();
        //int radius=getWidth()/2-100;
        int radius=70;
        int wid = getWidth()/2-100;
        cenPaint.setStyle(Paint.Style.FILL);
        //cenPaint.setColor(getResources().getColor(R.color.lightGrey));
        //cx=cy=400;
        //cx=cy=getWidth()/2;
        //canvas.drawCircle(cx,cy,radius,cenPaint);
        //canvas.drawCircle(500,356,radius,cenPaint);


    }
    private float[] scale() {
        float[] scaledValues = new float[this.data.size()];
        float total = getTotal(); //Total all values supplied to the chart
        for (int i = 0; i < this.data.size(); i++) {
            scaledValues[i] = (this.data.get(i) / total) * 360; //Scale each value
        }
        return scaledValues;
    }

    private float getTotal() {
        float total = 0;
        for (float val : this.data)
            total += val;
        return total;
    }


}