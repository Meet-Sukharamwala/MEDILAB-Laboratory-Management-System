package com.example.medilab.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.R;
import com.example.medilab.database.DatabaseHelper;
import com.example.medilab.models.LabReport;
import com.example.medilab.models.MedicalTest;
import com.example.medilab.models.Patient;
import com.example.medilab.utils.PdfGenerator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Activity for generating a new Diagnostic Lab Report in MEDILAB.
 * Connects a selected Patient, selected Medical Test, Date, Findings and Remarks.
 */
public class GenerateReportActivity extends AppCompatActivity {

    private Spinner spinnerPatient, spinnerTest;
    private TextView tvPatientHint, tvTestHint;
    private TextInputLayout tilDate, tilResult, tilRemarks;
    private TextInputEditText etDate, etResult, etRemarks;
    private MaterialButton btnSave, btnClear, btnBack;

    private DatabaseHelper dbHelper;
    private ArrayList<Patient> patientList = new ArrayList<>();
    private ArrayList<MedicalTest> testList = new ArrayList<>();

    private int preSelectedPatientId = -1;
    private int preSelectedTestId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_report);

        dbHelper = new DatabaseHelper(this);
        preSelectedPatientId = getIntent().getIntExtra("selected_patient_id", -1);
        preSelectedTestId = getIntent().getIntExtra("selected_test_id", -1);

        initViews();
        setupDatePicker();
        loadSpinners();
        setupListeners();
    }

    private void initViews() {
        spinnerPatient = findViewById(R.id.spinner_select_patient);
        spinnerTest = findViewById(R.id.spinner_select_test);
        tvPatientHint = findViewById(R.id.tv_patient_brief_hint);
        tvTestHint = findViewById(R.id.tv_test_brief_hint);

        tilDate = findViewById(R.id.til_test_date);
        tilResult = findViewById(R.id.til_report_result);
        tilRemarks = findViewById(R.id.til_report_remarks);

        etDate = findViewById(R.id.et_test_date);
        etResult = findViewById(R.id.et_report_result);
        etRemarks = findViewById(R.id.et_report_remarks);

        btnSave = findViewById(R.id.btn_save_report);
        btnClear = findViewById(R.id.btn_clear_report);
        btnBack = findViewById(R.id.btn_back_generate_report);

        // Pre-fill today's date
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        etDate.setText(today);
    }

    private void setupDatePicker() {
        View.OnClickListener dateClickListener = v -> {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpd = new DatePickerDialog(GenerateReportActivity.this,
                    (view, y, m, d) -> {
                        String formatted = String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m + 1, d);
                        etDate.setText(formatted);
                    }, year, month, day);
            dpd.show();
        };

        etDate.setOnClickListener(dateClickListener);
        tilDate.setOnClickListener(dateClickListener);
    }

    private void loadSpinners() {
        patientList = dbHelper.getAllPatients();
        testList = dbHelper.getAllMedicalTests();

        if (patientList.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("No Patients Registered")
                    .setMessage("Please register at least one patient before generating a diagnostic lab report.")
                    .setPositiveButton("Add Patient", (d, w) -> {
                        startActivity(new Intent(this, AddPatientActivity.class));
                        finish();
                    })
                    .setNegativeButton("Cancel", (d, w) -> finish())
                    .setCancelable(false)
                    .show();
            return;
        }

        if (testList.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("No Medical Tests Cataloged")
                    .setMessage("Please add at least one test to the laboratory catalog before generating a report.")
                    .setPositiveButton("Add Test", (d, w) -> {
                        startActivity(new Intent(this, AddMedicalTestActivity.class));
                        finish();
                    })
                    .setNegativeButton("Cancel", (d, w) -> finish())
                    .setCancelable(false)
                    .show();
            return;
        }

        // Build Patient strings
        ArrayList<String> patientLabels = new ArrayList<>();
        int preSelectPatientIdx = 0;
        for (int i = 0; i < patientList.size(); i++) {
            Patient p = patientList.get(i);
            patientLabels.add("#" + p.getId() + " - " + p.getName() + " (" + p.getBloodGroup() + ")");
            if (p.getId() == preSelectedPatientId) {
                preSelectPatientIdx = i;
            }
        }
        ArrayAdapter<String> pAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, patientLabels);
        spinnerPatient.setAdapter(pAdapter);
        spinnerPatient.setSelection(preSelectPatientIdx);

        // Build Test strings
        ArrayList<String> testLabels = new ArrayList<>();
        int preSelectTestIdx = 0;
        for (int i = 0; i < testList.size(); i++) {
            MedicalTest t = testList.get(i);
            testLabels.add("#" + t.getId() + " - " + t.getTestName() + " (₹" + (int) t.getPrice() + ")");
            if (t.getId() == preSelectedTestId) {
                preSelectTestIdx = i;
            }
        }
        ArrayAdapter<String> tAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, testLabels);
        spinnerTest.setAdapter(tAdapter);
        spinnerTest.setSelection(preSelectTestIdx);

        // Spinner item select listeners to update hint texts
        spinnerPatient.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Patient p = patientList.get(position);
                tvPatientHint.setText("Age: " + p.getAge() + " yrs • " + p.getGender() + " • Phone: " + p.getPhone());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerTest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MedicalTest t = testList.get(position);
                tvTestHint.setText("Category: " + t.getCategory() + " • Ref: " + t.getNormalRange());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnClear.setOnClickListener(v -> clearForm());
        btnSave.setOnClickListener(v -> saveReport());
    }

    private void clearForm() {
        etResult.setText("");
        etRemarks.setText("");
        tilResult.setError(null);
        tilDate.setError(null);
        if (!patientList.isEmpty()) spinnerPatient.setSelection(0);
        if (!testList.isEmpty()) spinnerTest.setSelection(0);
    }

    private void saveReport() {
        tilDate.setError(null);
        tilResult.setError(null);

        if (patientList.isEmpty() || testList.isEmpty()) {
            Toast.makeText(this, "Patient or test data missing", Toast.LENGTH_SHORT).show();
            return;
        }

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

        LabReport report = new LabReport(selectedPatient.getId(), selectedTest.getId(), date, result, remarks);
        long reportId = dbHelper.addReport(report);

        if (reportId != -1) {
            report.setReportId((int) reportId);
            report.setPatientName(selectedPatient.getName());
            report.setPatientAge(selectedPatient.getAge());
            report.setPatientGender(selectedPatient.getGender());
            report.setPatientPhone(selectedPatient.getPhone());
            report.setPatientBloodGroup(selectedPatient.getBloodGroup());
            report.setPatientAddress(selectedPatient.getAddress());
            report.setTestName(selectedTest.getTestName());
            report.setTestCategory(selectedTest.getCategory());
            report.setTestPrice(selectedTest.getPrice());
            report.setTestNormalRange(selectedTest.getNormalRange());

            showSuccessDialog(report);
        } else {
            Toast.makeText(this, "Failed to save lab report.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSuccessDialog(LabReport report) {
        new AlertDialog.Builder(this)
                .setTitle("Report Generated!")
                .setMessage("Laboratory Report #" + report.getReportId() + " successfully registered for " + report.getPatientName() + ".")
                .setPositiveButton("View Report", (dialog, which) -> {
                    Intent intent = new Intent(this, ReportDetailsActivity.class);
                    intent.putExtra("report_id", report.getReportId());
                    startActivity(intent);
                    finish();
                })
                .setNeutralButton("Export PDF", (dialog, which) -> {
                    File pdf = PdfGenerator.generateLabReportPdf(this, report);
                    if (pdf != null) {
                        PdfGenerator.openPdfFile(this, pdf);
                    }
                    finish();
                })
                .setNegativeButton("Done", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }
}
