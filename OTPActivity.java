package com.example.shaktisetu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class OTPActivity extends AppCompatActivity {

    EditText otpInput;
    Button verifyBtn;

    String otp = "1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        otpInput = findViewById(R.id.otpInput);
        verifyBtn = findViewById(R.id.verifyBtn);

        Toast.makeText(this, "OTP: 1234", Toast.LENGTH_LONG).show();

        verifyBtn.setOnClickListener(v -> {

            if (otpInput.getText().toString().equals(otp)) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, RoleActivity.class));
            } else {
                otpInput.setError("Invalid OTP");
            }
        });
    }
}