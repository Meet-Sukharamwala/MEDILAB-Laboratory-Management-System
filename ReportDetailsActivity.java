package com.example.medilab.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.R;
import com.example.medilab.database.DatabaseHelper;
import com.example.medilab.models.LabReport;
import com.example.medilab.utils.PdfGenerator;
import com.google.android.material.button.MaterialButton;

import java.io.File;

/**
 * Activity presenting the comprehensive Diagnostic Report details
 * along with direct options to Export PDF, Edit, or Delete the report.
 */
public class ReportDetailsActivity extends AppCompatActivity {

    private TextView tvReportId, tvDate, tvPatientName, tvBlood, tvAgeGender, tvAddress;
    private TextView tvTestName, tvPrice, tvCategory, tvResult, tvNormalRange, tvRemarks;
    private MaterialButton btnBack, btnTopPdf, btnTopEdit, btnActionPdf, btnActionDelete;

    private DatabaseHelper dbHelper;
    private int reportId = -1;
    private LabReport report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);

        dbHelper = new DatabaseHelper(this);
        reportId = getIntent().getIntExtra("report_id", -1);

        if (reportId == -1) {
            Toast.makeText(this, "Error: Invalid report ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReportData();
    }

    private void initViews() {
        tvReportId = findViewById(R.id.tv_det_rep_id);
        tvDate = findViewById(R.id.tv_det_rep_date);
        tvPatientName = findViewById(R.id.tv_det_rep_patient_name);
        tvBlood = findViewById(R.id.tv_det_rep_blood);
        tvAgeGender = findViewById(R.id.tv_det_rep_age_gender);
        tvAddress = findViewById(R.id.tv_det_rep_address);

        tvTestName = findViewById(R.id.tv_det_rep_test_name);
        tvPrice = findViewById(R.id.tv_det_rep_price);
        tvCategory = findViewById(R.id.tv_det_rep_category);
        tvResult = findViewById(R.id.tv_det_rep_result);
        tvNormalRange = findViewById(R.id.tv_det_rep_normal_range);
        tvRemarks = findViewById(R.id.tv_det_rep_remarks);

        btnBack = findViewById(R.id.btn_back_details_report);
        btnTopPdf = findViewById(R.id.btn_details_pdf);
        btnTopEdit = findViewById(R.id.btn_details_edit_report);
        btnActionPdf = findViewById(R.id.btn_action_generate_pdf);
        btnActionDelete = findViewById(R.id.btn_action_delete_report);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnTopEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditReportActivity.class);
            intent.putExtra("report_id", reportId);
            startActivity(intent);
        });

        btnTopPdf.setOnClickListener(v -> generatePdfAndOpen());
        btnActionPdf.setOnClickListener(v -> generatePdfAndOpen());

        btnActionDelete.setOnClickListener(v -> confirmDeleteReport());
    }

    private void loadReportData() {
        report = dbHelper.getReportById(reportId);
        if (report == null) {
            Toast.makeText(this, "Report not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvReportId.setText("REPORT #" + report.getReportId());
        tvDate.setText("Date: " + report.getTestDate());
        tvPatientName.setText(report.getPatientName());
        tvBlood.setText(report.getPatientBloodGroup() != null && !report.getPatientBloodGroup().isEmpty()
                ? report.getPatientBloodGroup() : "N/A");

        String ageGender = report.getPatientAge() + " yrs • " + report.getPatientGender()
                + " • Phone: " + report.getPatientPhone();
        tvAgeGender.setText(ageGender);

        String addr = report.getPatientAddress() != null && !report.getPatientAddress().isEmpty()
                ? "Address: " + report.getPatientAddress() : "Address: N/A";
        tvAddress.setText(addr);

        tvTestName.setText(report.getTestName());
        tvPrice.setText(String.format(java.util.Locale.getDefault(), "₹ %.2f", report.getTestPrice()));
        tvCategory.setText("Category: " + report.getTestCategory());

        tvResult.setText(report.getResult());
        tvNormalRange.setText(report.getTestNormalRange() != null && !report.getTestNormalRange().isEmpty()
                ? report.getTestNormalRange() : "Clinical standard reference range");

        tvRemarks.setText(report.getRemarks() != null && !report.getRemarks().isEmpty()
                ? report.getRemarks() : "Clinically correlate findings. Issued for diagnostic assistance.");
    }

    private void generatePdfAndOpen() {
        if (report == null) return;
        File pdf = PdfGenerator.generateLabReportPdf(this, report);
        if (pdf != null) {
            Toast.makeText(this, "PDF generated: " + pdf.getName(), Toast.LENGTH_SHORT).show();
            PdfGenerator.openPdfFile(this, pdf);
        } else {
            Toast.makeText(this, "Failed to generate PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDeleteReport() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Report Confirmation")
                .setMessage("Are you sure you want to delete Diagnostic Report #" + report.getReportId() + "? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    int rows = dbHelper.deleteReport(reportId);
                    if (rows > 0) {
                        Toast.makeText(this, "Report deleted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
