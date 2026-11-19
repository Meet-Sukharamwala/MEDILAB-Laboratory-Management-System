package com.example.medilab.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.R;
import com.example.medilab.database.DatabaseHelper;
import com.example.medilab.models.LabReport;
import com.example.medilab.models.MedicalTest;
import com.example.medilab.models.Patient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Activity for modifying an existing Laboratory Diagnostic Report in SQLite.
 * Demonstrates UPDATE operation with foreign key relationships intact.
 */
public class EditReportActivity extends AppCompatActivity {

    private Spinner spinnerPatient, spinnerTest;
    private TextView tvEditingBadge;
    private TextInputLayout tilDate, tilResult, tilRemarks;
    private TextInputEditText etDate, etResult, etRemarks;
    private MaterialButton btnUpdate, btnBack;

    private DatabaseHelper dbHelper;
    private int reportId = -1;
    private LabReport currentReport;

    private ArrayList<Patient> patientList = new ArrayList<>();
    private ArrayList<MedicalTest> testList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_report);

        dbHelper = new DatabaseHelper(this);
        reportId = getIntent().getIntExtra("report_id", -1);

        if (reportId == -1) {
            Toast.makeText(this, "Error: Invalid Report ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupDatePicker();
        loadReportData();
        setupListeners();
    }

    private void initViews() {
        spinnerPatient = findViewById(R.id.spinner_edit_patient);
        spinnerTest = findViewById(R.id.spinner_edit_test);
        tvEditingBadge = findViewById(R.id.tv_editing_report_badge);

        tilDate = findViewById(R.id.til_edit_test_date);
        tilResult = findViewById(R.id.til_edit_report_result);
        tilRemarks = findViewById(R.id.til_edit_report_remarks);

        etDate = findViewById(R.id.et_edit_test_date);
        etResult = findViewById(R.id.et_edit_report_result);
        etRemarks = findViewById(R.id.et_edit_report_remarks);

        btnUpdate = findViewById(R.id.btn_update_report);
        btnBack = findViewById(R.id.btn_back_edit_report);
    }

    private void setupDatePicker() {
        View.OnClickListener dateClickListener = v -> {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpd = new DatePickerDialog(EditReportActivity.this,
                    (view, y, m, d) -> {
                        String formatted = String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m + 1, d);
                        etDate.setText(formatted);
                    }, year, month, day);
            dpd.show();
        };

        etDate.setOnClickListener(dateClickListener);
        tilDate.setOnClickListener(dateClickListener);
    }

    private void loadReportData() {
        currentReport = dbHelper.getReportById(reportId);
        if (currentReport == null) {
            Toast.makeText(this, "Report not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvEditingBadge.setText("Editing Report #" + currentReport.getReportId());
        etDate.setText(currentReport.getTestDate());
        etResult.setText(currentReport.getResult());
        etRemarks.setText(currentReport.getRemarks());

        patientList = dbHelper.getAllPatients();
        testList = dbHelper.getAllMedicalTests();

        // Patients Spinner
        ArrayList<String> patientLabels = new ArrayList<>();
        int selectedPatientPos = 0;
        for (int i = 0; i < patientList.size(); i++) {
            Patient p = patientList.get(i);
            patientLabels.add("#" + p.getId() + " - " + p.getName() + " (" + p.getBloodGroup() + ")");
            if (p.getId() == currentReport.getPatientId()) {
                selectedPatientPos = i;
            }
        }
        ArrayAdapter<String> pAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, patientLabels);
        spinnerPatient.setAdapter(pAdapter);
        spinnerPatient.setSelection(selectedPatientPos);

        // Tests Spinner
        ArrayList<String> testLabels = new ArrayList<>();
        int selectedTestPos = 0;
        for (int i = 0; i < testList.size(); i++) {
            MedicalTest t = testList.get(i);
            testLabels.add("#" + t.getId() + " - " + t.getTestName() + " (₹" + (int) t.getPrice() + ")");
            if (t.getId() == currentReport.getTestId()) {
                selectedTestPos = i;
            }
        }
        ArrayAdapter<String> tAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, testLabels);
        spinnerTest.setAdapter(tAdapter);
        spinnerTest.setSelection(selectedTestPos);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnUpdate.setOnClickListener(v -> updateReport());
    }

    private void updateReport() {
        tilDate.setError(null);
        tilResult.setError(null);

        int patientPos = spinnerPatient.getSelectedItemPosition();
        int testPos = spinnerTest.getSelectedItemPosition();

        if (patientPos < 0 || patientPos >= patientList.size()) {
            Toast.makeText(this, getString(R.string.error_select_patient), Toast.LENGTH_SHORT).show();
            return;
        }

        if (testPos < 0 || testPos >= testList.size()) {
            Toast.makeText(this, getString(R.string.error_select_test), Toast.LENGTH_SHORT).show();
            return;
        }

        String date = etDate.getText() != null ? etDate.getText().toString().trim() : "";
        String result = etResult.getText() != null ? etResult.getText().toString().trim() : "";
        String remarks = etRemarks.getText() != null ? etRemarks.getText().toString().trim() : "";

        if (TextUtils.isEmpty(date)) {
            tilDate.setError(getString(R.string.error_empty_date));
            return;
        }

        if (TextUtils.isEmpty(result)) {
            tilResult.setError(getString(R.string.error_empty_result));
            etResult.requestFocus();
            return;
        }

        Patient selectedPatient = patientList.get(patientPos);
        MedicalTest selectedTest = testList.get(testPos);

        LabReport updated = new LabReport(reportId, selectedPatient.getId(), selectedTest.getId(), date, result, remarks);
        int rows = dbHelper.updateReport(updated);

        if (rows > 0) {
            Toast.makeText(this, getString(R.string.msg_report_updated), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update report.", Toast.LENGTH_SHORT).show();
        }
    }
}
