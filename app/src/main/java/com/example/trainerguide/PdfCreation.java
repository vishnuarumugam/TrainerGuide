package com.example.trainerguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

//import com.itextpdf.kernel.pdf.PdfWriter;

import com.itextpdf.layout.element.Image;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PdfCreation extends AppCompatActivity {

    private EditText myEditText;

    PieChart pieChart;
    LinearLayout linearLayout;

    int[] data={6,5,8};
    int[] color={Color.RED,Color.BLUE,Color.CYAN,Color.GREEN,Color.MAGENTA, Color.GREEN};
    int numberOfparts = 3;
    float start=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_creation);

        myEditText = findViewById(R.id.editText);

        int[] data={6,0,12};
        int[] color={Color.RED,Color.BLUE,Color.CYAN,Color.GREEN,Color.MAGENTA, Color.GREEN};
        linearLayout=findViewById(R.id.linearLayout);
        //linearLayout.addView(new PieChart(this,3,data,color));

        //ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.actio) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }



    /*public void createMyPDF(View view){

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){

                String [] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission,1000);
            }
            else{
                //savePdf();
            }
        }
        else {
            //savePdf();
        }

    }*/

    /*private void savePdf() {
        Document doc = new Document();
        String mFile = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        String myFilePath = Environment.getExternalStorageDirectory() + "/" + mFile + ".pdf";
        Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

        PdfDocument myPdfDocument = new PdfDocument();
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(300,600,1).create();
        PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);


        String filePath=  (CreateAppPath.getAppPath(PdfCreation.this)+"test"+ mFile +".pdf");

        //Canvas canvas = new Canvas();
        //canvas.drawColor(Color.BLACK);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.RED);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(0);
        p.setStyle(Paint.Style.STROKE);
        float[] scaledValues = scale();


        RectF rectF = new RectF(30,30,100,100);

        p.setColor(Color.BLACK);
        for(int i=0;i<numberOfparts;i++){
            p.setColor(color[i]);
            p.setStyle(Paint.Style.FILL);

            myPage.getCanvas().drawArc(rectF,start,scaledValues[i],true,p);
            start=start+scaledValues[i];
        }

        myPdfDocument.finishPage(myPage);



        File myFile = new File(filePath);
        try {
            myPdfDocument.writeTo(new FileOutputStream(myFile));
            Toast.makeText(this, "File created1", Toast.LENGTH_SHORT).show();

        }
        catch (Exception e){
            e.printStackTrace();
            myEditText.setText("ERROR");
        }
        myPdfDocument.close();



        *//*try{
            PdfWriter pdfWriter = PdfWriter.getInstance(doc,new FileOutputStream(filePath));

            doc.open();
            String mText = myEditText.getText().toString();
            doc.addAuthor("Vishnu");
            doc.add(new Paragraph(mText, smallBold));


            doc.close();
            Toast.makeText(this, "File created", Toast.LENGTH_SHORT).show();

        }
        catch (Exception e){
            Toast.makeText(this, "This :" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Error", e.getMessage());
        }*//*
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){

            case 1000:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //pdf();
                }
                else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                }
        }

    }

    /*public void pdf(){
        PdfDocument myPdfDocument = new PdfDocument();
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(300,600,1).create();
        PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);

        Paint myPaint = new Paint();
        String myString = myEditText.getText().toString();
        int x = 10, y=25;

        for (String line:myString.split("\n")){
            myPage.getCanvas().drawText(line, x, y, myPaint);
            y+=myPaint.descent()-myPaint.ascent();
        }

        myPdfDocument.finishPage(myPage);

        String myFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/myPDFFile.pdf";
        File myFile = new File(myFilePath);
        try {
            myPdfDocument.writeTo(new FileOutputStream(myFile));
        }
        catch (Exception e){
            e.printStackTrace();
            myEditText.setText("ERROR");
        }

        myPdfDocument.close();

    }*/

    /*private float[] scale() {
        float[] scaledValues = new float[this.data.length];
        float total = getTotal(); //Total all values supplied to the chart
        for (int i = 0; i < this.data.length; i++) {
            scaledValues[i] = (this.data[i] / total) * 360; //Scale each value
        }
        return scaledValues;
    }*/

    /*private float getTotal() {
        float total = 0;
        for (float val : this.data)
            total += val;
        return total;
    }*/

}