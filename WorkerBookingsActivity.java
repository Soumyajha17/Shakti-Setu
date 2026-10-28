package com.example.shaktisetu;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class WorkerBookingsActivity extends AppCompatActivity {

    LinearLayout container;
    DBHelper db;
    String workerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(0xFFF3E5F5);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(root);

        // Header
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setBackgroundColor(0xFF6A1B9A);
        header.setPadding(dp(24), dp(24), dp(24), dp(24));

        TextView title = new TextView(this);
        title.setText("My Bookings");
        title.setTextSize(22);
        title.setTextColor(0xFFFFFFFF);
        header.addView(title);

        root.addView(header);

        container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(dp(16), dp(16), dp(16), dp(16));
        root.addView(container);

        setContentView(scroll);

        db = new DBHelper(this);
        workerEmail = getSharedPreferences("session", MODE_PRIVATE)
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
        Cursor c = db.getWorkerBookingsDetailed(workerEmail);

        if (!c.moveToFirst()) {
            c.close();
            addEmptyState("No bookings yet.");
            return;
        }

        do {
            long bookingId = c.getLong(c.getColumnIndexOrThrow("id"));
            String service = c.getString(c.getColumnIndexOrThrow("service"));
            String date = c.getString(c.getColumnIndexOrThrow("date"));
            String time = c.getString(c.getColumnIndexOrThrow("time"));
            String status = c.getString(c.getColumnIndexOrThrow("status"));
            String amount = c.getString(c.getColumnIndexOrThrow("amount"));
            String clientName = c.getString(c.getColumnIndexOrThrow("client_name"));
            String clientEmail = c.getString(c.getColumnIndexOrThrow("client_email"));

            addBookingCard(bookingId, service, clientName, clientEmail, date, time, amount, status);

        } while (c.moveToNext());

        c.close();
    }

    private void addBookingCard(long bookingId, String service, String clientName,
                                String clientEmail, String date, String time,
                                String amount, String status) {

        CardView card = new CardView(this);
        card.setRadius(dp(12));
        card.setCardElevation(dp(4));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(12);
        card.setLayoutParams(lp);

        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setPadding(dp(16), dp(16), dp(16), dp(16));
        card.addView(inner);

        // Service Title
        TextView tvService = new TextView(this);
        tvService.setText(service);
        tvService.setTextSize(16);
        tvService.setTextColor(0xFF000000);
        inner.addView(tvService);

        addDetail(inner, "Client", clientName);
        addDetail(inner, "Date", date);
        addDetail(inner, "Time", time);
        addDetail(inner, "Amount", "₹" + amount);
        addDetail(inner, "Status", status);

        // 🔥 APPLY BUTTON (ONLY IF PENDING)
        if (status.equalsIgnoreCase("Pending")) {

            Button btnApply = new Button(this);
            btnApply.setText("Apply");

            btnApply.setOnClickListener(v -> {

                // Send notification to client
                db.insertNotification(clientEmail,
                        "Worker Applied",
                        "A worker applied for your job");

                Toast.makeText(this,
                        "Applied successfully!",
                        Toast.LENGTH_SHORT).show();
            });

            inner.addView(btnApply);
        }

        container.addView(card);
    }

    private void addDetail(LinearLayout parent, String label, String value) {
        TextView tv = new TextView(this);
        tv.setText(label + ": " + value);
        tv.setTextSize(13);
        parent.addView(tv);
    }

    private void addEmptyState(String msg) {
        TextView tv = new TextView(this);
        tv.setText(msg);
        tv.setPadding(40, 40, 40, 40);
        container.addView(tv);
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }
}