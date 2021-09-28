package com.example.trainerguide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trainerguide.models.Ad;
import com.example.trainerguide.models.Trainee;
import com.example.trainerguide.validation.UserInputValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class AdPostingScreen extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private Toolbar toolbar;
    private Spinner redirectAd;
    private ImageView postAdImage;
    private EditText postAdEmailInput, postAdAmountInput, postAdUrlInput;
    private LinearLayout postAdUrlLay, postAdExpiryLay;
    private Button postAdButton;
    private TextView chooseImage, postAdExpiryInput;
    private ArrayAdapter<CharSequence> redirectAdapter;

    //Dialog screen
    private Dialog calendarDialog;
    private ImageView calendarDialogClose;
    private TextView calendarDialogTitle;
    private DatePicker calendarDialogDatePicker;
    private Button calendarDialogUpdate;

    private String adId;
    private Uri imageUri;
    private String imageString;
    private StorageTask uploadTask;
    private StorageReference storageReference;

    private DatabaseReference databaseReference;
    private Ad adData;
    private String redirectType="Profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_posting_screen);

        //Ad Screen variables
        chooseImage = findViewById(R.id.chooseImage);
        redirectAd = findViewById(R.id.redirectAd);
        postAdImage = findViewById(R.id.postAdImage);
        postAdEmailInput = findViewById(R.id.postAdEmailInput);
        postAdExpiryInput = findViewById(R.id.postAdExpiryInput);
        postAdAmountInput = findViewById(R.id.postAdAmountInput);
        postAdUrlInput = findViewById(R.id.postAdUrlInput);
        postAdUrlLay = findViewById(R.id.postAdUrlLay);
        postAdExpiryLay = findViewById(R.id.postAdExpiryLay);
        postAdUrlLay.setVisibility(View.GONE);
        postAdButton = findViewById(R.id.postAdButton);

        redirectAdapter = ArrayAdapter.createFromResource(this, R.array.ad_redirect, android.R.layout.simple_spinner_item);
        redirectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        redirectAd.setAdapter(redirectAdapter);
        redirectAd.setOnItemSelectedListener(this);

        //Dialog variables
        calendarDialog = new Dialog(this);
        calendarDialog.setContentView(R.layout.calendar_dialog);
        calendarDialogClose = calendarDialog.findViewById(R.id.calendarDialogClose);
        calendarDialogDatePicker = calendarDialog.findViewById(R.id.calendarDialogDatePicker);
        calendarDialogDatePicker.setMinDate(new Date().getDate());
        calendarDialogTitle = calendarDialog.findViewById(R.id.calendarDialogTitle);
        calendarDialogUpdate = calendarDialog.findViewById(R.id.calendarDialogUpdate);

        //tool bar variables
        toolbar = findViewById(R.id.back_tool_bar);
        toolbar.setTitle("Post Ad");

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdPostingScreen.this, HomeScreen.class));
            }
        });

        storageReference = FirebaseStorage.getInstance().getReference();

        adId = getAdId();

        postAdImage.setOnClickListener(this);
        postAdButton.setOnClickListener(this);
        calendarDialogUpdate.setOnClickListener(this);
        calendarDialogClose.setOnClickListener(this);
        postAdExpiryLay.setOnClickListener(this);
        postAdExpiryInput.setOnClickListener(this);
    }

    private void fileChooser() {
        CropImage.startPickImageActivity(AdPostingScreen.this);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK)
        {
            Uri uri = CropImage.getPickImageResultUri(this,data);
            imageUri = uri;
            if(CropImage.isReadExternalStoragePermissionsRequired(this,uri))
            {

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
                StartCrop(imageUri);

            }
            else {
                StartCrop(imageUri);
            }
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK)
            {
                postAdImage.setImageURI(result.getUri());
                imageUri = result.getUri();
                chooseImage.setText("Choose an image");
                chooseImage.setTextColor(getColor(R.color.themeColourOne));
                uploadFile(result.getUri());
            }
        }
    }

    private void StartCrop(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    private String getExtension(Uri uri) {
        ContentResolver CR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(CR.getType(imageUri));
    }

    private void uploadFile(Uri imageUri) {

        if(imageUri!= null)
        {

            final StorageReference fileReference = storageReference.child("FitnessGuide/Ad").child(adId+".null");
            //final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Ad/"+adData.getAdId());
            uploadTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    HashMap<String,Object> imageMetaData = new HashMap<>();
                                    String image = task.getResult().toString();
                                    imageString = image;
                                    imageMetaData.put("image", image);
                                    //databaseReference.updateChildren(imageMetaData);
                                    //Toast.makeText(AdPostingScreen.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AdPostingScreen.this, "Something gone wrong", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
        else{
            Toast.makeText(this, "File not selected", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AdPostingScreen.this, HomeScreen.class));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getItemAtPosition(position).toString()) {

            case "Company":
                postAdUrlLay.setVisibility(View.GONE);
                redirectType="Company";
                break;

            case "Profile":
                postAdUrlLay.setVisibility(View.GONE);
                redirectType="Profile";
                break;

            case "Web Page":
                postAdUrlLay.setVisibility(View.VISIBLE);
                redirectType="Web Page";
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public String getAdId(){
        String id = String.valueOf(new Date().getTime());
        System.out.println("id"+id);
        return id;
    }

    public void updateAd(Ad adData){
        databaseReference = FirebaseDatabase.getInstance().getReference("Ad");
        HashMap hash = new HashMap();
        hash.put(adData.getAdId(),adData);
        databaseReference.updateChildren(hash);

        Toast.makeText(this,"Ad posted successfully", Toast.LENGTH_SHORT).show();
        reset();
    }

    private void checkAdData() {
        adData = new Ad();

        if (validAd()){
            String pattern = "dd-MM-yyyy";
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            Date dateUpdate = null;

            adData.setAdId(adId);
            adData.setCreateDate(new Date());
            adData.setEmailAddress(postAdEmailInput.getText().toString());
            adData.setAmount(new Double(postAdAmountInput.getText().toString()));
            adData.setPostedDate(new Date());
            adData.setIsExpired("0");
            adData.setRedirectTo(redirectType);
            adData.setImage(imageString);
            try {
                dateUpdate = simpleDateFormat.parse(postAdExpiryInput.getText().toString());
                adData.setExpiryDate(dateUpdate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (redirectType.equals("Web Page")){
                adData.setUrl(postAdUrlInput.getText().toString());
            }

            updateAd(adData);
        }
        else {
            System.out.println("out");
        }



    }

    private void reset(){
        adId = getAdId();
        imageUri= Uri.parse("");
        postAdEmailInput.setText("");
        postAdExpiryInput.setText("");
        postAdImage.setImageURI(imageUri);
        postAdAmountInput.setText("");
        postAdUrlInput.setText("");
        redirectAd.setSelection(0);
    }

    private Boolean validAd() {

        UserInputValidation userInputValidation = new UserInputValidation();

        String emailValid = userInputValidation.emailValidation(postAdEmailInput.getText().toString());
        String urlValid = userInputValidation.urlValidation(postAdUrlInput.getText().toString());
        String amountValid = userInputValidation.amountValidation(postAdAmountInput.getText().toString());

        if (imageUri!=null && emailValid.equals("Valid") && !(postAdExpiryInput.getText().toString().isEmpty()) && amountValid.equals("Valid") ){
            System.out.println("before web");
            if (redirectType.equals("Web Page")){
                System.out.println("inside web 1");
                if(urlValid.equals("Valid")){
                    return true;
                }
                else {
                    postAdUrlInput.setError(urlValid);
                    return false;
                }
            }
            return true;

        }
        else {

            if(!(imageUri!=null)){
                chooseImage.setText("Please choose an image");
                chooseImage.setTextColor(getColor(R.color.subscriptionRed));
            }
            if (!emailValid.equals("Valid")) {
                postAdEmailInput.setError(emailValid);
            }
            if(postAdExpiryInput.getText().toString().isEmpty()){
                postAdExpiryInput.setError("Please provide a valid expiry date");
            }
            if(postAdAmountInput.getText().toString().isEmpty()){
                postAdAmountInput.setError(amountValid);
            }
            if (redirectType.equals("Web Page")){
                System.out.println("inside web 2");
                if(postAdUrlInput.getText().toString().isEmpty()){
                    postAdUrlInput.setError(urlValid);
                }
            }
            System.out.println("AFTER web");
            return false;
        }

    }

    @Override
    public void onClick(View option) {

        switch (option.getId()){
            case R.id.calendarDialogClose:
            case R.id.calendarDialogUpdate:
                calendarDialog.dismiss();
                break;
            case R.id.postAdButton:
                checkAdData();
                break;
            case R.id.postAdImage:
                fileChooser();
                break;
            case R.id.postAdExpiryInput:
            case R.id.postAdExpiryLay:
                calendarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                calendarDialog.show();
                calendarDialogDatePicker.setMinDate(new Date().getTime());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    System.out.println("inCal1");
                    calendarDialogDatePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                        @Override
                        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            int  month = monthOfYear + 1;
                            postAdExpiryInput.setText(dayOfMonth +"-" + month +"-"+ year);
                            calendarDialogDatePicker.setClickable(true);

                        }
                    });


                }
                break;

            default:
                break;
        }

    }
}