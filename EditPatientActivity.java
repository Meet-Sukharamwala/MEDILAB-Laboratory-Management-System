package com.example.medilab.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.R;
import com.example.medilab.database.DatabaseHelper;
import com.example.medilab.models.Patient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Activity for modifying existing Patient data in SQLite.
 * Demonstrates UPDATE operation.
 */
public class EditPatientActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilAge, tilPhone, tilEmail, tilAddress;
    private TextInputEditText etName, etAge, etPhone, etEmail, etAddress;
    private Spinner spinnerGender, spinnerBloodGroup;
    private TextView tvEditingId;
    private MaterialButton btnUpdate, btnBack;

    private DatabaseHelper dbHelper;
    private int patientId = -1;
    private Patient currentPatient;

    private static final String[] GENDERS = {"Male", "Female", "Other"};
    private static final String[] BLOOD_GROUPS = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_patient);

        dbHelper = new DatabaseHelper(this);
        patientId = getIntent().getIntExtra("patient_id", -1);

        if (patientId == -1) {
            Toast.makeText(this, "Error: Invalid Patient ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupSpinners();
        loadPatientData();
        setupListeners();
    }

    private void initViews() {
        tilName = findViewById(R.id.til_edit_name);
        tilAge = findViewById(R.id.til_edit_age);
        tilPhone = findViewById(R.id.til_edit_phone);
        tilEmail = findViewById(R.id.til_edit_email);
        tilAddress = findViewById(R.id.til_edit_address);

        etName = findViewById(R.id.et_edit_patient_name);
        etAge = findViewById(R.id.et_edit_patient_age);
        etPhone = findViewById(R.id.et_edit_patient_phone);
        etEmail = findViewById(R.id.et_edit_patient_email);
        etAddress = findViewById(R.id.et_edit_patient_address);

        spinnerGender = findViewById(R.id.spinner_edit_gender);
        spinnerBloodGroup = findViewById(R.id.spinner_edit_blood_group);
        tvEditingId = findViewById(R.id.tv_editing_id_badge);

        btnUpdate = findViewById(R.id.btn_update_patient);
        btnBack = findViewById(R.id.btn_back_edit_patient);
    }

    private void setupSpinners() {
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, GENDERS);
        spinnerGender.setAdapter(genderAdapter);

        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, BLOOD_GROUPS);
        spinnerBloodGroup.setAdapter(bloodAdapter);
    }

    private void loadPatientData() {
        currentPatient = dbHelper.getPatientById(patientId);
        if (currentPatient == null) {
            Toast.makeText(this, "Patient not found in database", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvEditingId.setText("Editing Patient #" + currentPatient.getId());
        etName.setText(currentPatient.getName());
        etAge.setText(String.valueOf(currentPatient.getAge()));
        etPhone.setText(currentPatient.getPhone());
        etEmail.setText(currentPatient.getEmail());
        etAddress.setText(currentPatient.getAddress());

        // Set spinner selections
        for (int i = 0; i < GENDERS.length; i++) {
            if (GENDERS[i].equalsIgnoreCase(currentPatient.getGender())) {
                spinnerGender.setSelection(i);
                break;
            }
        }

        for (int i = 0; i < BLOOD_GROUPS.length; i++) {
            if (BLOOD_GROUPS[i].equalsIgnoreCase(currentPatient.getBloodGroup())) {
                spinnerBloodGroup.setSelection(i);
                break;
            }
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnUpdate.setOnClickListener(v -> updatePatient());
    }

    private void updatePatient() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String ageStr = etAge.getText() != null ? etAge.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
        String gender = spinnerGender.getSelectedItem().toString();
        String bloodGroup = spinnerBloodGroup.getSelectedItem().toString();

        tilName.setError(null);
        tilAge.setError(null);
        tilPhone.setError(null);
        tilEmail.setError(null);

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

        Patient updated = new Patient(patientId, name, age, gender, phone, email, address, bloodGroup);
        int rows = dbHelper.updatePatient(updated);

        if (rows > 0) {
            Toast.makeText(this, getString(R.string.msg_patient_updated), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Update failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
