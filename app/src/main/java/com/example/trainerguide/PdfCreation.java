package com.example.trainerguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.itextpdf.kernel.pdf.PdfWriter;

import com.google.android.gms.common.internal.service.Common;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.Path;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static android.os.Environment.getDownloadCacheDirectory;

public class PdfCreation extends AppCompatActivity {

    private EditText myEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_creation);

        myEditText = findViewById(R.id.editText);

        //ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);


    }



    public void createMyPDF(View view){

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){

                String [] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission,1000);
            }
            else{
                savePdf();
            }
        }
        else {
            savePdf();
        }

    }

    private void savePdf() {
        Document doc = new Document();
        String mFile = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        String myFilePath = Environment.getExternalStorageDirectory() + "/" + mFile + ".pdf";
        Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);


        String filePath=  (CreateAppPath.getAppPath(PdfCreation.this)+"test"+ mFile +".pdf");


        try{
            PdfWriter.getInstance(doc,new FileOutputStream(filePath));

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
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){

            case 1000:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    savePdf();
                }
                else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                }
        }

    }

    public void pdf(){
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

    }

}