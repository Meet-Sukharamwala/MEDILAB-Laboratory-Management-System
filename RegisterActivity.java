package com.example.medilab.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.R;
import com.example.medilab.database.DatabaseHelper;
import com.example.medilab.models.User;
import com.example.medilab.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etEmail, etMobile, etPassword, etConfirmPassword, etDob, etAddress;
    private TextInputLayout tilFullName, tilEmail, tilMobile, tilPassword, tilConfirmPassword;
    private Spinner spinnerBloodGroup;
    private RadioGroup rgGender;
    private MaterialButton btnRegister, btnLoginRedirect;
    private View btnBack;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private static final String[] BLOOD_GROUPS = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-", "Unknown"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupBloodGroupSpinner();
        setupDatePicker();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etDob = findViewById(R.id.etDob);
        etAddress = findViewById(R.id.etAddress);

        tilFullName = findViewById(R.id.tilFullName);
        tilEmail = findViewById(R.id.tilEmail);
        tilMobile = findViewById(R.id.tilMobile);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        spinnerBloodGroup = findViewById(R.id.spinnerBloodGroup);
        rgGender = findViewById(R.id.rgGender);
        btnRegister = findViewById(R.id.btnRegister);
        btnLoginRedirect = findViewById(R.id.btnLoginRedirect);
    }

    private void setupBloodGroupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, BLOOD_GROUPS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBloodGroup.setAdapter(adapter);
    }

    private void setupDatePicker() {
        etDob.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR) - 25; // default around 25 years old
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(RegisterActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String formatted = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                                selectedYear, selectedMonth + 1, selectedDay);
                        etDob.setText(formatted);
                    }, year, month, day);
            datePicker.show();
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnLoginRedirect.setOnClickListener(v -> {
            finish();
        });

        btnRegister.setOnClickListener(v -> attemptRegistration());
    }

    private void attemptRegistration() {
        // Clear errors
        tilFullName.setError(null);
        tilEmail.setError(null);
        tilMobile.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        String name = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String mobile = etMobile.getText() != null ? etMobile.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";
        String dob = etDob.getText() != null ? etDob.getText().toString().trim() : "";
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
        String bloodGroup = spinnerBloodGroup.getSelectedItem().toString();

        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        RadioButton rb = findViewById(selectedGenderId);
        String gender = rb != null ? rb.getText().toString() : "Male";

        // Form Validation
        if (TextUtils.isEmpty(name)) {
            tilFullName.setError("Please enter your full name");
            etFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(mobile) || mobile.length() < 10) {
            tilMobile.setError("Please enter a valid 10-digit mobile number");
            etMobile.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 4) {
            tilPassword.setError("Password must be at least 4 characters long");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        // Check duplicates
        if (dbHelper.checkUserExists(email)) {
            tilEmail.setError("An account with this email already exists");
            etEmail.requestFocus();
            return;
        }

        if (dbHelper.checkUserExists(mobile)) {
            tilMobile.setError("An account with this mobile number already exists");
            etMobile.requestFocus();
            return;
        }

        // Insert new user
        User user = new User(name, email, mobile, password, dob, gender, bloodGroup, address);
        long userId = dbHelper.addUser(user);

        if (userId > 0) {
            Toast.makeText(this, "Account created successfully! Logging in...", Toast.LENGTH_SHORT).show();

            // Link patient record if exists or just created
            int patientId = 0;
            com.example.medilab.models.Patient p = dbHelper.getPatientByPhone(mobile);
            if (p != null) {
                patientId = p.getPatientId();
            }

            // Save user session
            sessionManager.createUserSession((int) userId, patientId, name, email, mobile);

            // Redirect to UserDashboardActivity
            Intent intent = new Intent(this, UserDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to register account. Please try again.", Toast.LENGTH_LONG).show();
        }
    }
}
