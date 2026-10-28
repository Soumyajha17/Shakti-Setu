package com.example.shaktisetu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class UPIActivity extends AppCompatActivity {

    EditText upiId;
    Button payBtn;
    TextView workerText, amountText;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upi);

        db = new DBHelper(this);

        upiId      = findViewById(R.id.upiId);
        payBtn     = findViewById(R.id.upiPayBtn);
        workerText = findViewById(R.id.workerText);
        amountText = findViewById(R.id.amountText);

        int    requestId   = getIntent().getIntExtra("requestId", -1);
        String workerEmail = getIntent().getStringExtra("workerEmail");
        String amount      = getIntent().getStringExtra("amount");
        String worker      = getIntent().getStringExtra("worker");

        String clientEmail = getSharedPreferences("session", MODE_PRIVATE)
                .getString("email", getIntent().getStringExtra("clientEmail"));

        if (amount == null || amount.isEmpty()) amount = "500";
        String displayWorker = (worker != null) ? worker : (workerEmail != null ? workerEmail : "Worker");

        if (workerText != null) workerText.setText("Paying: " + displayWorker);
        if (amountText != null) amountText.setText("Amount: Rs." + amount);

        final int    fReqId   = requestId;
        final String fWEmail  = workerEmail != null ? workerEmail.toLowerCase().trim() : "";
        final String fCEmail  = clientEmail != null ? clientEmail.toLowerCase().trim() : "";
        final String fAmount  = amount;
        final String fWorker  = displayWorker;

        payBtn.setOnClickListener(v -> {
            String id = upiId.getText().toString().trim();
            if (id.isEmpty()) { upiId.setError("Enter UPI ID"); return; }

            if (fReqId != -1) {
                db.clientPaysRequest(fReqId, fCEmail, fWEmail, fAmount, "UPI");
            } else {
                // Direct pay (no prior payment request) - still record worker side
                db.insertPaymentFull(fCEmail, fWEmail, fAmount, "UPI");
            }

            Toast.makeText(this, "UPI Payment of Rs." + fAmount + " sent!", Toast.LENGTH_LONG).show();

            Intent i = new Intent(this, RatingActivity.class);
            i.putExtra("worker",      fWorker);
            i.putExtra("workerEmail", fWEmail);
            startActivity(i);
            finish();
        });
    }
}
