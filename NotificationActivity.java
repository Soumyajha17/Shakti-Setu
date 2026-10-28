package com.example.shaktisetu;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    ListView listNotif;
    DBHelper db;
    String email, role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        listNotif = findViewById(R.id.listNotif);
        db = new DBHelper(this);

        // Get logged-in user
        email = getSharedPreferences("session", MODE_PRIVATE).getString("email", "");
        role  = getSharedPreferences("session", MODE_PRIVATE).getString("role", "");

        loadNotifications();
    }

    private void loadNotifications() {
        ArrayList<String> notifications = new ArrayList<>();

        // 🔔 1. BOOKING NOTIFICATIONS
        Cursor bookings;

        if ("Worker".equals(role)) {
            bookings = db.getWorkerBookings(email);
        } else {
            bookings = db.getClientBookings(email);
        }

        if (bookings != null && bookings.moveToFirst()) {
            do {
                String service = bookings.getString(bookings.getColumnIndexOrThrow("service"));
                String date    = bookings.getString(bookings.getColumnIndexOrThrow("date"));
                String time    = bookings.getString(bookings.getColumnIndexOrThrow("time"));
                String status  = bookings.getString(bookings.getColumnIndexOrThrow("status"));

                notifications.add(
                        "📅 Booking\n"
                                + "Service: " + service
                                + "\nDate: " + date
                                + "\nTime: " + time
                                + "\nStatus: " + status
                );

            } while (bookings.moveToNext());
        }

        if (bookings != null) bookings.close();

        // 🔔 2. PAYMENT REQUESTS (CLIENT ONLY)
        if ("Client".equals(role)) {

            Cursor payments = db.getPendingRequestsForClient(email);

            if (payments != null && payments.moveToFirst()) {
                do {
                    String worker = payments.getString(payments.getColumnIndexOrThrow("worker_name"));
                    String amount = payments.getString(payments.getColumnIndexOrThrow("amount"));

                    notifications.add(
                            "💰 Payment Request\n"
                                    + "From: " + worker
                                    + "\nAmount: ₹" + amount
                    );

                } while (payments.moveToNext());
            }

            if (payments != null) payments.close();
        }

        // 🔔 3. EMPTY STATE
        if (notifications.isEmpty()) {
            notifications.add("No notifications yet.");
        }

        // 🔔 4. SET ADAPTER
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                notifications
        );

        listNotif.setAdapter(adapter);
    }
}
