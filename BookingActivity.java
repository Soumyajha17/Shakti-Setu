package com.example.shaktisetu;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class BookingActivity extends AppCompatActivity {

    Button dateBtn, timeBtn, confirmBtn;
    TextView workerText;

    Calendar calendar;

    String worker, workerEmail, clientEmail;
    String selectedDate = "", selectedTime = "";

    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        db = new DBHelper(this);

        workerText = findViewById(R.id.workerText);
        dateBtn    = findViewById(R.id.dateBtn);
        timeBtn    = findViewById(R.id.timeBtn);
        confirmBtn = findViewById(R.id.confirmBtn);

        worker      = getIntent().getStringExtra("worker");
        workerEmail = getIntent().getStringExtra("workerEmail");

        clientEmail = getSharedPreferences("session", MODE_PRIVATE)
                .getString("email", "");

        workerText.setText("Worker: " + (worker != null ? worker : "Not Selected"));

        calendar = Calendar.getInstance();

        // 📅 Date Picker
        dateBtn.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                    BookingActivity.this,
                    (view, year, month, day) -> {
                        selectedDate = day + "/" + (month + 1) + "/" + year;
                        dateBtn.setText("📅 " + selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            dialog.getDatePicker().setMinDate(System.currentTimeMillis());
            dialog.show();
        });

        // 🕐 Time Picker
        timeBtn.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();

            TimePickerDialog dialog = new TimePickerDialog(
                    BookingActivity.this,
                    (view, hour, minute) -> {
                        selectedTime = hour + ":" + String.format("%02d", minute);
                        timeBtn.setText("🕐 " + selectedTime);
                    },
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    true
            );

            dialog.show();
        });

        // ✅ Confirm Booking
        confirmBtn.setOnClickListener(v -> {

            if (selectedDate.isEmpty()) {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedTime.isEmpty()) {
                Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
                return;
            }

            String service = getIntent().getStringExtra("service");
            if (service == null) service = "Service";

            long bookingId = db.insertBooking(
                    clientEmail,
                    workerEmail != null ? workerEmail : "worker",
                    service,
                    selectedDate,
                    selectedTime,
                    "500"
            );

            if (bookingId != -1) {

                Toast.makeText(this,
                        "Booking sent to worker successfully!",
                        Toast.LENGTH_SHORT).show();

                // 🔔 SET REMINDERS & PENALTY CHECK
                setReminders(bookingId);

                Intent i = new Intent(this, PaymentOptionActivity.class);
                i.putExtra("worker", worker);
                i.putExtra("workerEmail", workerEmail);
                i.putExtra("clientEmail", clientEmail);
                i.putExtra("amount", "500");
                i.putExtra("bookingId", bookingId);
                startActivity(i);

            } else {
                Toast.makeText(this,
                        "Booking failed. Try again.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 🔔 REMINDER & PENALTY SYSTEM
    private void setReminders(long bookingId) {
        try {
            String[] dateParts = selectedDate.split("/");
            String[] timeParts = selectedTime.split(":");

            Calendar jobTime = Calendar.getInstance();
            jobTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateParts[0]));
            jobTime.set(Calendar.MONTH, Integer.parseInt(dateParts[1]) - 1);
            jobTime.set(Calendar.YEAR, Integer.parseInt(dateParts[2]));
            jobTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
            jobTime.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
            jobTime.set(Calendar.SECOND, 0);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager == null) return;

            // 1. Notification Reminder (2 hours before)
            Calendar reminderTime = (Calendar) jobTime.clone();
            reminderTime.add(Calendar.HOUR_OF_DAY, -2);

            Intent intentRem = new Intent(this, ReminderReceiver.class);
            intentRem.setAction(ReminderReceiver.ACTION_REMINDER);
            intentRem.putExtra("bookingId", bookingId);

            PendingIntent piRem = PendingIntent.getBroadcast(
                    this, (int) bookingId, intentRem,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            // Use setAndAllowWhileIdle for more reliability if needed, but setExact is standard for MAD labs
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTime.getTimeInMillis(), piRem);

            // 2. Penalty Check (1 hour before)
            Calendar penaltyTime = (Calendar) jobTime.clone();
            penaltyTime.add(Calendar.HOUR_OF_DAY, -1);

            Intent intentPen = new Intent(this, ReminderReceiver.class);
            intentPen.setAction(ReminderReceiver.ACTION_PENALTY_CHECK);
            intentPen.putExtra("bookingId", bookingId);

            // Unique request code for penalty check
            PendingIntent piPen = PendingIntent.getBroadcast(
                    this, (int) bookingId + 10000, intentPen,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, penaltyTime.getTimeInMillis(), piPen);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
