package com.example.medilab.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.R;
import com.example.medilab.database.DatabaseHelper;
import com.example.medilab.models.MedicalTest;
import com.google.android.material.button.MaterialButton;

/**
 * Activity for inspecting complete catalog details of a specific medical test.
 */
public class MedicalTestDetailsActivity extends AppCompatActivity {

    private TextView tvId, tvCategory, tvName, tvPrice, tvNormalRange, tvDescription;
    private MaterialButton btnBack, btnEdit, btnGenerateReport, btnDelete;

    private DatabaseHelper dbHelper;
    private int testId = -1;
    private MedicalTest test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_test_details);

        dbHelper = new DatabaseHelper(this);
        testId = getIntent().getIntExtra("test_id", -1);

        if (testId == -1) {
            Toast.makeText(this, "Error: Invalid test ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTestData();
    }

    private void initViews() {
        tvId = findViewById(R.id.tv_det_test_id);
        tvCategory = findViewById(R.id.tv_det_test_category);
        tvName = findViewById(R.id.tv_det_test_name);
        tvPrice = findViewById(R.id.tv_det_test_price);
        tvNormalRange = findViewById(R.id.tv_det_normal_range);
        tvDescription = findViewById(R.id.tv_det_test_description);

        btnBack = findViewById(R.id.btn_back_details_test);
        btnEdit = findViewById(R.id.btn_details_edit_test);
        btnGenerateReport = findViewById(R.id.btn_det_generate_report_test);
        btnDelete = findViewById(R.id.btn_det_delete_test);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditMedicalTestActivity.class);
            intent.putExtra("test_id", testId);
            startActivity(intent);
        });

        btnGenerateReport.setOnClickListener(v -> {
            Intent intent = new Intent(this, GenerateReportActivity.class);
            intent.putExtra("selected_test_id", testId);
            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> confirmDeleteTest());
    }

    private void loadTestData() {
        test = dbHelper.getMedicalTestById(testId);
        if (test == null) {
            Toast.makeText(this, "Medical Test not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvId.setText("TEST ID #" + test.getId());
        tvCategory.setText(test.getCategory() != null ? test.getCategory() : "General");
        tvName.setText(test.getTestName());
        tvPrice.setText(String.format(java.util.Locale.getDefault(), "₹ %.2f", test.getPrice()));
        tvNormalRange.setText(test.getNormalRange() != null ? test.getNormalRange() : "Standard reference range");
        tvDescription.setText(test.getDescription() != null && !test.getDescription().isEmpty()
                ? test.getDescription() : "No clinical description recorded.");
    }

    private void confirmDeleteTest() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Medical Test")
                .setMessage("Are you sure you want to remove \"" + test.getTestName() + "\"? Any generated diagnostic reports for this test will also be deleted.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    int rows = dbHelper.deleteMedicalTest(testId);
                    if (rows > 0) {
                        Toast.makeText(this, "Medical test deleted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
