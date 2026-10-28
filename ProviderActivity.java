package com.example.shaktisetu;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ProviderActivity extends AppCompatActivity {

    ListView listView;
    DBHelper db;
    ArrayList<String> workerEmails = new ArrayList<>();
    ArrayList<String> workerNames  = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);

        listView = findViewById(R.id.listView);
        db       = new DBHelper(this);

        // Load all workers from DB
        Cursor c = db.getUserByEmail(""); // get all workers below
        // Use raw query to get all workers
        android.database.sqlite.SQLiteDatabase sqlDb = db.getReadableDatabase();
        Cursor workers = sqlDb.rawQuery(
                "SELECT name, email FROM users WHERE role='Worker'", null);

        ArrayList<String> labels = new ArrayList<>();
        while (workers.moveToNext()) {
            String name  = workers.getString(0);
            String email = workers.getString(1);
            workerNames.add(name);
            workerEmails.add(email);
            labels.add("👩‍🔧 " + name + "  ·  " + email);
        }
        workers.close();

        // Fallback static list if DB is empty (first run before any worker registers)
        if (labels.isEmpty()) {
            labels.add("Meena — m@gmail.com  (Demo Worker)");
            workerNames.add("Meena");
            workerEmails.add("m@gmail.com");
        }

        listView.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, labels));

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, BookingActivity.class);
            intent.putExtra("worker",      workerNames.get(position));
            intent.putExtra("workerEmail", workerEmails.get(position));
            intent.putExtra("service",     getIntent().getStringExtra("category"));
            startActivity(intent);
        });
    }
}
