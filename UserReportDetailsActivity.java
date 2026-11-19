package com.example.medilab.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.R;
import com.example.medilab.database.DatabaseHelper;
import com.example.medilab.models.LabReport;
import com.example.medilab.utils.PdfGenerator;
import com.example.medilab.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.io.File;

public class UserReportDetailsActivity extends AppCompatActivity {

    private ImageButton btnBack, btnExportPdfTop;
    private TextView tvReportBadge, tvReportDate, tvReportTestName, tvReportId;
    private TextView tvReportNormalRange, tvReportResults, tvReportRemarks;
    private MaterialButton btnExportPdf;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private LabReport currentReport;
    private int reportId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_report_details);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        reportId = getIntent().getIntExtra("report_id", -1);

        initViews();
        loadReportDetails();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnExportPdfTop = findViewById(R.id.btnExportPdfTop);
        tvReportBadge = findViewById(R.id.tvReportBadge);
        tvReportDate = findViewById(R.id.tvReportDate);
        tvReportTestName = findViewById(R.id.tvReportTestName);
        tvReportId = findViewById(R.id.tvReportId);
        tvReportNormalRange = findViewById(R.id.tvReportNormalRange);
        tvReportResults = findViewById(R.id.tvReportResults);
        tvReportRemarks = findViewById(R.id.tvReportRemarks);
        btnExportPdf = findViewById(R.id.btnExportPdf);

        btnBack.setOnClickListener(v -> finish());

        View.OnClickListener exportListener = v -> {
            if (currentReport != null) {
                File pdf = PdfGenerator.generateLabReportPdf(this, currentReport);
                if (pdf != null) {
                    PdfGenerator.openPdfFile(this, pdf);
                } else {
                    Toast.makeText(this, "Failed to generate PDF report", Toast.LENGTH_SHORT).show();
                }
            }
        };

        btnExportPdfTop.setOnClickListener(exportListener);
        btnExportPdf.setOnClickListener(exportListener);
    }

    private void loadReportDetails() {
        if (reportId == -1) {
            Toast.makeText(this, "Invalid report ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Privacy check: Patient can only view their own reports
        int userId = sessionManager.getUserId();
        String phone = sessionManager.getUserPhone();
        String email = sessionManager.getUserEmail();

        if (!dbHelper.isReportOwnedByUser(reportId, userId, phone, email)) {
            Toast.makeText(this, "Unauthorized access: You can only view your own lab reports.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        currentReport = dbHelper.getLabReport(reportId);
        if (currentReport == null) {
            Toast.makeText(this, "Report record not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvReportBadge.setText(currentReport.getTestCategory() != null ? currentReport.getTestCategory() : "General");
        tvReportDate.setText("Date: " + currentReport.getTestDate());
        tvReportTestName.setText(currentReport.getTestName());
        tvReportId.setText("Report Ref: #" + currentReport.getReportId() + " • Patient: " + currentReport.getPatientName());
        tvReportNormalRange.setText(currentReport.getNormalRange() != null && !currentReport.getNormalRange().isEmpty()
                ? currentReport.getNormalRange() : "Reference values according to standard laboratory protocols.");
        tvReportResults.setText(currentReport.getResult() != null && !currentReport.getResult().isEmpty()
                ? currentReport.getResult() : "Findings pending validation.");
        tvReportRemarks.setText(currentReport.getRemarks() != null && !currentReport.getRemarks().isEmpty()
                ? currentReport.getRemarks() : "Results verified and authorized by clinical pathologist.");
    }
}
