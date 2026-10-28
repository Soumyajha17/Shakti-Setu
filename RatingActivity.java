package com.example.shaktisetu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RatingActivity extends AppCompatActivity {

    RatingBar ratingBar;
    Button submitRatingBtn;
    TextView workerLabel;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        db             = new DBHelper(this);
        ratingBar      = findViewById(R.id.ratingBar);
        submitRatingBtn= findViewById(R.id.submitRatingBtn);
        workerLabel    = findViewById(R.id.workerRatingLabel);

        String workerEmail = getIntent().getStringExtra("workerEmail");
        String workerName  = getIntent().getStringExtra("worker");
        String clientEmail = getSharedPreferences("session", MODE_PRIVATE).getString("email", "");

        if (workerLabel != null)
            workerLabel.setText("Rate: " + (workerName != null ? workerName : workerEmail));

        final String fWorkerEmail = workerEmail != null ? workerEmail : "";
        final String fClientEmail = clientEmail;

        submitRatingBtn.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            if (rating == 0) { Toast.makeText(this, "Please give a rating", Toast.LENGTH_SHORT).show(); return; }
            db.insertRating(fWorkerEmail, fClientEmail, String.valueOf(rating));
            Toast.makeText(this, "⭐ Rating saved! Thank you.", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
