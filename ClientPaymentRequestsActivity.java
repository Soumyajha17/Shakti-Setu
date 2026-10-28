package com.example.shaktisetu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ClientPaymentRequestsActivity extends AppCompatActivity {

    ListView listView;
    DBHelper db;
    String clientEmail;
    ArrayList<long[]> requestMeta;
    ArrayList<String> workerEmails;
    ArrayList<String> workerNames;
    ArrayList<String> amounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_payment_requests);

        db = new DBHelper(this);
        listView = findViewById(R.id.requestsList);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            // Guard: placeholder row when list is empty - do nothing
            if (requestMeta == null || position >= requestMeta.size()) return;

            long requestId    = requestMeta.get(position)[0];
            String workerEmail = workerEmails.get(position);
            String workerName  = workerNames.get(position);
            String amount      = amounts.get(position);

            Intent i = new Intent(this, PaymentOptionActivity.class);
            i.putExtra("requestId",   (int) requestId);
            i.putExtra("workerEmail", workerEmail);
            i.putExtra("worker",      workerName);
            i.putExtra("clientEmail", clientEmail);
            i.putExtra("amount",      amount);
            startActivity(i);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Re-read email every resume so it's fresh after login
        clientEmail = getSharedPreferences("session", MODE_PRIVATE).getString("email", "");
        loadRequests();
    }

    private void loadRequests() {
        requestMeta  = new ArrayList<>();
        workerEmails = new ArrayList<>();
        workerNames  = new ArrayList<>();
        amounts      = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        Cursor c = db.getPendingRequestsForClient(clientEmail);
        while (c.moveToNext()) {
            long rId      = c.getLong(c.getColumnIndexOrThrow("id"));
            long bId      = c.getLong(c.getColumnIndexOrThrow("booking_id"));
            String wEmail = c.getString(c.getColumnIndexOrThrow("worker_email"));
            String wName  = c.getString(c.getColumnIndexOrThrow("worker_name"));
            String amt    = c.getString(c.getColumnIndexOrThrow("amount"));

            requestMeta.add(new long[]{rId, bId});
            workerEmails.add(wEmail);
            workerNames.add(wName != null ? wName : wEmail);
            amounts.add(amt);
            labels.add("💳 " + (wName != null ? wName : wEmail)
                    + " requests Rs." + amt + "\nTap to Pay");
        }
        c.close();

        if (labels.isEmpty()) {
            labels.add("No pending payment requests.");
        }

        listView.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, labels));
    }
}
