package com.example.medilab.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.R;
import com.example.medilab.database.DatabaseHelper;
import com.example.medilab.models.Patient;
import com.example.medilab.models.User;
import com.example.medilab.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Dual-role login activity for MEDILAB.
 * Allows both Staff/Admin login and Patient login on the same screen.
 * If user does not exist, prompts and redirects them to the registration page.
 */
public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private TextInputLayout tilUsername;
    private TextInputLayout tilPassword;
    private MaterialButton btnLogin;
    private MaterialButton btnQuickFill;
    private MaterialButton btnRegisterRedirect;
    private MaterialButtonToggleGroup toggleRoleGroup;
    private Button btnRolePatient;
    private Button btnRoleStaff;
    private TextView tvLoginCardTitle;
    private TextView tvLoginCardSubtitle;

    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    private boolean isStaffMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);

        // Pre-populate sample records for testing
        databaseHelper.seedSampleDataIfEmpty();

        // Check if session is already active
        if (sessionManager.isLoggedIn()) {
            navigateLoggedInUser();
            return;
        }

        setContentView(R.layout.activity_login);
        initViews();
        setupRoleToggle();
        setupListeners();
    }

    private void navigateLoggedInUser() {
        if (sessionManager.isAdmin()) {
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
            startActivity(intent);
        }
        finish();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnQuickFill = findViewById(R.id.btnQuickFill);
        btnRegisterRedirect = findViewById(R.id.btnRegisterRedirect);
        toggleRoleGroup = findViewById(R.id.toggleRoleGroup);
        btnRolePatient = findViewById(R.id.btnRolePatient);
        btnRoleStaff = findViewById(R.id.btnRoleStaff);
        tvLoginCardTitle = findViewById(R.id.tvLoginCardTitle);
        tvLoginCardSubtitle = findViewById(R.id.tvLoginCardSubtitle);

        // Default to Patient mode
        toggleRoleGroup.check(R.id.btnRolePatient);
        updateRoleUI(false);
    }

    private void setupRoleToggle() {
        toggleRoleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnRoleStaff) {
                    updateRoleUI(true);
                } else {
                    updateRoleUI(false);
                }
            }
        });
    }

    private void updateRoleUI(boolean staff) {
        isStaffMode = staff;
        tilUsername.setError(null);
        tilPassword.setError(null);

        if (staff) {
            tvLoginCardTitle.setText("Staff & Laboratory Login");
            tvLoginCardSubtitle.setText("Authorized medical staff and administrators only.");
            tilUsername.setHint("Staff Username");
            btnQuickFill.setVisibility(View.VISIBLE);
            btnRegisterRedirect.setVisibility(View.GONE);
            btnLogin.setBackgroundColor(getResources().getColor(R.color.navy_primary));
        } else {
            tvLoginCardTitle.setText("Patient Sign In");
            tvLoginCardSubtitle.setText("Access your test bookings, lab reports, and payment receipts.");
            tilUsername.setHint("Email or Mobile Number");
            btnQuickFill.setVisibility(View.GONE);
            btnRegisterRedirect.setVisibility(View.VISIBLE);
            btnLogin.setBackgroundColor(getResources().getColor(R.color.teal_accent));
        }
    }

    private void setupListeners() {
        btnQuickFill.setOnClickListener(v -> {
            etUsername.setText("admin");
            etPassword.setText("admin123");
            tilUsername.setError(null);
            tilPassword.setError(null);
        });

        btnRegisterRedirect.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        tilUsername.setError(null);
        tilPassword.setError(null);

        String usernameOrEmail = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(usernameOrEmail)) {
            tilUsername.setError(isStaffMode ? "Please enter staff username" : "Please enter email or mobile");
            etUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError(getString(R.string.error_empty_password));
            etPassword.requestFocus();
            return;
        }

        if (isStaffMode) {
            // Staff / Admin Login
            if ("admin".equalsIgnoreCase(usernameOrEmail) && "admin123".equals(password)) {
                sessionManager.createLoginSession("Administrator");
                Toast.makeText(this, "Staff login successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            } else {
                tilPassword.setError("Invalid admin credentials (demo: admin / admin123)");
            }
        } else {
            // Patient / User Login
            boolean userExists = databaseHelper.checkUserExists(usernameOrEmail);
            if (!userExists) {
                // User does not exist prompt
                showUserNotExistsDialog(usernameOrEmail);
                return;
            }

            User user = databaseHelper.authenticateUser(usernameOrEmail, password);
            if (user != null) {
                // Find associated patient ID if present
                Patient p = databaseHelper.getPatientByPhone(user.getPhone());
                int patientId = p != null ? p.getPatientId() : 0;

                sessionManager.createUserSession(user.getUserId(), patientId, user.getName(), user.getEmail(), user.getPhone());
                Toast.makeText(this, "Welcome back, " + user.getName() + "!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
                startActivity(intent);
                finish();
            } else {
                tilPassword.setError("Incorrect password. Please try again.");
            }
        }
    }

    private void showUserNotExistsDialog(String inputAccount) {
        new AlertDialog.Builder(this)
                .setTitle("Account Not Found")
                .setMessage("No patient account found for \"" + inputAccount + "\". Would you like to create a new patient account now?")
                .setPositiveButton("Register", (dialog, which) -> {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
