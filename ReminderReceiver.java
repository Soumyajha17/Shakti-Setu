package com.example.shaktisetu;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {

    public static final String ACTION_CONFIRM_PRESENCE = "com.example.shaktisetu.CONFIRM_PRESENCE";
    public static final String ACTION_PENALTY_CHECK = "com.example.shaktisetu.PENALTY_CHECK";
    public static final String ACTION_REMINDER = "com.example.shaktisetu.REMINDER";
    private static final String CHANNEL_ID = "booking_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        DBHelper db = new DBHelper(context);
        long bookingId = intent.getLongExtra("bookingId", -1);
        String action = intent.getAction();

        createNotificationChannel(context);

        if (ACTION_CONFIRM_PRESENCE.equals(action)) {
            db.confirmPresence(bookingId);
            Toast.makeText(context, "Presence Confirmed!", Toast.LENGTH_SHORT).show();
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel((int) bookingId);
            return;
        }

        if (ACTION_PENALTY_CHECK.equals(action)) {
            if (!db.isPresenceConfirmed(bookingId)) {
                Cursor c = db.getBookingById(bookingId);
                if (c != null && c.moveToFirst()) {
                    String workerEmail = c.getString(c.getColumnIndexOrThrow("worker_email"));
                    String status = c.getString(c.getColumnIndexOrThrow("status"));
                    String amountStr = c.getString(c.getColumnIndexOrThrow("amount"));
                    
                    if (status.equalsIgnoreCase("Payment Hold")) {
                        double amount = 500;
                        try { amount = Double.parseDouble(amountStr); } catch (Exception ignored) {}
                        
                        db.deductWallet(workerEmail, amount);
                        db.updateBookingStatus(bookingId, "Cancelled - No Show");
                        db.insertNotification(workerEmail, "Full Payment Deducted", "₹" + amount + " deducted for not confirming presence.");
                        
                        showStatusBarNotification(context, (int)bookingId + 20000, 
                                "🚨 Penalty Applied", 
                                "₹" + amount + " deducted from your wallet for no-show.");
                    }
                }
                if (c != null) c.close();
            }
            return;
        }

        // Show Presence Reminder
        showPresenceReminder(context, bookingId);
    }

    private void showPresenceReminder(Context context, long bookingId) {
        Intent confirmIntent = new Intent(context, ReminderReceiver.class);
        confirmIntent.setAction(ACTION_CONFIRM_PRESENCE);
        confirmIntent.putExtra("bookingId", bookingId);
        
        PendingIntent confirmPendingIntent = PendingIntent.getBroadcast(
                context, (int) bookingId, confirmIntent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("⏰ Job Confirmation Required")
                .setContentText("Please confirm you will be present for your upcoming job!")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .addAction(android.R.drawable.ic_menu_view, "I WILL BE PRESENT", confirmPendingIntent);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify((int) bookingId, builder.build());
    }

    private void showStatusBarNotification(Context context, int id, String title, String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id, builder.build());
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Booking Notifications", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }
}
