package com.example.shaktisetu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText name, email, password, address, aadhar, bio;
    EditText phone, emergencyContact;
    Spinner gender;
    RadioGroup maritalGroup, roleGroup;
    Button submit;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DBHelper(this);

        name             = findViewById(R.id.name);
        email            = findViewById(R.id.email);
        password         = findViewById(R.id.password);
        phone            = findViewById(R.id.phone);
        emergencyContact = findViewById(R.id.emergencyContact);
        gender           = findViewById(R.id.gender);
        maritalGroup     = findViewById(R.id.maritalGroup);
        roleGroup        = findViewById(R.id.roleGroup);
        address          = findViewById(R.id.address);
        aadhar           = findViewById(R.id.aadhar);
        bio              = findViewById(R.id.bio);
        submit           = findViewById(R.id.submitBtn);

        String[] genders = {"Select Gender", "Male", "Female", "Other"};
        gender.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, genders));

        submit.setOnClickListener(v -> {
            try {
                String n  = name.getText().toString().trim();
                String e  = email.getText().toString().trim();
                String pw = password.getText().toString();
                String ph = phone.getText().toString().trim();
                String em = emergencyContact.getText().toString().trim();
                String g  = gender.getSelectedItem().toString();
                String ad = address.getText().toString().trim();
                String aa = aadhar.getText().toString().trim();
                String bi = bio.getText().toString().trim();

                if (n.isEmpty())  { name.setError("Enter Name"); return; }
                if (e.isEmpty())  { email.setError("Enter Email"); return; }
                if (pw.isEmpty()) { password.setError("Enter Password"); return; }
                if (ph.length() != 10) { phone.setError("Enter 10-digit phone"); return; }
                if (em.length() != 10) { emergencyContact.setError("Invalid emergency contact"); return; }
                if (g.equals("Select Gender")) { Toast.makeText(this, "Select Gender", Toast.LENGTH_SHORT).show(); return; }
                if (aa.length() != 12) { aadhar.setError("Aadhar must be 12 digits"); return; }

                int maritalId = maritalGroup.getCheckedRadioButtonId();
                if (maritalId == -1) { Toast.makeText(this, "Select Marital Status", Toast.LENGTH_SHORT).show(); return; }
                String marital = ((RadioButton) findViewById(maritalId)).getText().toString();

                int roleId = roleGroup.getCheckedRadioButtonId();
                if (roleId == -1) { Toast.makeText(this, "Select Role", Toast.LENGTH_SHORT).show(); return; }
                String role = ((RadioButton) findViewById(roleId)).getText().toString();

                // Check duplicate email
                if (db.emailExists(e)) {
                    email.setError("Email already registered"); return;
                }

                boolean ok = db.registerUser(n, e, pw, ph, em, g, marital, ad, aa, bi, role);
                if (ok) {
                    Toast.makeText(this, "Registered Successfully ✅", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Registration failed. Try again.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                Toast.makeText(this, "Error: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
