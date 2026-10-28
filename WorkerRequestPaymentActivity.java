package com.example.shaktisetu;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class WorkerRequestPaymentActivity extends AppCompatActivity {

    Spinner bookingSpinner;
    EditText amountEdit;
    Button requestBtn, backBtn;
    DBHelper db;
    String workerEmail;
    long[] bookingIds;
    String[] clientEmails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_request_payment);

        db          = new DBHelper(this);
        workerEmail = getSharedPreferences("session", MODE_PRIVATE).getString("email", "");

        bookingSpinner = findViewById(R.id.bookingSpinner);
        amountEdit     = findViewById(R.id.amountEdit);
        requestBtn     = findViewById(R.id.requestPaymentBtn);
        backBtn        = findViewById(R.id.backBtn);

        loadBookings();

        requestBtn.setOnClickListener(v -> {
            String amount = amountEdit.getText().toString().trim();
            if (amount.isEmpty()) { amountEdit.setError("Enter amount"); return; }
            if (bookingIds == null || bookingIds.length == 0) {
                Toast.makeText(this, "No bookings found", Toast.LENGTH_SHORT).show();
                return;
            }

            int pos = bookingSpinner.getSelectedItemPosition();
            long bookingId   = bookingIds[pos];
            String clientEmail = clientEmails[pos];

            long reqId = db.workerRequestPayment(workerEmail, clientEmail, amount, bookingId);
            if (reqId != -1) {
                Toast.makeText(this,
                        "Payment request of ₹" + amount + " sent to client!",
                        Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Failed. Try again.", Toast.LENGTH_SHORT).show();
            }
        });

        backBtn.setOnClickListener(v -> finish());
    }

    private void loadBookings() {
        Cursor c = db.getWorkerBookings(workerEmail);
        int count = c.getCount();
        if (count == 0) {
            bookingSpinner.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item,
                    new String[]{"No bookings yet"}));
            c.close();
            return;
        }

        bookingIds    = new long[count];
        clientEmails  = new String[count];
        String[] labels = new String[count];
        int i = 0;
        while (c.moveToNext()) {
            bookingIds[i]   = c.getLong(c.getColumnIndexOrThrow("id"));
            clientEmails[i] = c.getString(c.getColumnIndexOrThrow("client_email"));
            labels[i] = c.getString(c.getColumnIndexOrThrow("service"))
                    + " — " + clientEmails[i];
            i++;
        }
        c.close();

        bookingSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, labels));
    }
}
