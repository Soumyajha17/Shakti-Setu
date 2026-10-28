package com.example.shaktisetu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PaymentHistoryActivity extends AppCompatActivity {

    LinearLayout container;
    DBHelper db;
    String email, role;
    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        // TOOLBAR for Menu support
        Toolbar toolbar = new Toolbar(this);
        toolbar.setBackgroundColor(0xFF6A1B9A);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar.setTitle("Payment History");
        setSupportActionBar(toolbar);
        mainLayout.addView(toolbar);

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(0xFFF3E5F5);
        mainLayout.addView(scroll);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(0, 0, 0, 32);
        scroll.addView(root);

        SharedPreferences session = getSharedPreferences("session", MODE_PRIVATE);
        email = session.getString("email", "");
        role  = session.getString("role", "Worker");

        // Header
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setBackgroundColor(0xFF6A1B9A);
        int hp = dp(24);
        header.setPadding(hp, hp, hp, hp);
        TextView title = new TextView(this);
        title.setText("Payment History");
        title.setTextSize(22);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setTextColor(0xFFFFFFFF);
        header.addView(title);
        TextView sub = new TextView(this);
        sub.setText("Worker".equals(role) ? "Payments received from clients" : "Workers you have paid");
        sub.setTextSize(13);
        sub.setTextColor(0xFFDEDEDE);
        sub.setPadding(0, dp(4), 0, 0);
        header.addView(sub);
        root.addView(header);

        container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int mp = dp(16);
        container.setPadding(mp, mp, mp, mp);
        root.addView(container);

        setContentView(mainLayout);

        db = new DBHelper(this);

        if ("Worker".equals(role)) {
            loadWorkerHistory();
            addRatingsButton(root);
        } else {
            loadClientHistory();
        }
    }

    private void loadWorkerHistory() {
        Cursor c = db.getPaymentsForWorker(email);
        if (c == null) {
            addEmpty("Database error.");
            return;
        }
        if (!c.moveToFirst()) {
            c.close();
            addEmpty("No payments received yet.");
            return;
        }
        do {
            String amt        = c.getString(c.getColumnIndexOrThrow("amount"));
            String method     = c.getString(c.getColumnIndexOrThrow("method"));
            String fromClient = c.getString(c.getColumnIndexOrThrow("client_email"));

            addCard(
                "Received ₹" + amt,
                "From: " + fromClient,
                "Via: " + (method != null ? method : "-"),
                "",
                "",
                0xFF1B5E20
            );
        } while (c.moveToNext());
        c.close();
    }

    private void loadClientHistory() {
        Cursor c = db.getClientPaymentHistoryDetailed(email);
        if (c == null) {
            addEmpty("Database error.");
            return;
        }
        if (!c.moveToFirst()) {
            c.close();
            addEmpty("No payments made yet.\nBook a worker to get started.");
            return;
        }
        do {
            String amt        = c.getString(c.getColumnIndexOrThrow("amount"));
            String method     = c.getString(c.getColumnIndexOrThrow("method"));
            String workerEmail = c.getString(c.getColumnIndexOrThrow("worker_email"));

            addCard(
                "Paid ₹" + amt,
                "To: " + workerEmail,
                "Via: " + (method != null ? method : "-"),
                "",
                "",
                0xFF0D47A1
            );
        } while (c.moveToNext());
        c.close();
    }

    private void addCard(String heading, String line1, String line2,
                         String date, String ratingLine, int accentColor) {
        CardView card = new CardView(this);
        android.view.ViewGroup.MarginLayoutParams lp =
                new android.view.ViewGroup.MarginLayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(12);
        card.setLayoutParams(lp);
        card.setRadius(dp(14));
        card.setCardElevation(dp(4));
        card.setCardBackgroundColor(0xFFFFFFFF);

        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setPadding(dp(18), dp(16), dp(18), dp(16));
        card.addView(inner);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        android.view.View bar = new android.view.View(this);
        LinearLayout.LayoutParams barLp = new LinearLayout.LayoutParams(dp(4), LinearLayout.LayoutParams.MATCH_PARENT);
        bar.setLayoutParams(barLp);
        bar.setBackgroundColor(accentColor);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(14), 0, 0, 0);
        LinearLayout.LayoutParams contentLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        content.setLayoutParams(contentLp);

        addLine(content, heading, 17, android.graphics.Typeface.BOLD, 0xFF212121);
        addLine(content, line1,   13, android.graphics.Typeface.NORMAL, 0xFF424242);
        addLine(content, line2,   13, android.graphics.Typeface.NORMAL, 0xFF616161);
        if (date != null && !date.isEmpty()) {
            addLine(content, date,    12, android.graphics.Typeface.NORMAL, 0xFF9E9E9E);
        }
        if (ratingLine != null && !ratingLine.isEmpty()) {
            addLine(content, ratingLine, 14, android.graphics.Typeface.BOLD, 0xFFF57F17);
        }

        row.addView(bar);
        row.addView(content);
        inner.addView(row);
        container.addView(card);
    }

    private void addLine(LinearLayout parent, String text, int sp, int style, int color) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(sp);
        tv.setTypeface(null, style);
        tv.setTextColor(color);
        tv.setPadding(0, dp(2), 0, dp(2));
        parent.addView(tv);
    }

    private void addRatingsButton(LinearLayout root) {
        LinearLayout btnWrapper = new LinearLayout(this);
        btnWrapper.setOrientation(LinearLayout.VERTICAL);
        btnWrapper.setPadding(dp(16), 0, dp(16), dp(24));
        root.addView(btnWrapper);

        Button btn = new Button(this);
        btn.setText("View My Ratings");
        btn.setTextColor(0xFF4A148C);
        btn.setBackgroundColor(0xFFEDE7F6);
        btn.setTextSize(15);
        btn.setPadding(dp(16), dp(12), dp(16), dp(12));
        btn.setOnClickListener(v -> startActivity(new Intent(this, WorkerRatingsActivity.class)));
        btnWrapper.addView(btn);
    }

    private void addEmpty(String msg) {
        TextView tv = new TextView(this);
        tv.setText(msg);
        tv.setTextSize(15);
        tv.setTextColor(0xFF757575);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(dp(24), dp(40), dp(24), dp(40));
        container.addView(tv);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
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
            startActivity(new Intent(this, ChatActivity.class));
        } else if (id == R.id.menu_logout) {
            logoutUser();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        SharedPreferences sp = getSharedPreferences("session", MODE_PRIVATE);
        sp.edit().clear().apply();
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }
}
