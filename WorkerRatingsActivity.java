package com.example.shaktisetu;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class WorkerRatingsActivity extends AppCompatActivity {

    LinearLayout container;
    DBHelper db;
    String workerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(0xFFF3E5F5);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(0, 0, 0, 32);
        scroll.addView(root);

        // Header
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setBackgroundColor(0xFF6A1B9A);
        int hp = dp(24);
        header.setPadding(hp, hp, hp, hp);

        TextView title = new TextView(this);
        title.setText("My Ratings");
        title.setTextSize(22);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setTextColor(0xFFFFFFFF);
        header.addView(title);

        TextView sub = new TextView(this);
        sub.setText("Feedback from clients you've worked with");
        sub.setTextSize(13);
        sub.setTextColor(0xFFDEDEDE);
        sub.setPadding(0, dp(4), 0, 0);
        header.addView(sub);
        root.addView(header);

        // Average rating banner
        LinearLayout avgBanner = new LinearLayout(this);
        avgBanner.setOrientation(LinearLayout.HORIZONTAL);
        avgBanner.setGravity(android.view.Gravity.CENTER_VERTICAL);
        avgBanner.setBackgroundColor(0xFFEDE7F6);
        avgBanner.setPadding(dp(20), dp(14), dp(20), dp(14));

        TextView avgLabel = new TextView(this);
        avgLabel.setId(android.R.id.text1);
        avgLabel.setText("Average Rating: —");
        avgLabel.setTextSize(15);
        avgLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        avgLabel.setTextColor(0xFF4A148C);
        avgBanner.addView(avgLabel);
        root.addView(avgBanner);

        container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int mp = dp(16);
        container.setPadding(mp, mp, mp, mp);
        root.addView(container);

        setContentView(scroll);

        db = new DBHelper(this);
        workerEmail = getSharedPreferences("session", MODE_PRIVATE).getString("email", "");
        loadRatings(avgLabel);
    }

    private void loadRatings(TextView avgLabel) {
        Cursor c = db.getRatingsForWorker(workerEmail);

        if (!c.moveToFirst()) {
            c.close();
            avgLabel.setText("Average Rating: No ratings yet");
            addEmptyState("No ratings yet.\nClients will rate you after payment.");
            return;
        }

        float total = 0;
        int count = 0;

        do {
            String ratingStr  = c.getString(c.getColumnIndexOrThrow("rating"));
            String clientName = c.getString(c.getColumnIndexOrThrow("client_name"));

            float rating = 0;
            try { rating = Float.parseFloat(ratingStr); } catch (Exception ignored) {}

            total += rating;
            count++;

            addRatingCard(clientName, rating);
        } while (c.moveToNext());
        c.close();

        if (count > 0) {
            float avg = total / count;
            avgLabel.setText(String.format("Average Rating: %.1f ⭐  (%d review%s)",
                    avg, count, count == 1 ? "" : "s"));
        }
    }

    private void addRatingCard(String clientName, float rating) {
        CardView card = new CardView(this);
        android.view.ViewGroup.MarginLayoutParams lp =
                new android.view.ViewGroup.MarginLayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(10);
        card.setLayoutParams(lp);
        card.setRadius(dp(14));
        card.setCardElevation(dp(4));
        card.setCardBackgroundColor(0xFFFFFFFF);

        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.HORIZONTAL);
        inner.setGravity(android.view.Gravity.CENTER_VERTICAL);
        inner.setPadding(dp(18), dp(16), dp(18), dp(16));
        card.addView(inner);

        // Left: client name
        LinearLayout left = new LinearLayout(this);
        left.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams leftLp = new LinearLayout.LayoutParams(0,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        left.setLayoutParams(leftLp);

        TextView nameTv = new TextView(this);
        nameTv.setText("👤 " + clientName);
        nameTv.setTextSize(15);
        nameTv.setTypeface(null, android.graphics.Typeface.BOLD);
        nameTv.setTextColor(0xFF212121);
        left.addView(nameTv);

        inner.addView(left);

        // Right: stars
        TextView starsTv = new TextView(this);
        starsTv.setText(starsDisplay(rating));
        starsTv.setTextSize(18);
        starsTv.setTextColor(0xFFF57F17);
        inner.addView(starsTv);
    }

    private String starsDisplay(float rating) {
        int full = Math.round(rating);
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) sb.append(i <= full ? "★" : "☆");
        sb.append(" ").append(String.format("%.0f", rating));
        return sb.toString();
    }

    private void addEmptyState(String msg) {
        TextView tv = new TextView(this);
        tv.setText(msg);
        tv.setTextSize(15);
        tv.setTextColor(0xFF757575);
        tv.setGravity(android.view.Gravity.CENTER);
        tv.setPadding(dp(24), dp(40), dp(24), dp(40));
        container.addView(tv);
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }
}
