package com.example.medilab.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.R;
import com.example.medilab.database.DatabaseHelper;
import com.example.medilab.models.User;
import com.example.medilab.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class UserProfileActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvProfileName, tvProfileRole, tvProfileEmail;
    private TextInputEditText etEditName, etEditPhone, etEditAddress;
    private TextInputLayout tilEditName, tilEditPhone;
    private MaterialButton btnSaveProfile, btnViewPayments, btnGiveFeedback, btnLogoutProfile;
    private BottomNavigationView bottomNavigationView;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupBottomNavigation();
        loadProfileData();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileRole = findViewById(R.id.tvProfileRole);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);

        etEditName = findViewById(R.id.etEditName);
        etEditPhone = findViewById(R.id.etEditPhone);
        etEditAddress = findViewById(R.id.etEditAddress);

        tilEditName = findViewById(R.id.tilEditName);
        tilEditPhone = findViewById(R.id.tilEditPhone);

        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnViewPayments = findViewById(R.id.btnViewPayments);
        btnGiveFeedback = findViewById(R.id.btnGiveFeedback);
        btnLogoutProfile = findViewById(R.id.btnLogoutProfile);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        btnBack.setOnClickListener(v -> finish());
        bottomNavigationView.setSelectedItemId(R.id.nav_user_profile);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_user_profile) {
                return true;
            } else if (id == R.id.nav_user_home) {
                startActivity(new Intent(this, UserDashboardActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_user_tests) {
                startActivity(new Intent(this, UserTestListActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_user_appointments) {
                startActivity(new Intent(this, UserAppointmentListActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_user_reports) {
                startActivity(new Intent(this, UserReportListActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private void loadProfileData() {
        int userId = sessionManager.getUserId();
        currentUser = dbHelper.getUserById(userId);

        if (currentUser != null) {
            tvProfileName.setText(currentUser.getName());
            tvProfileEmail.setText(currentUser.getEmail());
            etEditName.setText(currentUser.getName());
            etEditPhone.setText(currentUser.getPhone());
            etEditAddress.setText(currentUser.getAddress() != null ? currentUser.getAddress() : "");
        } else {
            tvProfileName.setText(sessionManager.getUsername());
            tvProfileEmail.setText(sessionManager.getUserEmail());
        }
    }

    private void setupListeners() {
        btnSaveProfile.setOnClickListener(v -> saveProfile());

        btnViewPayments.setOnClickListener(v -> {
            startActivity(new Intent(this, PaymentHistoryActivity.class));
        });

        btnGiveFeedback.setOnClickListener(v -> {
            startActivity(new Intent(this, FeedbackActivity.class));
        });

        btnLogoutProfile.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to sign out?")
                    .setPositiveButton("Logout", (dialog, which) -> {
                        sessionManager.logoutUser();
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void saveProfile() {
        tilEditName.setError(null);
        tilEditPhone.setError(null);

        String name = etEditName.getText() != null ? etEditName.getText().toString().trim() : "";
        String phone = etEditPhone.getText() != null ? etEditPhone.getText().toString().trim() : "";
        String address = etEditAddress.getText() != null ? etEditAddress.getText().toString().trim() : "";

        if (TextUtils.isEmpty(name)) {
            tilEditName.setError("Name cannot be blank");
            return;
        }

        if (TextUtils.isEmpty(phone) || phone.length() < 10) {
            tilEditPhone.setError("Enter a valid 10-digit phone number");
            return;
        }

        boolean success = dbHelper.updateUserProfile(sessionManager.getUserId(), name, phone, address);
        if (success) {
            sessionManager.updateUserInfo(name, phone);
            tvProfileName.setText(name);
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }
}
