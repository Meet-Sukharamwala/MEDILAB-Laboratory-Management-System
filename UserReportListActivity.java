package com.example.medilab.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.medilab.adapters.UserReportAdapter;
import com.example.medilab.database.DatabaseHelper;
import com.example.medilab.models.LabReport;
import com.example.medilab.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class UserReportListActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextInputEditText etSearchReports;
    private RecyclerView rvReports;
    private TextView tvEmptyReports;
    private BottomNavigationView bottomNavigationView;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private UserReportAdapter adapter;

    private ArrayList<LabReport> allUserReports = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_report_list);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupBottomNavigation();
        setupSearch();
        loadReports();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReports();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etSearchReports = findViewById(R.id.etSearchReports);
        rvReports = findViewById(R.id.rvReports);
        tvEmptyReports = findViewById(R.id.tvEmptyReports);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        rvReports.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserReportAdapter(this, new ArrayList<>());
        rvReports.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
        bottomNavigationView.setSelectedItemId(R.id.nav_user_reports);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_user_reports) {
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
            } else if (id == R.id.nav_user_profile) {
                startActivity(new Intent(this, UserProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private void setupSearch() {
        etSearchReports.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterReports(s != null ? s.toString().trim() : "");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadReports() {
        int userId = sessionManager.getUserId();
        String phone = sessionManager.getUserPhone();
        String email = sessionManager.getUserEmail();

        allUserReports = dbHelper.getReportsForUser(userId, phone, email);
        filterReports(etSearchReports.getText() != null ? etSearchReports.getText().toString().trim() : "");
    }

    private void filterReports(String query) {
        if (query.isEmpty()) {
            adapter.updateList(allUserReports);
            updateEmptyState(allUserReports.isEmpty());
            return;
        }

        ArrayList<LabReport> filtered = new ArrayList<>();
        String lower = query.toLowerCase(java.util.Locale.getDefault());
        for (LabReport r : allUserReports) {
            boolean matchTest = r.getTestName() != null && r.getTestName().toLowerCase(java.util.Locale.getDefault()).contains(lower);
            boolean matchCat = r.getTestCategory() != null && r.getTestCategory().toLowerCase(java.util.Locale.getDefault()).contains(lower);
            boolean matchRemark = r.getRemarks() != null && r.getRemarks().toLowerCase(java.util.Locale.getDefault()).contains(lower);
            boolean matchResult = r.getResult() != null && r.getResult().toLowerCase(java.util.Locale.getDefault()).contains(lower);
            if (matchTest || matchCat || matchRemark || matchResult) {
                filtered.add(r);
            }
        }

        adapter.updateList(filtered);
        updateEmptyState(filtered.isEmpty());
    }

    private void updateEmptyState(boolean isEmpty) {
        if (isEmpty) {
            rvReports.setVisibility(View.GONE);
            tvEmptyReports.setVisibility(View.VISIBLE);
        } else {
            rvReports.setVisibility(View.VISIBLE);
            tvEmptyReports.setVisibility(View.GONE);
        }
    }
}
