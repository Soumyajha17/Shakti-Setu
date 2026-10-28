package com.example.shaktisetu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.SharedPreferences;

public class TrainingActivity extends AppCompatActivity {

    String[] trainings = {
        "Cooking Basics",
        "Advanced Tailoring",
        "Electrical Safety",
        "House Cleaning Techniques",
        "Driving Safety & Rules"
    };
    String[] durations = { "2 Weeks", "1 Month", "3 Weeks", "2 Weeks", "10 Days" };
    String[] dates     = { "1 April 2026", "5 April 2026", "10 April 2026", "15 April 2026", "20 April 2026" };
    String[] seats     = { "10", "5", "8", "12", "6" };
    String[] videos    = {
        "https://www.youtube.com/watch?v=1-SJGQ2HLp8",
        "https://www.youtube.com/watch?v=JHhXxg8F6Yk",
        "https://www.youtube.com/watch?v=U3aXWizDbQ4",
        "https://www.youtube.com/watch?v=3aZ9Xz5QhL0",
        "https://www.youtube.com/watch?v=aqz-KE-bpKQ"
    };
    // Icon emoji + category color per course
    String[] icons  = { "\uD83C\uDF73", "\u2702\uFE0F", "\u26A1", "\uD83E\uDDF9", "\uD83D\uDE97" };
    int[] cardColors = {
        0xFFFFF8E1,  // warm yellow - cooking
        0xFFE8EAF6,  // indigo tint - tailoring
        0xFFFFF3E0,  // amber tint  - electrical
        0xFFE1F5FE,  // light blue  - cleaning
        0xFFE8F5E9   // light green - driving
    };
    int[] accentColors = {
        0xFFF57F17,
        0xFF3949AB,
        0xFFE65100,
        0xFF0277BD,
        0xFF2E7D32
    };
    String[] tags = { "Beginner", "Intermediate", "Safety", "Household", "Transport" };

    // Loaded once in onCreate, used by buildCard
    private SharedPreferences trainingPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String userEmail = getSharedPreferences("session", MODE_PRIVATE).getString("email", "default");
        trainingPrefs = getSharedPreferences("training_" + userEmail, MODE_PRIVATE);

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(0xFFF3E5F5);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(0, 0, 0, dp(32));
        scroll.addView(root);

        // Header
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setBackgroundColor(0xFF6A1B9A);
        header.setPadding(dp(24), dp(28), dp(24), dp(28));
        TextView h1 = new TextView(this);
        h1.setText("Training Modules");
        h1.setTextSize(24); h1.setTypeface(null, android.graphics.Typeface.BOLD);
        h1.setTextColor(0xFFFFFFFF);
        header.addView(h1);
        TextView h2 = new TextView(this);
        h2.setText("Upskill and grow your career");
        h2.setTextSize(13); h2.setTextColor(0xFFDEDEDE);
        h2.setPadding(0, dp(4), 0, 0);
        header.addView(h2);
        root.addView(header);

        // Stats bar
        LinearLayout stats = new LinearLayout(this);
        stats.setOrientation(LinearLayout.HORIZONTAL);
        stats.setBackgroundColor(0xFF7B1FA2);
        stats.setPadding(dp(20), dp(12), dp(20), dp(12));
        addStatChip(stats, "5", "Courses");
        addStatChip(stats, "Free", "Enroll");
        addStatChip(stats, "Certificate", "On Completion");
        root.addView(stats);

        // Cards
        LinearLayout cardsContainer = new LinearLayout(this);
        cardsContainer.setOrientation(LinearLayout.VERTICAL);
        cardsContainer.setPadding(dp(16), dp(16), dp(16), dp(8));
        root.addView(cardsContainer);

        for (int i = 0; i < trainings.length; i++) {
            cardsContainer.addView(buildCard(i));
        }

        setContentView(scroll);
    }

    private android.view.View buildCard(int i) {
        CardView card = new CardView(this);
        android.view.ViewGroup.MarginLayoutParams lp =
                new android.view.ViewGroup.MarginLayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(14);
        card.setLayoutParams(lp);
        card.setRadius(dp(18));
        card.setCardElevation(dp(5));
        card.setCardBackgroundColor(cardColors[i]);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        row.setPadding(dp(18), dp(18), dp(18), dp(18));
        card.addView(row);

        // Icon circle
        LinearLayout iconCircle = new LinearLayout(this);
        int circleSize = dp(58);
        LinearLayout.LayoutParams circleLp = new LinearLayout.LayoutParams(circleSize, circleSize);
        circleLp.rightMargin = dp(16);
        iconCircle.setLayoutParams(circleLp);
        iconCircle.setGravity(android.view.Gravity.CENTER);
        iconCircle.setBackgroundColor(accentColors[i] & 0x22FFFFFF | (accentColors[i] & 0x00FFFFFF) | 0x22000000);
        // Use a rounded drawable - approximate with corner via background color + shape
        android.graphics.drawable.GradientDrawable circle = new android.graphics.drawable.GradientDrawable();
        circle.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        circle.setColor(withAlpha(accentColors[i], 0x22));
        iconCircle.setBackground(circle);

        TextView iconTv = new TextView(this);
        iconTv.setText(icons[i]);
        iconTv.setTextSize(26);
        iconTv.setGravity(android.view.Gravity.CENTER);
        iconCircle.addView(iconTv);
        row.addView(iconCircle);

        // Text content
        LinearLayout textCol = new LinearLayout(this);
        textCol.setOrientation(LinearLayout.VERTICAL);
        textCol.setLayoutParams(new LinearLayout.LayoutParams(0,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView nameTv = new TextView(this);
        nameTv.setText(trainings[i]);
        nameTv.setTextSize(16);
        nameTv.setTypeface(null, android.graphics.Typeface.BOLD);
        nameTv.setTextColor(0xFF212121);
        textCol.addView(nameTv);

        LinearLayout meta = new LinearLayout(this);
        meta.setOrientation(LinearLayout.HORIZONTAL);
        meta.setPadding(0, dp(4), 0, dp(6));

        addMetaBadge(meta, durations[i], 0xFF6A1B9A);

        // Read saved remaining seats; fall back to original if never enrolled
        int remainingSeats = trainingPrefs.getInt(trainings[i] + "_seats",
                Integer.parseInt(seats[i]));
        boolean enrolled = trainingPrefs.getBoolean(trainings[i] + "_enrolled", false);
        String seatLabel = enrolled ? "Enrolled" : (remainingSeats + " seats");
        int seatColor    = enrolled ? 0xFF2E7D32 : accentColors[i];
        addMetaBadge(meta, seatLabel, seatColor);
        textCol.addView(meta);

        TextView dateTv = new TextView(this);
        dateTv.setText("Starts: " + dates[i]);
        dateTv.setTextSize(12);
        dateTv.setTextColor(0xFF757575);
        textCol.addView(dateTv);

        row.addView(textCol);

        // Arrow
        TextView arrow = new TextView(this);
        arrow.setText(">");
        arrow.setTextSize(18);
        arrow.setTypeface(null, android.graphics.Typeface.BOLD);
        arrow.setTextColor(accentColors[i]);
        arrow.setPadding(dp(8), 0, 0, 0);
        row.addView(arrow);

        final int idx = i;
        card.setOnClickListener(v -> {
            Intent intent = new Intent(this, TrainingDetailActivity.class);
            intent.putExtra("title",    trainings[idx]);
            intent.putExtra("duration", durations[idx]);
            intent.putExtra("date",     dates[idx]);
            intent.putExtra("seats",    seats[idx]);
            intent.putExtra("video",    videos[idx]);
            startActivity(intent);
        });

        return card;
    }

    private void addStatChip(LinearLayout parent, String value, String label) {
        LinearLayout chip = new LinearLayout(this);
        chip.setOrientation(LinearLayout.VERTICAL);
        chip.setGravity(android.view.Gravity.CENTER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        chip.setLayoutParams(lp);

        TextView v = new TextView(this);
        v.setText(value); v.setTextSize(15);
        v.setTypeface(null, android.graphics.Typeface.BOLD);
        v.setTextColor(0xFFFFFFFF); v.setGravity(android.view.Gravity.CENTER);
        chip.addView(v);

        TextView l = new TextView(this);
        l.setText(label); l.setTextSize(11);
        l.setTextColor(0xFFDEDEDE); l.setGravity(android.view.Gravity.CENTER);
        chip.addView(l);

        parent.addView(chip);
    }

    private void addMetaBadge(LinearLayout parent, String text, int color) {
        TextView badge = new TextView(this);
        badge.setText(text);
        badge.setTextSize(11);
        badge.setTypeface(null, android.graphics.Typeface.BOLD);
        badge.setTextColor(color);
        android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
        bg.setCornerRadius(dp(20));
        bg.setColor(withAlpha(color, 0x18));
        badge.setBackground(bg);
        badge.setPadding(dp(8), dp(2), dp(8), dp(2));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.rightMargin = dp(6);
        badge.setLayoutParams(lp);
        parent.addView(badge);
    }

    private int withAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | (alpha << 24);
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }
}
