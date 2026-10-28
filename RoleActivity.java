package com.example.shaktisetu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class RoleActivity extends AppCompatActivity {

    Button workerBtn, clientBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role);

        workerBtn = findViewById(R.id.workerBtn);
        clientBtn = findViewById(R.id.clientBtn);

        // 👷 WORKER
        workerBtn.setOnClickListener(v -> {

            // ✅ Save role
            getSharedPreferences("user", MODE_PRIVATE)
                    .edit()
                    .putString("role", "worker")
                    .apply();

            Intent intent = new Intent(RoleActivity.this, HomeActivity.class);
            intent.putExtra("role", "worker");
            startActivity(intent);
        });

        // 👩 CLIENT
        clientBtn.setOnClickListener(v -> {

            // ✅ Save role
            getSharedPreferences("user", MODE_PRIVATE)
                    .edit()
                    .putString("role", "client")
                    .apply();

            Intent intent = new Intent(RoleActivity.this, HomeActivity.class);
            intent.putExtra("role", "client");
            startActivity(intent);
        });
    }
}