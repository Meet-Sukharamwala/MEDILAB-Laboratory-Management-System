package com.example.medilab.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.medilab.adapters.LabReportAdapter;
import com.example.medilab.database.DatabaseHelper;
import com.example.medilab.models.LabReport;
import com.example.medilab.models.Patient;
import com.example.medilab.utils.PdfGenerator;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.util.ArrayList;

/**
 * Activity presenting the complete patient dossier, including their history
 * of laboratory diagnostic reports.
 */
public class PatientDetailsActivity extends AppCompatActivity implements LabReportAdapter.OnReportActionListener {

    private TextView tvId, tvBlood, tvName, tvAgeGender, tvPhone, tvEmail, tvAddress, tvReportsSummary;
    private MaterialButton btnBack, btnEdit, btnGenerateReport, btnDelete;
    private RecyclerView rvReports;

    private DatabaseHelper dbHelper;
    private int patientId = -1;
    private Patient patient;
    private LabReportAdapter reportAdapter;
    private ArrayList<LabReport> patientReports = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);

        dbHelper = new DatabaseHelper(this);
        patientId = getIntent().getIntExtra("patient_id", -1);

        if (patientId == -1) {
            Toast.makeText(this, "Error: Invalid patient ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPatientData();
    }

    private void initViews() {
        tvId = findViewById(R.id.tv_det_patient_id);
        tvBlood = findViewById(R.id.tv_det_blood_group);
        tvName = findViewById(R.id.tv_det_patient_name);
        tvAgeGender = findViewById(R.id.tv_det_age_gender);
        tvPhone = findViewById(R.id.tv_det_phone);
        tvEmail = findViewById(R.id.tv_det_email);
        tvAddress = findViewById(R.id.tv_det_address);
        tvReportsSummary = findViewById(R.id.tv_det_reports_summary);

        btnBack = findViewById(R.id.btn_back_details_patient);
        btnEdit = findViewById(R.id.btn_details_edit_patient);
        btnGenerateReport = findViewById(R.id.btn_det_generate_report);
        btnDelete = findViewById(R.id.btn_det_delete_patient);
        rvReports = findViewById(R.id.rv_det_patient_reports);
    }

    private void setupRecyclerView() {
        rvReports.setLayoutManager(new LinearLayoutManager(this));
        reportAdapter = new LabReportAdapter(this, patientReports, this);
        rvReports.setAdapter(reportAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditPatientActivity.class);
            intent.putExtra("patient_id", patientId);
            startActivity(intent);
        });

        btnGenerateReport.setOnClickListener(v -> {
            Intent intent = new Intent(this, GenerateReportActivity.class);
            intent.putExtra("selected_patient_id", patientId);
            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> confirmDeletePatient());
    }

    private void loadPatientData() {
        patient = dbHelper.getPatientById(patientId);
        if (patient == null) {
            Toast.makeText(this, "Patient not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvId.setText("ID #" + patient.getId());
        tvBlood.setText(patient.getBloodGroup() != null ? patient.getBloodGroup() : "N/A");
        tvName.setText(patient.getName());
        tvAgeGender.setText(patient.getAge() + " Years • " + (patient.getGender() != null ? patient.getGender() : "N/A"));
        tvPhone.setText(patient.getPhone() != null ? patient.getPhone() : "N/A");
        tvEmail.setText(patient.getEmail() != null && !patient.getEmail().isEmpty() ? patient.getEmail() : "N/A");
        tvAddress.setText(patient.getAddress() != null && !patient.getAddress().isEmpty() ? patient.getAddress() : "N/A");

        patientReports = dbHelper.getReportsByPatientId(patientId);
        reportAdapter.updateList(patientReports);
        tvReportsSummary.setText(patientReports.size() + " Reports on file");
    }

    private void confirmDeletePatient() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Patient")
                .setMessage("Are you sure you want to delete " + patient.getName() + "? All associated diagnostic reports will also be deleted.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    int rows = dbHelper.deletePatient(patientId);
                    if (rows > 0) {
                        Toast.makeText(this, "Patient deleted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onViewReport(LabReport report) {
        Intent intent = new Intent(this, ReportDetailsActivity.class);
        intent.putExtra("report_id", report.getReportId());
        startActivity(intent);
    }

    @Override
    public void onEditReport(LabReport report) {
        Intent intent = new Intent(this, EditReportActivity.class);
        intent.putExtra("report_id", report.getReportId());
        startActivity(intent);
    }

    @Override
    public void onDeleteReport(LabReport report) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Report")
                .setMessage("Are you sure you want to delete Report #" + report.getReportId() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteReport(report.getReportId());
                    loadPatientData();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onGeneratePdf(LabReport report) {
        File pdf = PdfGenerator.generateLabReportPdf(this, report);
        if (pdf != null) {
            PdfGenerator.openPdfFile(this, pdf);
        }
    }
}
