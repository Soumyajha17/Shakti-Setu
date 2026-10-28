package com.example.shaktisetu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class HomeActivity extends AppCompatActivity {

    MaterialCardView jobBtn, trainingBtn, paymentBtn, profileBtn, myAppsBtn, chatBtn, walletStatsCard;
    MaterialButton logoutBtn;
    TextView welcomeName, statsBalance, statsJobs, statsJobsLabel;
    ExtendedFloatingActionButton fabSupport;
    String role, email, name;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = new DBHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        welcomeName = findViewById(R.id.welcomeName);
        statsBalance = findViewById(R.id.statsBalance);
        statsJobs = findViewById(R.id.statsJobs);
        statsJobsLabel = findViewById(R.id.statsJobsLabel);
        walletStatsCard = findViewById(R.id.walletStatsCard);
        
        jobBtn      = findViewById(R.id.jobBtn);
        trainingBtn = findViewById(R.id.trainingBtn);
        paymentBtn  = findViewById(R.id.paymentBtn);
        profileBtn  = findViewById(R.id.profileBtn);
        myAppsBtn   = findViewById(R.id.myAppsBtn);
        chatBtn     = findViewById(R.id.chatBtn);
        logoutBtn   = findViewById(R.id.logoutBtn);
        fabSupport  = findViewById(R.id.fabAction);

        SharedPreferences sp = getSharedPreferences("session", MODE_PRIVATE);
        role  = sp.getString("role", "Worker");
        email = sp.getString("email", "");
        name  = sp.getString("name", "User");

        if (welcomeName != null) welcomeName.setText("Hello, " + name + "!");

        updateDashboardStats();

        // Role-based UI adjustments
        TextView jobLabel = findViewById(R.id.jobLabel);
        if ("Client".equalsIgnoreCase(role)) {
            if (jobLabel != null) jobLabel.setText("Find Workers");
            if (trainingBtn != null) trainingBtn.setVisibility(View.GONE);
            if (walletStatsCard != null) walletStatsCard.setVisibility(View.GONE);
            if (statsJobsLabel != null) statsJobsLabel.setText("Active Requests");
        } else {
            if (jobLabel != null) jobLabel.setText("Find Jobs");
            if (trainingBtn != null) trainingBtn.setVisibility(View.VISIBLE);
            if (walletStatsCard != null) walletStatsCard.setVisibility(View.VISIBLE);
            if (statsJobsLabel != null) statsJobsLabel.setText("Active Jobs");
        }

        // Click Listeners
        if (jobBtn != null) {
            jobBtn.setOnClickListener(v -> {
                Intent i = new Intent(this, CategoryActivity.class);
                i.putExtra("role", role);
                startActivity(i);
            });
        }

        if (trainingBtn != null) {
            trainingBtn.setOnClickListener(v -> startActivity(new Intent(this, TrainingActivity.class)));
        }

        if (paymentBtn != null) {
            paymentBtn.setOnClickListener(v -> {
                if ("Worker".equalsIgnoreCase(role)) {
                    startActivity(new Intent(this, WorkerRequestPaymentActivity.class));
                } else {
                    startActivity(new Intent(this, ClientPaymentRequestsActivity.class));
                }
            });
        }

        if (profileBtn != null) {
            profileBtn.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        }

        if (myAppsBtn != null) {
            myAppsBtn.setOnClickListener(v -> {
                if ("Worker".equalsIgnoreCase(role)) {
                    startActivity(new Intent(this, WorkerBookingsActivity.class));
                } else {
                    startActivity(new Intent(this, MyApplicationsActivity.class));
                }
            });
        }

        if (chatBtn != null) {
            chatBtn.setOnClickListener(v -> startActivity(new Intent(this, UserListActivity.class)));
        }

        if (logoutBtn != null) {
            logoutBtn.setOnClickListener(v -> logoutUser());
        }

        if (fabSupport != null) {
            fabSupport.setOnClickListener(v -> {
                Toast.makeText(this, "Support feature coming soon!", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void updateDashboardStats() {
        try {
            SQLiteDatabase sqlDb = db.getReadableDatabase();
            
            // Update Balance (Only for Workers)
            if (!"Client".equalsIgnoreCase(role)) {
                Cursor cWallet = sqlDb.rawQuery("SELECT balance FROM wallet WHERE user_email=?", new String[]{email});
                if (cWallet != null && cWallet.moveToFirst()) {
                    if (statsBalance != null) statsBalance.setText("₹" + String.format("%.2f", cWallet.getDouble(0)));
                }
                if (cWallet != null) cWallet.close();
            }

            // Update Active Jobs/Requests Count
            String query = "Worker".equalsIgnoreCase(role) ? 
                    "SELECT COUNT(*) FROM bookings WHERE worker_email=? AND status='Payment Hold'" :
                    "SELECT COUNT(*) FROM bookings WHERE client_email=? AND status='Payment Hold'";
            
            Cursor cJobs = sqlDb.rawQuery(query, new String[]{email});
            if (cJobs != null && cJobs.moveToFirst()) {
                if (statsJobs != null) statsJobs.setText(String.valueOf(cJobs.getInt(0)));
            }
            if (cJobs != null) cJobs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDashboardStats();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        
        // Hide wallet menu item for Clients
        MenuItem walletItem = menu.findItem(R.id.menu_wallet);
        if (walletItem != null && "Client".equalsIgnoreCase(role)) {
            walletItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_notifications) {
            startActivity(new Intent(this, NotificationActivity.class));
        } else if (id == R.id.menu_wallet) {
            startActivity(new Intent(this, WalletActivity.class));
        } else if (id == R.id.menu_chat) {
            startActivity(new Intent(this, UserListActivity.class));
        } else if (id == R.id.menu_logout) {
            logoutUser();
        }
        return true;
    }

    private void logoutUser() {
        getSharedPreferences("session", MODE_PRIVATE).edit().clear().apply();
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}
