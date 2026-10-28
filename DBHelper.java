package com.example.shaktisetu;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "ShaktiSetu.db";
    private static final int DB_VERSION = 11; // Incremented for presence_confirmed

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE users(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "email TEXT UNIQUE," +
                "password TEXT," +
                "phone TEXT," +
                "emergency TEXT," +
                "gender TEXT," +
                "marital TEXT," +
                "address TEXT," +
                "aadhar TEXT," +
                "bio TEXT," +
                "role TEXT)");

        db.execSQL("CREATE TABLE bookings(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "client_email TEXT," +
                "worker_email TEXT," +
                "service TEXT," +
                "date TEXT," +
                "time TEXT," +
                "amount TEXT," +
                "status TEXT," +
                "presence_confirmed INTEGER DEFAULT 0)");

        db.execSQL("CREATE TABLE payment_requests(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "booking_id INTEGER," +
                "worker_email TEXT," +
                "client_email TEXT," +
                "amount TEXT," +
                "status TEXT)");

        db.execSQL("CREATE TABLE payments(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "booking_id INTEGER," +
                "client_email TEXT," +
                "worker_email TEXT," +
                "amount TEXT," +
                "method TEXT)");

        db.execSQL("CREATE TABLE wallet(" +
                "user_email TEXT PRIMARY KEY," +
                "balance REAL DEFAULT 0," +
                "hold_balance REAL DEFAULT 0)");

        db.execSQL("CREATE TABLE notifications(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_email TEXT," +
                "title TEXT," +
                "message TEXT)");

        db.execSQL("CREATE TABLE messages(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "sender TEXT," +
                "receiver TEXT," +
                "message TEXT)");

        db.execSQL("CREATE TABLE ratings(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "worker_email TEXT," +
                "client_email TEXT," +
                "rating TEXT)");

        seedUsers(db);
    }

    private void seedUsers(SQLiteDatabase db) {
        insertUser(db, "Meena", "m@gmail.com", "123456", "Worker");
        insertUser(db, "Priya", "p@gmail.com", "654321", "Client");
    }

    private void insertUser(SQLiteDatabase db, String name, String email, String pass, String role) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("email", email);
        cv.put("password", pass);
        cv.put("role", role);
        db.insert("users", null, cv);

        ContentValues w = new ContentValues();
        w.put("user_email", email);
        db.insert("wallet", null, w);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS bookings");
        db.execSQL("DROP TABLE IF EXISTS payment_requests");
        db.execSQL("DROP TABLE IF EXISTS payments");
        db.execSQL("DROP TABLE IF EXISTS wallet");
        db.execSQL("DROP TABLE IF EXISTS notifications");
        db.execSQL("DROP TABLE IF EXISTS messages");
        db.execSQL("DROP TABLE IF EXISTS ratings");
        onCreate(db);
    }

    // CHECK EMAIL EXISTS
    public boolean emailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email=?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // REGISTER USER
    public boolean registerUser(String name, String email, String password, String phone,
                                String emergency, String gender, String marital,
                                String address, String aadhar, String bio, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("email", email);
        cv.put("password", password);
        cv.put("phone", phone);
        cv.put("emergency", emergency);
        cv.put("gender", gender);
        cv.put("marital", marital);
        cv.put("address", address);
        cv.put("aadhar", aadhar);
        cv.put("bio", bio);
        cv.put("role", role);

        long res = db.insert("users", null, cv);

        if (res != -1) {
            ContentValues w = new ContentValues();
            w.put("user_email", email);
            db.insert("wallet", null, w);
            return true;
        }
        return false;
    }

    // LOGIN
    public Cursor loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM users WHERE email=? AND password=?",
                new String[]{email, password});
    }

    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        if (email == null || email.isEmpty()) {
            return db.rawQuery("SELECT * FROM users WHERE role='Worker'", null);
        }
        return db.rawQuery("SELECT * FROM users WHERE email=?", new String[]{email});
    }

    // BOOKINGS
    public long insertBooking(String client, String worker,
                              String service, String date,
                              String time, String amount) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("client_email", client);
        cv.put("worker_email", worker);
        cv.put("service", service);
        cv.put("date", date);
        cv.put("time", time);
        cv.put("amount", amount);
        cv.put("status", "Pending");

        return db.insert("bookings", null, cv);
    }

    public void updateBookingStatus(long id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        db.update("bookings", cv, "id=?", new String[]{String.valueOf(id)});
    }

    public void confirmPresence(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("presence_confirmed", 1);
        db.update("bookings", cv, "id=?", new String[]{String.valueOf(id)});
    }

    public boolean isPresenceConfirmed(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT presence_confirmed FROM bookings WHERE id=?", new String[]{String.valueOf(id)});
        boolean confirmed = false;
        if (cursor.moveToFirst()) {
            confirmed = cursor.getInt(0) == 1;
        }
        cursor.close();
        return confirmed;
    }

    public void deductWallet(String email, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT balance FROM wallet WHERE user_email=?", new String[]{email});
        if (c.moveToFirst()) {
            double bal = c.getDouble(0);
            bal -= amount;
            ContentValues cv = new ContentValues();
            cv.put("balance", bal);
            db.update("wallet", cv, "user_email=?", new String[]{email});
        }
        c.close();
    }

    public Cursor getBookingById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM bookings WHERE id=?", new String[]{String.valueOf(id)});
    }

    public Cursor getClientBookings(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM bookings WHERE client_email=?",
                new String[]{email});
    }

    public Cursor getClientBookingsDetailed(String clientEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT b.*, u.name as worker_name FROM bookings b JOIN users u ON b.worker_email = u.email WHERE b.client_email=?",
                new String[]{clientEmail});
    }

    public Cursor getWorkerBookings(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM bookings WHERE worker_email=?",
                new String[]{email});
    }

    public Cursor getWorkerBookingsDetailed(String workerEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT b.*, u.name as client_name FROM bookings b JOIN users u ON b.client_email = u.email WHERE b.worker_email=?",
                new String[]{workerEmail});
    }

    public long workerRequestPayment(String workerEmail, String clientEmail, String amount, long bookingId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("worker_email", workerEmail);
        cv.put("client_email", clientEmail);
        cv.put("amount", amount);
        cv.put("booking_id", bookingId);
        cv.put("status", "Pending");
        return db.insert("payment_requests", null, cv);
    }

    public Cursor getPendingRequestsForClient(String clientEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT pr.*, u.name as worker_name FROM payment_requests pr JOIN users u ON pr.worker_email = u.email WHERE pr.client_email=? AND pr.status='Pending'",
                new String[]{clientEmail});
    }

    public void clientPaysRequest(int requestId, String clientEmail, String workerEmail, String amount, String method) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("status", "Paid");
        db.update("payment_requests", cv, "id=?", new String[]{String.valueOf(requestId)});

        insertPaymentFull(clientEmail, workerEmail, amount, method);
    }

    public void insertPaymentFull(String clientEmail, String workerEmail, String amount, String method) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues p = new ContentValues();
        p.put("client_email", clientEmail);
        p.put("worker_email", workerEmail);
        p.put("amount", amount);
        p.put("method", method);
        db.insert("payments", null, p);

        Cursor c = db.rawQuery("SELECT hold_balance FROM wallet WHERE user_email=?", new String[]{workerEmail});
        if (c.moveToFirst()) {
            double hold = c.getDouble(0);
            try {
                hold += Double.parseDouble(amount);
            } catch (Exception e) {
                hold += 0;
            }
            ContentValues w = new ContentValues();
            w.put("hold_balance", hold);
            db.update("wallet", w, "user_email=?", new String[]{workerEmail});
        }
        c.close();
    }

    // PAYMENT HISTORY
    public Cursor getPaymentsForWorker(String workerEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM payments WHERE worker_email=?",
                new String[]{workerEmail});
    }

    public Cursor getClientPaymentHistoryDetailed(String clientEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM payments WHERE client_email=?",
                new String[]{clientEmail});
    }

    // PAYMENT HOLD
    public void clientPays(String client, String worker, String amount, long bookingId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT hold_balance FROM wallet WHERE user_email=?",
                new String[]{worker});

        if (c.moveToFirst()) {
            double hold = c.getDouble(0);
            hold += Double.parseDouble(amount);

            ContentValues cv = new ContentValues();
            cv.put("hold_balance", hold);
            db.update("wallet", cv, "user_email=?", new String[]{worker});
        }
        c.close();

        ContentValues b = new ContentValues();
        b.put("status", "Payment Hold");
        db.update("bookings", b, "id=?", new String[]{String.valueOf(bookingId)});
    }

    // COMPLETE JOB
    public void markCompleted(long bookingId, String workerEmail, String amount) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues b = new ContentValues();
        b.put("status", "Completed");
        db.update("bookings", b, "id=?", new String[]{String.valueOf(bookingId)});

        Cursor c = db.rawQuery(
                "SELECT balance, hold_balance FROM wallet WHERE user_email=?",
                new String[]{workerEmail});

        if (c.moveToFirst()) {
            double balance = c.getDouble(0);
            double hold = c.getDouble(1);
            double amt = Double.parseDouble(amount);

            balance += amt;
            hold -= amt;

            ContentValues cv = new ContentValues();
            cv.put("balance", balance);
            cv.put("hold_balance", hold);

            db.update("wallet", cv, "user_email=?", new String[]{workerEmail});
        }
        c.close();
    }

    // NOTIFICATIONS
    public void insertNotification(String email, String title, String msg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user_email", email);
        cv.put("title", title);
        cv.put("message", msg);
        db.insert("notifications", null, cv);
    }

    public Cursor getNotifications(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM notifications WHERE user_email=?",
                new String[]{email});
    }

    // CHAT
    public void sendMessage(String s, String r, String m) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("sender", s);
        cv.put("receiver", r);
        cv.put("message", m);
        db.insert("messages", null, cv);
    }

    public Cursor getMessages(String u1, String u2) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM messages WHERE (sender=? AND receiver=?) OR (sender=? AND receiver=?)",
                new String[]{u1, u2, u2, u1});
    }

    // INSERT RATING
    public boolean insertRating(String workerEmail, String clientEmail, String rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("worker_email", workerEmail);
        cv.put("client_email", clientEmail);
        cv.put("rating", rating);

        long result = db.insert("ratings", null, cv);
        db.close();
        return result != -1;
    }

    public Cursor getRatingsForWorker(String workerEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT r.*, u.name as client_name FROM ratings r JOIN users u ON r.client_email = u.email WHERE r.worker_email=?",
                new String[]{workerEmail});
    }

}
