package com.example.shaktisetu;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {

    ListView userListView;
    DBHelper db;
    ArrayList<String> userEmails = new ArrayList<>();
    ArrayList<String> userNames  = new ArrayList<>();
    String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userListView = findViewById(R.id.userListView);
        db = new DBHelper(this);

        currentUserEmail = getSharedPreferences("session", MODE_PRIVATE).getString("email", "");

        loadUsers();
    }

    private void loadUsers() {
        SQLiteDatabase sqlDb = db.getReadableDatabase();
        // Get all users except current user
        Cursor cursor = sqlDb.rawQuery("SELECT name, email, role FROM users WHERE email != ?", new String[]{currentUserEmail});

        ArrayList<String> labels = new ArrayList<>();
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            String email = cursor.getString(1);
            String role = cursor.getString(2);
            
            userNames.add(name);
            userEmails.add(email);
            labels.add((role.equalsIgnoreCase("Worker") ? "👩‍🔧 " : "👤 ") + name + " (" + role + ")");
        }
        cursor.close();

        if (labels.isEmpty()) {
            labels.add("No other users found.");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, labels);
        userListView.setAdapter(adapter);

        userListView.setOnItemClickListener((parent, view, position, id) -> {
            if (userEmails.isEmpty()) return;
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("receiver", userEmails.get(position));
            startActivity(intent);
        });
    }
}
