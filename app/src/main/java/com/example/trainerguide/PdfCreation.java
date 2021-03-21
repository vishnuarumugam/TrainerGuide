package com.example.trainerguide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.itextpdf.kernel.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static android.os.Environment.getDownloadCacheDirectory;

public class PdfCreation extends AppCompatActivity {

    private Button generatePdfBtn;
    private EditText pdfInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_creation);

        generatePdfBtn = findViewById(R.id.generatePdfBtn);
        pdfInput = findViewById(R.id.pdfInput);

        ActivityCompat.requestPermissions(PdfCreation.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        generatePdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PdfDocument pdfDocument = new PdfDocument();

                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300,800,1).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);

                Paint paint = new Paint();

                String pdfString = pdfInput.getText().toString();

                int x= 10, y = 25;

                page.getCanvas().drawText(pdfString, x, y, paint);

                pdfDocument.finishPage(page);

                String filePath = Environment.getExternalStorageDirectory().toString() + "/userFoodChart.pdf";

                File file = new File(filePath);

                try{
                    pdfDocument.writeTo(new FileOutputStream(file));
                }
                catch (Exception e){
                    e.printStackTrace();
                    pdfInput.setText("Error");
                }

                pdfDocument.close();
            }
        });
    }

    private void createPdf() throws FileNotFoundException{
        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

        File file = new File(pdfPath, "userFoodChart.pdf");

        OutputStream outputStream = new FileOutputStream(file);

        //PdfWriter writer = new PdfWriter(file);

        PdfDocument pdfDocument = new PdfDocument();
    }

    private void generatePDF(View view){

        PdfDocument pdfDocument = new PdfDocument();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300,800,1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Paint paint = new Paint();

        String pdfString = pdfInput.getText().toString();

        int x= 10, y = 25;

        page.getCanvas().drawText(pdfString, x, y, paint);

        pdfDocument.finishPage(page);

        String filePath = Environment.getExternalStorageDirectory().getPath() + "userFoodChart.pdf";

        File file = new File(filePath);

        try{
            pdfDocument.writeTo(new FileOutputStream(file));
        }
        catch (Exception e){
            e.printStackTrace();
            pdfInput.setText("Error");
        }

        pdfDocument.close();
    }
}