package com.example.shaktisetu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class CategoryActivity extends AppCompatActivity {

    CardView tailorBtn, cookBtn, cleanerBtn, electricianBtn, tutorBtn, driverBtn;
    String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        role = getIntent().getStringExtra("role");
        if (role == null) {
            role = getSharedPreferences("session", MODE_PRIVATE).getString("role", "Worker");
        }

        TextView title = findViewById(R.id.titleText);
        title.setText("Client".equals(role) ? "Select Worker Category" : "Select Job Category");

        tailorBtn      = findViewById(R.id.tailorBtn);
        cookBtn        = findViewById(R.id.cookBtn);
        cleanerBtn     = findViewById(R.id.cleanerBtn);
        electricianBtn = findViewById(R.id.electricianBtn);
        tutorBtn       = findViewById(R.id.tutorBtn);
        driverBtn      = findViewById(R.id.driverBtn);

        tailorBtn.setOnClickListener(v      -> openCategory("TAILOR"));
        cookBtn.setOnClickListener(v        -> openCategory("COOK"));
        cleanerBtn.setOnClickListener(v     -> openCategory("CLEANER"));
        electricianBtn.setOnClickListener(v -> openCategory("ELECTRICIAN"));
        tutorBtn.setOnClickListener(v       -> openCategory("TUTOR"));
        driverBtn.setOnClickListener(v      -> openCategory("DRIVER"));
    }

    private void openCategory(String category) {
        Intent intent;
        if ("Client".equals(role)) {
            intent = new Intent(this, ProviderActivity.class);
        } else {
            intent = new Intent(this, WorkerJobsActivity.class);
        }
        intent.putExtra("category", category);
        intent.putExtra("role", role);
        startActivity(intent);
    }
}
