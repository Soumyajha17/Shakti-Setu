package com.example.shaktisetu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TrainingDetailActivity extends AppCompatActivity {

    TextView title, duration, startDate, seats;
    Button watchBtn, completeBtn, enrollBtn;
    SharedPreferences prefs;
    int currentSeats;
    String courseTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_detail);

        title       = findViewById(R.id.titleText);
        duration    = findViewById(R.id.durationText);
        startDate   = findViewById(R.id.dateText);
        seats       = findViewById(R.id.seatsText);
        watchBtn    = findViewById(R.id.watchBtn);
        completeBtn = findViewById(R.id.completeBtn);
        enrollBtn   = findViewById(R.id.enrollBtn);

        // Per-user prefs (training progress + enrollment)
        String userEmail = getSharedPreferences("session", MODE_PRIVATE).getString("email", "default");
        prefs = getSharedPreferences("training_" + userEmail, MODE_PRIVATE);

        courseTitle = getIntent().getStringExtra("title");
        String videoUrl      = getIntent().getStringExtra("video");
        int originalSeats    = Integer.parseInt(getIntent().getStringExtra("seats"));

        // Restore saved remaining seats; first visit uses the original count from intent
        currentSeats = prefs.getInt(courseTitle + "_seats", originalSeats);

        title.setText(courseTitle);
        duration.setText("Duration: " + getIntent().getStringExtra("duration"));
        startDate.setText("Start Date: " + getIntent().getStringExtra("date"));
        seats.setText("Vacant Seats: " + currentSeats);

        // Restore enroll state
        boolean isEnrolled = prefs.getBoolean(courseTitle + "_enrolled", false);
        if (isEnrolled) {
            enrollBtn.setText("Already Enrolled");
            enrollBtn.setEnabled(false);
        }

        enrollBtn.setOnClickListener(v -> {
            if (currentSeats > 0) {
                currentSeats--;
                // Persist both the updated seat count and enrollment flag
                prefs.edit()
                        .putInt(courseTitle + "_seats", currentSeats)
                        .putBoolean(courseTitle + "_enrolled", true)
                        .apply();
                seats.setText("Vacant Seats: " + currentSeats);
                enrollBtn.setText("Already Enrolled");
                enrollBtn.setEnabled(false);
            }
        });

        // Restore completed state
        boolean isCompleted = prefs.getBoolean(courseTitle + "_completed", false);
        if (isCompleted) {
            completeBtn.setText("Completed");
            completeBtn.setEnabled(false);
        }

        completeBtn.setOnClickListener(v -> {
            prefs.edit().putBoolean(courseTitle + "_completed", true).apply();
            completeBtn.setText("Completed");
            completeBtn.setEnabled(false);
            Intent intent = new Intent(this, CertificateActivity.class);
            intent.putExtra("course", courseTitle);
            startActivity(intent);
        });

        watchBtn.setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))));
    }
}
