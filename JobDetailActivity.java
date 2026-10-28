package com.example.shaktisetu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class JobDetailActivity extends AppCompatActivity {

    TextView title, hours, location, payment, salaryText, paymentStatusText;
    Button applyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        title             = findViewById(R.id.titleText);
        hours             = findViewById(R.id.hoursText);
        location          = findViewById(R.id.locationText);
        payment           = findViewById(R.id.paymentText);
        applyBtn          = findViewById(R.id.applyBtn);
        salaryText        = findViewById(R.id.salaryText);
        paymentStatusText = findViewById(R.id.paymentStatusText);

        String jobTitle    = getIntent().getStringExtra("title");
        String jobHours    = getIntent().getStringExtra("hours");
        String jobLocation = getIntent().getStringExtra("location");
        String jobPayment  = getIntent().getStringExtra("payment");

        title.setText(jobTitle);
        hours.setText("Hours: " + jobHours);
        location.setText("Location: " + jobLocation);
        payment.setText("Payment: " + jobPayment);
        salaryText.setText("Salary: Rs.500/day");
        paymentStatusText.setText("Payment Status: Check in history");

        // Namespace prefs by logged-in user email so each account has its own applied jobs
        String userEmail = getSharedPreferences("session", MODE_PRIVATE).getString("email", "default");
        SharedPreferences prefs = getSharedPreferences("jobs_" + userEmail, MODE_PRIVATE);

        int count = prefs.getInt("appliedCount", 0);
        boolean alreadyApplied = false;
        for (int i = 0; i < count; i++) {
            if (jobTitle != null && jobTitle.equals(prefs.getString("job" + i, ""))) {
                alreadyApplied = true;
                break;
            }
        }

        if (alreadyApplied) {
            applyBtn.setText("Already Applied");
            applyBtn.setEnabled(false);
        }

        applyBtn.setOnClickListener(v -> {
            int newCount = prefs.getInt("appliedCount", 0);
            prefs.edit()
                    .putString("job" + newCount, jobTitle)
                    .putInt("appliedCount", newCount + 1)
                    .apply();
            Toast.makeText(this, "Applied Successfully!", Toast.LENGTH_SHORT).show();
            applyBtn.setText("Applied");
            applyBtn.setEnabled(false);
        });
    }
}
