package com.example.shaktisetu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WorkerProfileActivity extends AppCompatActivity {

    TextView nameText, ratingText, locationText, experienceText, jobsText, verifyText;
    Button bookBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_profile);

        // Initialize views
        nameText = findViewById(R.id.nameText);
        ratingText = findViewById(R.id.ratingText);
        locationText = findViewById(R.id.locationText);
        experienceText = findViewById(R.id.experienceText);
        jobsText = findViewById(R.id.jobsText);
        verifyText = findViewById(R.id.verifyText);
        bookBtn = findViewById(R.id.bookBtn);

        String worker = getIntent().getStringExtra("worker");

        // ✅ SET DATA BASED ON WORKER
        if (worker.contains("Anita")) {
            setData("Anita", "⭐4.5", "Udupi", "3 Years", "25 Jobs");
        } else if (worker.contains("Meena")) {
            setData("Meena", "⭐4.2", "Manipal", "2 Years", "18 Jobs");
        } else if (worker.contains("Riya")) {
            setData("Riya", "⭐4.3", "Udupi", "4 Years", "30 Jobs");
        } else if (worker.contains("Sneha")) {
            setData("Sneha", "⭐4.1", "Kundapura", "2 Years", "15 Jobs");
        } else {
            setData("Worker", "⭐4.0", "Nearby", "2 Years", "10 Jobs");
        }

        verifyText.setText("Aadhar Verified ✅");

        // ✅ BOOK BUTTON
        bookBtn.setOnClickListener(v -> {
            Intent intent = new Intent(WorkerProfileActivity.this, BookingActivity.class);
            intent.putExtra("worker", worker);
            startActivity(intent);
        });
    }

    // 🔹 Helper method (clean code)
    private void setData(String name, String rating, String location, String exp, String jobs) {
        nameText.setText("Name: " + name);
        ratingText.setText("Rating: " + rating);
        locationText.setText("Location: " + location);
        experienceText.setText("Experience: " + exp);
        jobsText.setText("Jobs Done: " + jobs);
    }
}