package com.example.shaktisetu;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CertificateActivity extends AppCompatActivity {

    TextView certText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);

        certText = findViewById(R.id.certText);

        String course = getIntent().getStringExtra("course");

        certText.setText("🎓 Certificate of Completion\n\nThis certifies that you have successfully completed:\n\n"
                + course);
    }
}