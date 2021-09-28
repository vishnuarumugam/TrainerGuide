package com.example.trainerguide;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PdfCreation extends AppCompatActivity {

    private PDFView pdfView;
    private Toolbar toolbar;
    private Animation buttonBounce;
    private ImageButton pdfShare;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_creation);

        pdfView = findViewById(R.id.pdfView);
        pdfShare = findViewById(R.id.pdfShare);

        // loading Animation from
        buttonBounce= AnimationUtils.loadAnimation(this, R.anim.button_bounce);

        //tool bar variables
        toolbar = findViewById(R.id.back_tool_bar);
        toolbar.setTitle("Diet Plan");

        setSupportActionBar(toolbar);

        File filePath = new File(getIntent().getExtras().getString("filePath"));
        pdfView.fromFile(filePath).load();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        builder.detectFileUriExposure();


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PdfCreation.this, PrepareFoodChart.class);
                intent.putExtra("userId", getIntent().getExtras().getString("userId"));
                intent.putExtra("userName",  getIntent().getExtras().getString("userName"));
                intent.putExtra("totalCalories", getIntent().getExtras().getDouble("totalCalories"));
                startActivity(intent);
                finish();
            }
        });

        pdfShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdfShare.startAnimation(buttonBounce);
                Uri uri = Uri.fromFile(new File(getIntent().getExtras().getString("filePath").replaceAll("file///","")));

                Intent share = new Intent();
                share.setAction(Intent.ACTION_SEND);
                share.setType("application/pdf");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(share);
            }
        });


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