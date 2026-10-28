package com.example.shaktisetu;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ClientBookingsActivity extends AppCompatActivity {

    LinearLayout container;
    DBHelper db;
    String clientEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_bookings);

        container = findViewById(R.id.container);
        db = new DBHelper(this);

        clientEmail = getSharedPreferences("session", MODE_PRIVATE)
                .getString("email", "");

        loadBookings();
    }

    @Override
    protected void onResume() {
        super.onResume();
        container.removeAllViews();
        loadBookings();
    }

    private void loadBookings() {

        Cursor c = db.getClientBookings(clientEmail);

        if (!c.moveToFirst()) {
            TextView tv = new TextView(this);
            tv.setText("No bookings found");
            tv.setPadding(40, 40, 40, 40);
            container.addView(tv);
            c.close();
            return;
        }

        do {
            long bookingId = c.getLong(c.getColumnIndexOrThrow("id"));
            String workerEmail = c.getString(c.getColumnIndexOrThrow("worker_email"));
            String service = c.getString(c.getColumnIndexOrThrow("service"));
            String date = c.getString(c.getColumnIndexOrThrow("date"));
            String time = c.getString(c.getColumnIndexOrThrow("time"));
            String amount = c.getString(c.getColumnIndexOrThrow("amount"));
            String status = c.getString(c.getColumnIndexOrThrow("status"));

            addCard(bookingId, workerEmail, service, date, time, amount, status);

        } while (c.moveToNext());

        c.close();
    }

    private void addCard(long bookingId, String workerEmail,
                         String service, String date, String time,
                         String amount, String status) {

        CardView card = new CardView(this);
        card.setRadius(12);
        card.setCardElevation(4);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 20);
        card.setLayoutParams(lp);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);
        card.addView(layout);

        TextView tv1 = new TextView(this);
        tv1.setText("Service: " + service);
        layout.addView(tv1);

        TextView tv2 = new TextView(this);
        tv2.setText("Worker: " + workerEmail);
        layout.addView(tv2);

        TextView tv3 = new TextView(this);
        tv3.setText("Date: " + date + "  Time: " + time);
        layout.addView(tv3);

        TextView tv4 = new TextView(this);
        tv4.setText("Amount: ₹" + amount);
        layout.addView(tv4);

        TextView tv5 = new TextView(this);
        tv5.setText("Status: " + status);
        layout.addView(tv5);

        // 🔥 MARK COMPLETED BUTTON
        if (status.equalsIgnoreCase("Payment Hold")) {

            Button btnComplete = new Button(this);
            btnComplete.setText("Mark Completed");

            btnComplete.setOnClickListener(v -> {

                db.markCompleted(bookingId, workerEmail, amount);

                Toast.makeText(this,
                        "Job completed! Payment released.",
                        Toast.LENGTH_LONG).show();

                container.removeAllViews();
                loadBookings();
            });

            layout.addView(btnComplete);
        }

        container.addView(card);
    }
}