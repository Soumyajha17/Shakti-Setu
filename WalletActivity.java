package com.example.shaktisetu;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WalletActivity extends AppCompatActivity {

    TextView tvBalance, tvHold;
    Button btnRefresh, btnWithdraw;
    LinearLayout withdrawSection;
    DBHelper db;
    String userEmail, role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        tvBalance  = findViewById(R.id.tvBalance);
        tvHold     = findViewById(R.id.tvHold);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnWithdraw = findViewById(R.id.btnWithdraw);
        withdrawSection = findViewById(R.id.withdrawSection);

        db = new DBHelper(this);

        // 🔥 IMPORTANT: Checking "session" shared prefs for role
        userEmail = getSharedPreferences("session", MODE_PRIVATE).getString("email", "");
        role = getSharedPreferences("session", MODE_PRIVATE).getString("role", "Worker");

        // Case-insensitive check because some parts of your app use "Worker" and some use "worker"
        if (role.equalsIgnoreCase("Client")) {
            withdrawSection.setVisibility(View.GONE);
        } else {
            withdrawSection.setVisibility(View.VISIBLE);
        }

        loadWallet();

        btnRefresh.setOnClickListener(v -> loadWallet());

        btnWithdraw.setOnClickListener(v -> {
            withdrawMoney();
        });
    }

    private void loadWallet() {
        SQLiteDatabase sqlDb = db.getReadableDatabase();
        Cursor c = sqlDb.rawQuery("SELECT balance, hold_balance FROM wallet WHERE user_email=?", new String[]{userEmail});

        if (c != null && c.moveToFirst()) {
            double balance = c.getDouble(0);
            double hold = c.getDouble(1);

            tvBalance.setText("₹" + String.format("%.2f", balance));
            tvHold.setText("₹" + String.format("%.2f", hold));

            if (balance <= 0) {
                btnWithdraw.setEnabled(false);
                btnWithdraw.setAlpha(0.5f);
            } else {
                btnWithdraw.setEnabled(true);
                btnWithdraw.setAlpha(1.0f);
            }
        }
        if (c != null) c.close();
    }

    private void withdrawMoney() {
        SQLiteDatabase sqlDb = db.getWritableDatabase();
        Cursor c = sqlDb.rawQuery("SELECT balance FROM wallet WHERE user_email=?", new String[]{userEmail});

        if (c != null && c.moveToFirst()) {
            double balance = c.getDouble(0);
            if (balance > 0) {
                ContentValues cv = new ContentValues();
                cv.put("balance", 0);
                sqlDb.update("wallet", cv, "user_email=?", new String[]{userEmail});

                Toast.makeText(this, "Withdrawal of ₹" + String.format("%.2f", balance) + " successful!", Toast.LENGTH_LONG).show();
                loadWallet();
            } else {
                Toast.makeText(this, "No available balance to withdraw.", Toast.LENGTH_SHORT).show();
            }
        }
        if (c != null) c.close();
    }
}
