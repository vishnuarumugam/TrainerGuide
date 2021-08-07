package com.example.trainerguide;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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

import com.github.barteksc.pdfviewer.PDFView;
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

    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_creation);

        pdfView = findViewById(R.id.pdfView);

        System.out.println(getIntent().getExtras().getString("filePath"));
        File filePath = new File(getIntent().getExtras().getString("filePath"));
        pdfView.fromFile(filePath).load();



    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(PdfCreation.this, PrepareFoodChart.class);
         intent.putExtra("userId", getIntent().getExtras().getString("userId"));
        intent.putExtra("userName",  getIntent().getExtras().getString("userName"));
        intent.putExtra("totalCalories", getIntent().getExtras().getDouble("totalCalories"));
        startActivity(intent);
        finish();

    }




}