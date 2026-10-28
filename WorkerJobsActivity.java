package com.example.shaktisetu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

public class WorkerJobsActivity extends AppCompatActivity {

    ListView listView;
    DBHelper db;
    String workerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_jobs);

        listView    = findViewById(R.id.jobList);
        db          = new DBHelper(this);
        workerEmail = getSharedPreferences("session", MODE_PRIVATE).getString("email", "");

        String category = getIntent().getStringExtra("category");

        String[] jobs;
        if ("COOK".equals(category)) {
            jobs = new String[]{"Cook Needed - Udupi Rs.500", "Cook Needed - Manipal Rs.600"};
        } else if ("TAILOR".equals(category)) {
            jobs = new String[]{"Tailor Needed - Udupi Rs.400", "Tailor Needed - Kundapura Rs.550"};
        } else if ("CLEANER".equals(category)) {
            jobs = new String[]{"Cleaner Needed - Udupi Rs.300", "Cleaner Needed - Manipal Rs.350"};
        } else if ("ELECTRICIAN".equals(category)) {
            jobs = new String[]{"Electrician Needed - Udupi Rs.700", "Electrician Needed - Manipal Rs.800"};
        } else if ("TUTOR".equals(category)) {
            jobs = new String[]{"Tutor Needed - Maths (Udupi) Rs.600", "Tutor Needed - Science (Manipal) Rs.700"};
        } else {
            jobs = new String[]{"Driver Needed - Udupi Rs.500", "Driver Needed - Manipal Rs.650"};
        }

        listView.setAdapter(new android.widget.ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, jobs));

        listView.setOnItemClickListener((parent, view, position, id) -> {
            int payment = extractPayment(jobs[position]);

            Intent intent = new Intent(this, JobDetailActivity.class);
            intent.putExtra("title",    jobs[position]);
            intent.putExtra("hours",    "Flexible hours");
            intent.putExtra("location", extractLocation(jobs[position]));
            intent.putExtra("payment",  "Rs." + payment + "/day");
            startActivity(intent);
        });
    }

    private int extractPayment(String job) {
        try { return Integer.parseInt(job.split("Rs\\.")[1].trim()); }
        catch (Exception e) { return 500; }
    }

    private String extractLocation(String job) {
        if (job.contains("Udupi"))    return "Udupi";
        if (job.contains("Manipal"))  return "Manipal";
        if (job.contains("Kundapura"))return "Kundapura";
        return "Nearby Area";
    }
}
