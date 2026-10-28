package com.example.shaktisetu;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    TextView tvChatUser;
    ListView listMessages;
    EditText etMessage;
    Button btnSend;

    DBHelper db;

    String sender, receiver;

    ArrayList<String> messages;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        db = new DBHelper(this);

        // UI
        tvChatUser = findViewById(R.id.tvChatUser);
        listMessages = findViewById(R.id.listMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        // Sender (logged user)
        sender = getSharedPreferences("session", MODE_PRIVATE)
                .getString("email", "");

        // Receiver (passed from previous screen)
        receiver = getIntent().getStringExtra("receiver");

        if (receiver == null || receiver.isEmpty()) {
            Toast.makeText(this, "No user selected!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvChatUser.setText("Chat with: " + receiver);

        messages = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, messages);

        listMessages.setAdapter(adapter);

        loadMessages();

        // SEND BUTTON
        btnSend.setOnClickListener(v -> {

            String msg = etMessage.getText().toString().trim();

            if (msg.isEmpty()) {
                Toast.makeText(this, "Enter message", Toast.LENGTH_SHORT).show();
                return;
            }

            db.sendMessage(sender, receiver, msg);

            etMessage.setText("");

            loadMessages();
        });
    }

    private void loadMessages() {

        messages.clear();

        Cursor c = db.getMessages(sender, receiver);

        if (c.moveToFirst()) {
            do {
                String s = c.getString(c.getColumnIndexOrThrow("sender"));
                String m = c.getString(c.getColumnIndexOrThrow("message"));

                if (s.equals(sender)) {
                    messages.add("Me: " + m);
                } else {
                    messages.add("Them: " + m);
                }

            } while (c.moveToNext());
        }

        c.close();

        adapter.notifyDataSetChanged();

        listMessages.setSelection(messages.size() - 1);
    }
}