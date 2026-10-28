package com.example.shaktisetu;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MyApplicationsActivity extends AppCompatActivity {

    ListView listView;
    DBHelper db;
    String clientEmail;
    ArrayList<Booking> bookingsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_applications);

        listView = findViewById(R.id.listView);
        db = new DBHelper(this);
        clientEmail = getSharedPreferences("session", MODE_PRIVATE).getString("email", "");

        loadBookings();
    }

    private void loadBookings() {
        bookingsList = new ArrayList<>();
        Cursor c = db.getClientBookingsDetailed(clientEmail);

        if (c != null && c.moveToFirst()) {
            do {
                Booking b = new Booking();
                b.id = c.getLong(c.getColumnIndexOrThrow("id"));
                b.workerEmail = c.getString(c.getColumnIndexOrThrow("worker_email"));
                b.workerName = c.getString(c.getColumnIndexOrThrow("worker_name"));
                b.service = c.getString(c.getColumnIndexOrThrow("service"));
                b.status = c.getString(c.getColumnIndexOrThrow("status"));
                b.amount = c.getString(c.getColumnIndexOrThrow("amount"));
                bookingsList.add(b);
            } while (c.moveToNext());
            c.close();
        }

        if (bookingsList.isEmpty()) {
            ArrayList<String> empty = new ArrayList<>();
            empty.add("No bookings found.");
            listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, empty));
        } else {
            BookingAdapter adapter = new BookingAdapter();
            listView.setAdapter(adapter);
        }
    }

    class Booking {
        long id;
        String workerEmail, workerName, service, status, amount;
    }

    class BookingAdapter extends BaseAdapter {
        @Override
        public int getCount() { return bookingsList.size(); }
        @Override
        public Object getItem(int position) { return bookingsList.get(position); }
        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_2, parent, false);
            }
            TextView t1 = convertView.findViewById(android.R.id.text1);
            TextView t2 = convertView.findViewById(android.R.id.text2);

            Booking b = bookingsList.get(position);
            t1.setText(b.service + " - " + b.workerName);
            t2.setText("Status: " + b.status + " | Amount: ₹" + b.amount + "\n(Tap to manage)");

            convertView.setOnClickListener(v -> {
                if ("Payment Hold".equalsIgnoreCase(b.status) || "Pending".equalsIgnoreCase(b.status)) {
                    showCompletionDialog(b);
                } else if ("Completed".equalsIgnoreCase(b.status)) {
                    Toast.makeText(MyApplicationsActivity.this, "Job already completed!", Toast.LENGTH_SHORT).show();
                }
            });

            return convertView;
        }
    }

    private void showCompletionDialog(Booking b) {
        new AlertDialog.Builder(this)
                .setTitle("Mark as Completed")
                .setMessage("Has the worker " + b.workerName + " finished the work? \n\nClicking 'Yes' will release the payment to their available balance.")
                .setPositiveButton("Yes, Completed", (dialog, which) -> {
                    db.markCompleted(b.id, b.workerEmail, b.amount);
                    Toast.makeText(this, "Job marked as completed. Payment released!", Toast.LENGTH_LONG).show();
                    loadBookings();
                })
                .setNegativeButton("Not Yet", null)
                .show();
    }
}
