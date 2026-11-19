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
import com.example.medilab.adapters.UserTestAdapter;
import com.example.medilab.database.DatabaseHelper;
import com.example.medilab.models.MedicalTest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class UserTestListActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextInputEditText etSearchTests;
    private ChipGroup chipGroupCategories;
    private RecyclerView rvTests;
    private TextView tvEmptyTests;
    private BottomNavigationView bottomNavigationView;

    private DatabaseHelper dbHelper;
    private UserTestAdapter testAdapter;

    private String currentCategory = "All";
    private String currentSearchKeyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_test_list);

        dbHelper = new DatabaseHelper(this);

        initViews();
        setupBottomNavigation();
        setupCategoryChips();
        setupSearch();
        loadTests();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etSearchTests = findViewById(R.id.etSearchTests);
        chipGroupCategories = findViewById(R.id.chipGroupCategories);
        rvTests = findViewById(R.id.rvTests);
        tvEmptyTests = findViewById(R.id.tvEmptyTests);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        rvTests.setLayoutManager(new LinearLayoutManager(this));
        testAdapter = new UserTestAdapter(this, new ArrayList<>());
        rvTests.setAdapter(testAdapter);

        btnBack.setOnClickListener(v -> finish());
        bottomNavigationView.setSelectedItemId(R.id.nav_user_tests);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_user_tests) {
                return true;
            } else if (id == R.id.nav_user_home) {
                startActivity(new Intent(this, UserDashboardActivity.class));
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
            } else if (id == R.id.nav_user_profile) {
                startActivity(new Intent(this, UserProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private void setupCategoryChips() {
        chipGroupCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentCategory = "All";
            } else {
                int checkedId = checkedIds.get(0);
                Chip chip = findViewById(checkedId);
                currentCategory = chip != null ? chip.getText().toString() : "All";
            }
            loadTests();
        });
    }

    private void setupSearch() {
        etSearchTests.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchKeyword = s != null ? s.toString().trim() : "";
                loadTests();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadTests() {
        ArrayList<MedicalTest> list = dbHelper.filterMedicalTests(currentCategory, currentSearchKeyword);
        testAdapter.updateList(list);

        if (list.isEmpty()) {
            rvTests.setVisibility(View.GONE);
            tvEmptyTests.setVisibility(View.VISIBLE);
        } else {
            rvTests.setVisibility(View.VISIBLE);
            tvEmptyTests.setVisibility(View.GONE);
        }
    }
}
