package com.example.medilab.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.R;
import com.example.medilab.database.DatabaseHelper;
import com.example.medilab.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

/**
 * Modern Laboratory Dashboard for MEDILAB.
 * Displays real-time SQLite statistical counters and central navigation modules.
 */
public class DashboardActivity extends AppCompatActivity {

    private TextView tvGreeting, tvCountPatients, tvCountTests, tvCountReports, tvCountToday;
    private MaterialCardView cardPatients, cardTests, cardReports, cardToday;
    private MaterialCardView btnGenerateReport, btnPatients, btnTests, btnReports;
    private MaterialButton btnLogoutTop, btnLogoutBottom, btnSeedData;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDashboardStatistics();
    }

    private void initViews() {
        tvGreeting = findViewById(R.id.tv_welcome_greeting);
        tvCountPatients = findViewById(R.id.tv_count_patients);
        tvCountTests = findViewById(R.id.tv_count_tests);
        tvCountReports = findViewById(R.id.tv_count_reports);
        tvCountToday = findViewById(R.id.tv_count_today);

        cardPatients = findViewById(R.id.card_stat_patients);
        cardTests = findViewById(R.id.card_stat_tests);
        cardReports = findViewById(R.id.card_stat_reports);
        cardToday = findViewById(R.id.card_stat_today);

        btnGenerateReport = findViewById(R.id.btn_dash_generate_report);
        btnPatients = findViewById(R.id.btn_dash_patients);
        btnTests = findViewById(R.id.btn_dash_tests);
        btnReports = findViewById(R.id.btn_dash_reports);

        btnLogoutTop = findViewById(R.id.btn_dashboard_logout);
        btnLogoutBottom = findViewById(R.id.btn_dash_logout_bottom);
        btnSeedData = findViewById(R.id.btn_dash_seed);

        tvGreeting.setText("Welcome, " + sessionManager.getUsername());
    }

    private void refreshDashboardStatistics() {
        int patientCount = dbHelper.getPatientCount();
        int testCount = dbHelper.getTestCount();
        int reportCount = dbHelper.getReportCount();
        int todayCount = dbHelper.getTodayReportCount();

        tvCountPatients.setText(String.valueOf(patientCount));
        tvCountTests.setText(String.valueOf(testCount));
        tvCountReports.setText(String.valueOf(reportCount));
        tvCountToday.setText(String.valueOf(todayCount));
    }

    private void setupListeners() {
        // Module 1: Patients
        cardPatients.setOnClickListener(v -> startActivity(new Intent(this, PatientListActivity.class)));
        btnPatients.setOnClickListener(v -> startActivity(new Intent(this, PatientListActivity.class)));

        // Module 2: Medical Tests
        cardTests.setOnClickListener(v -> startActivity(new Intent(this, MedicalTestListActivity.class)));
        btnTests.setOnClickListener(v -> startActivity(new Intent(this, MedicalTestListActivity.class)));

        // Module 3: Lab Reports
        cardReports.setOnClickListener(v -> startActivity(new Intent(this, ReportListActivity.class)));
        cardToday.setOnClickListener(v -> startActivity(new Intent(this, ReportListActivity.class)));
        btnReports.setOnClickListener(v -> startActivity(new Intent(this, ReportListActivity.class)));

        // Generate Report
        btnGenerateReport.setOnClickListener(v -> startActivity(new Intent(this, GenerateReportActivity.class)));

        // Load Sample Data
        btnSeedData.setOnClickListener(v -> {
            dbHelper.seedSampleDataIfEmpty();
            refreshDashboardStatistics();
            Toast.makeText(this, "Sample BCA demo data verified and ready!", Toast.LENGTH_SHORT).show();
        });

        // Logout
        btnLogoutTop.setOnClickListener(v -> confirmLogout());
        btnLogoutBottom.setOnClickListener(v -> confirmLogout());
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout Confirmation")
                .setMessage("Are you sure you want to end your session and log out of MEDILAB?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    sessionManager.logoutUser();
                    Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
