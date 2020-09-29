package com.example.trainerguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.trainerguide.validation.UserInputValidation;
import com.google.android.material.textfield.TextInputLayout;

public class ForgotPasswordForm extends AppCompatActivity {

    private EditText txtemail, txtOtp, txtPwd, txtCnfrmPwd;
    private TextInputLayout layout_Otp, layout_pwd,layout_CnfrmPwd;
    private UserInputValidation userInputValidation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_form);

        txtemail = findViewById(R.id.userFgpEmail_Input);
        txtOtp = findViewById(R.id.userFgpOtp_Input);
        txtCnfrmPwd = findViewById(R.id.fgpCnfrmPassword_Input);
        txtPwd = findViewById(R.id.userFgpPassword_Input);
        final Button btnFgp = findViewById(R.id.btnFgp);

        layout_CnfrmPwd = findViewById(R.id.fgpLay_txtCnfrmPwd);
        layout_Otp = findViewById(R.id.fgpLay_txtOtp);
        layout_pwd = findViewById(R.id.fgpLay_txtLayPwd);

        btnFgp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(btnFgp.getText().toString().equals("Generate OTP")) {
                    String emailValid = userInputValidation.emailValidation(txtemail.getText().toString());
                    if (emailValid.equals("Valid")){
                        Toast.makeText(ForgotPasswordForm.this, btnFgp.getText().toString(), Toast.LENGTH_SHORT).show();
                        layout_Otp.setVisibility(View.VISIBLE);
                        layout_pwd.setVisibility(View.GONE);
                        layout_CnfrmPwd.setVisibility(View.GONE);
                        btnFgp.setText("Verify OTP");
                    }
                    else {
                        txtemail.setError(emailValid);
                    }
                }

                else if (btnFgp.getText().toString().equals("Verify OTP")) {

                    String otpValid = userInputValidation.otpNumberValidation(txtOtp.getText().toString());

                    if (otpValid.equals("Valid")){
                        layout_pwd.setVisibility(View.VISIBLE);
                        layout_CnfrmPwd.setVisibility(View.VISIBLE);
                        layout_Otp.setVisibility(View.GONE);
                        btnFgp.setText("Change Password");
                    }
                    else {
                        txtOtp.setError(otpValid);
                    }
                }

                else if (btnFgp.getText().toString().equals("Change Password")) {

                    if ( ChangePasswordValidation() ){

                        if (txtPwd.getText().toString().equals(txtCnfrmPwd.getText().toString())){
                            startActivity(new Intent(ForgotPasswordForm.this,MainActivity.class));

                        }
                        else {
                            layout_CnfrmPwd.setPasswordVisibilityToggleEnabled(false);
                            txtCnfrmPwd.setError("Confirm Password doesn't match with Password");
                        }

                    }
                }
            }
        });
    }

    private boolean ChangePasswordValidation() {
        String passwordValid = userInputValidation.passwordValidation(txtPwd.getText().toString(),"ChangePassword");
        if (!passwordValid.equals("Valid")){
                txtPwd.setError(passwordValid);
                return false;
        }
        if (txtCnfrmPwd.getText().toString().isEmpty()){
            layout_CnfrmPwd.setPasswordVisibilityToggleEnabled(false);
            txtCnfrmPwd.setError("Password cannot be empty");
            return false;
        }
        return true;
    }

}