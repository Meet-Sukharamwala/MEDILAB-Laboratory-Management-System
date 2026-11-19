package com.example.medilab.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.R;
import com.example.medilab.database.DatabaseHelper;
import com.example.medilab.models.Patient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Activity for registering a new Patient into SQLite.
 * Demonstrates CREATE operation with strict form validations.
 */
public class AddPatientActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilAge, tilPhone, tilEmail, tilAddress;
    private TextInputEditText etName, etAge, etPhone, etEmail, etAddress;
    private Spinner spinnerGender, spinnerBloodGroup;
    private MaterialButton btnSave, btnClear, btnBack;

    private DatabaseHelper dbHelper;

    private static final String[] GENDERS = {"Male", "Female", "Other"};
    private static final String[] BLOOD_GROUPS = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        dbHelper = new DatabaseHelper(this);

        initViews();
        setupSpinners();
        setupListeners();
    }

    private void initViews() {
        tilName = findViewById(R.id.til_name);
        tilAge = findViewById(R.id.til_age);
        tilPhone = findViewById(R.id.til_phone);
        tilEmail = findViewById(R.id.til_email);
        tilAddress = findViewById(R.id.til_address);

        etName = findViewById(R.id.et_patient_name);
        etAge = findViewById(R.id.et_patient_age);
        etPhone = findViewById(R.id.et_patient_phone);
        etEmail = findViewById(R.id.et_patient_email);
        etAddress = findViewById(R.id.et_patient_address);

        spinnerGender = findViewById(R.id.spinner_gender);
        spinnerBloodGroup = findViewById(R.id.spinner_blood_group);

        btnSave = findViewById(R.id.btn_save_patient);
        btnClear = findViewById(R.id.btn_clear_patient);
        btnBack = findViewById(R.id.btn_back_add_patient);
    }

    private void setupSpinners() {
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, GENDERS);
        spinnerGender.setAdapter(genderAdapter);

        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, BLOOD_GROUPS);
        spinnerBloodGroup.setAdapter(bloodAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnClear.setOnClickListener(v -> clearForm());
        btnSave.setOnClickListener(v -> savePatient());
    }

    private void clearForm() {
        etName.setText("");
        etAge.setText("");
        etPhone.setText("");
        etEmail.setText("");
        etAddress.setText("");
        spinnerGender.setSelection(0);
        spinnerBloodGroup.setSelection(0);

        tilName.setError(null);
        tilAge.setError(null);
        tilPhone.setError(null);
        tilEmail.setError(null);
    }

    private void savePatient() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String ageStr = etAge.getText() != null ? etAge.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
        String gender = spinnerGender.getSelectedItem().toString();
        String bloodGroup = spinnerBloodGroup.getSelectedItem().toString();

        // Reset errors
        tilName.setError(null);
        tilAge.setError(null);
        tilPhone.setError(null);
        tilEmail.setError(null);

        // Validations
        if (TextUtils.isEmpty(name)) {
            tilName.setError(getString(R.string.error_invalid_name));
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(ageStr)) {
            tilAge.setError(getString(R.string.error_invalid_age));
            etAge.requestFocus();
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age <= 0 || age > 130) {
                tilAge.setError(getString(R.string.error_invalid_age));
                etAge.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            tilAge.setError(getString(R.string.error_invalid_age));
            etAge.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone) || phone.length() < 10) {
            tilPhone.setError(getString(R.string.error_invalid_phone));
            etPhone.requestFocus();
            return;
        }

        if (!TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.error_invalid_email));
            etEmail.requestFocus();
            return;
        }

        Patient patient = new Patient(name, age, gender, phone, email, address, bloodGroup);
        long newId = dbHelper.addPatient(patient);

        if (newId != -1) {
            Toast.makeText(this, "Patient #" + newId + " added successfully!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to register patient. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
