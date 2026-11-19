package com.example.medilab.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.medilab.adapters.UserReportAdapter;
import com.example.medilab.database.DatabaseHelper;
import com.example.medilab.models.Appointment;
import com.example.medilab.models.LabReport;
import com.example.medilab.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class UserDashboardActivity extends AppCompatActivity {

    private TextView tvUserGreeting;
    private FrameLayout flNotifications;
    private TextView tvNotifBadge;
    private ImageButton btnLogout;

    private MaterialCardView cardUpcomingBanner;
    private TextView tvBannerStatus, tvBannerTestName, tvBannerDateTime, tvBannerCollection;

    private MaterialCardView cardStatReports, cardStatAppointments, cardStatPayments, cardBookTest;
    private TextView tvCountReports, tvCountAppointments, tvCountPayments;
    private MaterialButton btnSubmitFeedback;

    private TextView tvViewAllReports, tvEmptyReportsNotice;
    private RecyclerView rvRecentReports;
    private BottomNavigationView bottomNavigationView;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private UserReportAdapter reportAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn() || sessionManager.isAdmin()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupBottomNavigation();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void initViews() {
        tvUserGreeting = findViewById(R.id.tvUserGreeting);
        flNotifications = findViewById(R.id.flNotifications);
        tvNotifBadge = findViewById(R.id.tvNotifBadge);
        btnLogout = findViewById(R.id.btnLogout);

        cardUpcomingBanner = findViewById(R.id.cardUpcomingBanner);
        tvBannerStatus = findViewById(R.id.tvBannerStatus);
        tvBannerTestName = findViewById(R.id.tvBannerTestName);
        tvBannerDateTime = findViewById(R.id.tvBannerDateTime);
        tvBannerCollection = findViewById(R.id.tvBannerCollection);

        cardStatReports = findViewById(R.id.cardStatReports);
        cardStatAppointments = findViewById(R.id.cardStatAppointments);
        cardStatPayments = findViewById(R.id.cardStatPayments);
        cardBookTest = findViewById(R.id.cardBookTest);

        tvCountReports = findViewById(R.id.tvCountReports);
        tvCountAppointments = findViewById(R.id.tvCountAppointments);
        tvCountPayments = findViewById(R.id.tvCountPayments);
        btnSubmitFeedback = findViewById(R.id.btnSubmitFeedback);

        tvViewAllReports = findViewById(R.id.tvViewAllReports);
        tvEmptyReportsNotice = findViewById(R.id.tvEmptyReportsNotice);
        rvRecentReports = findViewById(R.id.rvRecentReports);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        rvRecentReports.setLayoutManager(new LinearLayoutManager(this));
        reportAdapter = new UserReportAdapter(this, new ArrayList<>());
        rvRecentReports.setAdapter(reportAdapter);

        // Highlight Home in bottom nav
        bottomNavigationView.setSelectedItemId(R.id.nav_user_home);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_user_home) {
                return true;
            } else if (id == R.id.nav_user_tests) {
                startActivity(new Intent(this, UserTestListActivity.class));
                return true;
            } else if (id == R.id.nav_user_appointments) {
                startActivity(new Intent(this, UserAppointmentListActivity.class));
                return true;
            } else if (id == R.id.nav_user_reports) {
                startActivity(new Intent(this, UserReportListActivity.class));
                return true;
            } else if (id == R.id.nav_user_profile) {
                startActivity(new Intent(this, UserProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void setupListeners() {
        flNotifications.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationActivity.class));
        });

        btnLogout.setOnClickListener(v -> confirmLogout());

        cardStatReports.setOnClickListener(v -> {
            startActivity(new Intent(this, UserReportListActivity.class));
        });

        tvViewAllReports.setOnClickListener(v -> {
            startActivity(new Intent(this, UserReportListActivity.class));
        });

        cardStatAppointments.setOnClickListener(v -> {
            startActivity(new Intent(this, UserAppointmentListActivity.class));
        });

        cardStatPayments.setOnClickListener(v -> {
            startActivity(new Intent(this, PaymentHistoryActivity.class));
        });

        cardBookTest.setOnClickListener(v -> {
            startActivity(new Intent(this, BookTestActivity.class));
        });

        btnSubmitFeedback.setOnClickListener(v -> {
            startActivity(new Intent(this, FeedbackActivity.class));
        });

        cardUpcomingBanner.setOnClickListener(v -> {
            startActivity(new Intent(this, UserAppointmentListActivity.class));
        });
    }

    private void loadDashboardData() {
        String username = sessionManager.getUsername();
        tvUserGreeting.setText("Hello, " + username + " 👋");

        int userId = sessionManager.getUserId();
        String phone = sessionManager.getUserPhone();
        String email = sessionManager.getUserEmail();

        // Notification badge
        int unreadNotifs = dbHelper.getUnreadNotificationCount(userId);
        if (unreadNotifs > 0) {
            tvNotifBadge.setVisibility(View.VISIBLE);
            tvNotifBadge.setText(String.valueOf(unreadNotifs));
        } else {
            tvNotifBadge.setVisibility(View.GONE);
        }

        // Stats counts
        ArrayList<LabReport> userReports = dbHelper.getReportsForUser(userId, phone, email);
        int reportCount = userReports.size();
        int aptCount = dbHelper.getUserAppointmentCount(userId);
        int payCount = dbHelper.getUserPaymentCount(userId);

        tvCountReports.setText(String.valueOf(reportCount));
        tvCountAppointments.setText(String.valueOf(aptCount));
        tvCountPayments.setText(String.valueOf(payCount));

        // Upcoming appointment banner
        ArrayList<Appointment> upcomingList = dbHelper.getAppointmentsByUser(userId, "Confirmed");
        if (!upcomingList.isEmpty()) {
            Appointment upcoming = upcomingList.get(0);
            cardUpcomingBanner.setVisibility(View.VISIBLE);
            tvBannerTestName.setText(upcoming.getTestName());
            tvBannerDateTime.setText(upcoming.getAppointmentDate() + " at " + upcoming.getAppointmentTime());
            tvBannerCollection.setText(upcoming.getCollectionType() + (upcoming.getAddress() != null && !upcoming.getAddress().isEmpty() ? " • " + upcoming.getAddress() : ""));
            tvBannerStatus.setText(upcoming.getStatus());
        } else {
            cardUpcomingBanner.setVisibility(View.GONE);
        }

        // Recent Reports
        if (userReports.isEmpty()) {
            rvRecentReports.setVisibility(View.GONE);
            tvEmptyReportsNotice.setVisibility(View.VISIBLE);
        } else {
            rvRecentReports.setVisibility(View.VISIBLE);
            tvEmptyReportsNotice.setVisibility(View.GONE);
            // Limit to top 3 recent reports on dashboard
            ArrayList<LabReport> recent = new ArrayList<>();
            for (int i = 0; i < Math.min(3, userReports.size()); i++) {
                recent.add(userReports.get(i));
            }
            reportAdapter.updateList(recent);
        }
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to sign out of your patient portal?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    sessionManager.logoutUser();
                    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
