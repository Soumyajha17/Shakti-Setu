package com.example.shaktisetu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class PaymentOptionActivity extends AppCompatActivity {

    CardView upiCard, cashCard;
    TextView workerNameText, amountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_option);

        upiCard        = (CardView) findViewById(R.id.upiBtn);
        cashCard       = (CardView) findViewById(R.id.cashBtn);
        workerNameText = findViewById(R.id.workerNameText);
        amountText     = findViewById(R.id.amountText);

        int    requestId   = getIntent().getIntExtra("requestId", -1);
        String workerEmail = getIntent().getStringExtra("workerEmail");
        String clientEmail = getIntent().getStringExtra("clientEmail");
        String amount      = getIntent().getStringExtra("amount");
        // Legacy fallback (called from BookingActivity)
        String worker      = getIntent().getStringExtra("worker");

        if (amount == null || amount.isEmpty()) amount = "500";
        String displayWorker = (workerEmail != null) ? workerEmail : (worker != null ? worker : "Worker");

        if (workerNameText != null)
            workerNameText.setText("Worker: " + displayWorker);
        if (amountText != null)
            amountText.setText("Amount: ₹" + amount);

        final int    fRequestId   = requestId;
        final String fWorkerEmail = workerEmail != null ? workerEmail : "";
        final String fClientEmail = clientEmail != null ? clientEmail : "";
        final String fAmount      = amount;
        final String fWorker      = displayWorker;

        upiCard.setOnClickListener(v -> {
            Intent i = new Intent(this, UPIActivity.class);
            i.putExtra("requestId",   fRequestId);
            i.putExtra("workerEmail", fWorkerEmail);
            i.putExtra("clientEmail", fClientEmail);
            i.putExtra("amount",      fAmount);
            i.putExtra("method",      "UPI");
            i.putExtra("worker",      fWorker);
            startActivity(i);
        });

        cashCard.setOnClickListener(v -> {
            Intent i = new Intent(this, PaymentActivity.class);
            i.putExtra("requestId",   fRequestId);
            i.putExtra("workerEmail", fWorkerEmail);
            i.putExtra("clientEmail", fClientEmail);
            i.putExtra("amount",      fAmount);
            i.putExtra("method",      "CASH");
            i.putExtra("worker",      fWorker);
            startActivity(i);
        });
    }
}
