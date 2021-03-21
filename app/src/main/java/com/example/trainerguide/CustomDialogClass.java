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

        public Activity c;
        public Dialog d;
        public TextView text;
        public Button yes, no;
        public String message;

        public CustomDialogClass(Activity a, String message) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
            this.message = message;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_dialog);
            //yes = (Button) findViewById(R.id.btn_yes);
            no = (Button) findViewById(R.id.btn_no);
            text = findViewById(R.id.txt_dia);
            text.setText(message);
            yes.setOnClickListener(this);
            no.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_no:
                    dismiss();
                    break;
                default:
                    break;
            }
            dismiss();
        }
}
