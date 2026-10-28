package com.example.shaktisetu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    TextView methodText, workerText, amountText, statusText;
    Button confirmBtn, backBtn;

    DBHelper db;

    String workerEmail, workerName, amount, method, clientEmail;
    long bookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        db = new DBHelper(this);

        // UI
        methodText = findViewById(R.id.methodText);
        workerText = findViewById(R.id.payWorkerText);
        amountText = findViewById(R.id.payAmountText);
        statusText = findViewById(R.id.payStatusText);
        confirmBtn = findViewById(R.id.confirmPayBtn);
        backBtn    = findViewById(R.id.backBtn);

        // Get data from intent
        workerEmail = getIntent().getStringExtra("workerEmail");
        workerName  = getIntent().getStringExtra("worker");
        amount      = getIntent().getStringExtra("amount");
        method      = getIntent().getStringExtra("method");
        bookingId   = getIntent().getLongExtra("bookingId", -1);

        // Get logged-in client
        clientEmail = getSharedPreferences("session", MODE_PRIVATE)
                .getString("email", "");

        // Default values
        if (method == null) method = "Cash";
        if (workerName == null) workerName = "Worker";
        if (amount == null) amount = "500";

        // Set UI
        methodText.setText("Payment Method: " + method);
        workerText.setText("Worker: " + workerName);
        amountText.setText("Amount: ₹" + amount);
        statusText.setText("Status: Pending");

        // ✅ CONFIRM PAYMENT BUTTON
        confirmBtn.setOnClickListener(v -> {

            if (bookingId == -1) {
                Toast.makeText(this, "Booking error!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🔥 NEW WALLET SYSTEM
            db.clientPays(clientEmail, workerEmail, amount, bookingId);

            statusText.setText("Status: Payment on HOLD");
            confirmBtn.setEnabled(false);
            confirmBtn.setText("Paid");

            Toast.makeText(this,
                    "Payment successful! Money is on HOLD until job completion.",
                    Toast.LENGTH_LONG).show();

            // Go to rating (after completion ideally, but okay for now)
            Intent i = new Intent(this, RatingActivity.class);
            i.putExtra("worker", workerName);
            i.putExtra("workerEmail", workerEmail);
            startActivity(i);

            finish();
        });

        // Back button
        backBtn.setOnClickListener(v -> finish());
    }
}