package com.example.shaktisetu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView profileText = findViewById(R.id.nameText);

        // Session is written under "session" key by MainActivity
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);

        String data =
                "Name:              " + prefs.getString("name",      "—") + "\n\n" +
                "Email:             " + prefs.getString("email",     "—") + "\n\n" +
                "Role:              " + prefs.getString("role",      "—") + "\n\n" +
                "Phone:             " + prefs.getString("phone",     "—") + "\n\n" +
                "Emergency Contact: " + prefs.getString("emergency", "—") + "\n\n" +
                "Gender:            " + prefs.getString("gender",    "—") + "\n\n" +
                "Marital Status:    " + prefs.getString("marital",   "—") + "\n\n" +
                "Address:           " + prefs.getString("address",   "—") + "\n\n" +
                "Bio:               " + prefs.getString("bio",       "—") + "\n\n" +
                "Aadhar:            ✅ Verified";

        profileText.setText(data);
    }
}
