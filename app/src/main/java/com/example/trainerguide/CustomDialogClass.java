package com.example.trainerguide;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialogClass extends Dialog implements
        android.view.View.OnClickListener{

        public Activity activity;
        public Dialog dialog;
        public TextView alertDialogTitle, alertDialogMsg;
        public Button alertDialogBtnLeft, alertDialogBtnRight;
        public String alertTitle, alertMessage, alertType;


        public CustomDialogClass(Activity activity,String alertTitle, String alertMessage, String alertType) {
            super(activity);
            // TODO Auto-generated constructor stub
            this.activity = activity;
            this.alertTitle = alertTitle;
            this.alertMessage = alertMessage;
            this.alertType = alertType;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_dialog);
            alertDialogBtnLeft = (Button) findViewById(R.id.alertDialogBtnLeft);
            alertDialogBtnRight = (Button) findViewById(R.id.alertDialogBtnRight);
            alertDialogTitle = findViewById(R.id.alertDialogTitle);
            alertDialogMsg = findViewById(R.id.alertDialogMsg);
            alertDialogBtnLeft.setOnClickListener(this);
            alertDialogBtnRight.setOnClickListener(this);
            alertDialogBtnLeft.setVisibility(View.GONE);
            alertDialogBtnRight.setVisibility(View.GONE);

            alertDialogTitle.setText(alertTitle);
            alertDialogMsg.setText(alertMessage);

            switch (alertType){
                case "Normal":
                    alertDialogBtnLeft.setVisibility(View.GONE);
                    alertDialogBtnRight.setText("OK");
                    alertDialogBtnRight.setVisibility(View.VISIBLE);
                    break;
                case "Cancel":
                    alertDialogBtnLeft.setVisibility(View.GONE);
                    alertDialogBtnRight.setText("Cancel");
                    alertDialogBtnRight.setVisibility(View.VISIBLE);
                    break;
                case "Action":
                    alertDialogBtnRight.setText("No");
                    alertDialogBtnLeft.setText("Yes");
                    alertDialogBtnLeft.setVisibility(View.VISIBLE);
                    alertDialogBtnRight.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.alertDialogBtnRight:
                    dismiss();
                    break;
                case R.id.alertDialogBtnLeft:
                    dismiss();
                    break;
                default:
                    break;
            }
            //dismiss();
        }
}
