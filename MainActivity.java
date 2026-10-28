package com.example.shaktisetu;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Patterns;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    Button loginBtn, registerBtn, otpBtn;
    EditText email, password;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ✅ 1. Request Notification Permission (Android 13+)
        checkNotificationPermission();

        // ✅ 2. Check Exact Alarm Permission (Android 12+)
        checkExactAlarmPermission();

        // ✅ Initialize DB
        db = new DBHelper(this);
        db.getWritableDatabase();

        email    = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn    = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        otpBtn      = findViewById(R.id.otpBtn);

        loginBtn.setOnClickListener(v -> {
            String userEmail = email.getText().toString().trim();
            String userPass  = password.getText().toString();

            if (userEmail.isEmpty()) {
                email.setError("Enter Email");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                email.setError("Invalid Email");
                return;
            }

            if (userPass.isEmpty()) {
                password.setError("Enter Password");
                return;
            }

            Cursor c = db.loginUser(userEmail, userPass);

            if (c == null || !c.moveToFirst()) {
                if (c != null) c.close();
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences.Editor ed = getSharedPreferences("session", MODE_PRIVATE).edit();
            ed.putString("email", c.getString(c.getColumnIndexOrThrow("email")));
            ed.putString("name",  c.getString(c.getColumnIndexOrThrow("name")));
            ed.putString("role",  c.getString(c.getColumnIndexOrThrow("role")));
            ed.apply();

            String role = c.getString(c.getColumnIndexOrThrow("role"));
            c.close();

            Intent i = new Intent(this, HomeActivity.class);
            i.putExtra("role", role);
            startActivity(i);
            finish();
        });

        otpBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, OTPActivity.class);
            i.putExtra("email", email.getText().toString());
            startActivity(i);
        });

        registerBtn.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    private void checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(this, "Please enable 'Alarms & Reminders' for this app", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }
    }
}
