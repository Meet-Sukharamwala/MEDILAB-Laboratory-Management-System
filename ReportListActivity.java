package com.example.medilab.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.example.medilab.utils.PdfGenerator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.io.File;
import java.util.ArrayList;

/**
 * Activity for displaying and searching generated Laboratory Diagnostic Reports.
 * Provides actions to View, Edit, Delete, or export Reports to PDF format.
 */
public class ReportListActivity extends AppCompatActivity implements LabReportAdapter.OnReportActionListener {

    private RecyclerView rvReports;
    private LabReportAdapter adapter;
    private DatabaseHelper dbHelper;
    private ArrayList<LabReport> reportList = new ArrayList<>();

    private EditText etSearch;
    private ImageButton btnClearSearch;
    private TextView tvTotalBadge;
    private LinearLayout layoutEmpty;
    private ExtendedFloatingActionButton fabAdd;
    private MaterialButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);

        dbHelper = new DatabaseHelper(this);

        initViews();
        setupRecyclerView();
        setupSearch();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReports();
    }

    private void initViews() {
        rvReports = findViewById(R.id.rv_reports);
        etSearch = findViewById(R.id.et_search_report);
        btnClearSearch = findViewById(R.id.btn_clear_search_report);
        tvTotalBadge = findViewById(R.id.tv_reports_total_badge);
        layoutEmpty = findViewById(R.id.layout_empty_reports);
        fabAdd = findViewById(R.id.fab_generate_report);
        btnBack = findViewById(R.id.btn_back_reports);
    }

    private void setupRecyclerView() {
        rvReports.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LabReportAdapter(this, reportList, this);
        rvReports.setAdapter(adapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                btnClearSearch.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                filterReports(query);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnClearSearch.setOnClickListener(v -> etSearch.setText(""));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        fabAdd.setOnClickListener(v -> startActivity(new Intent(this, GenerateReportActivity.class)));
    }

    private void loadReports() {
        String query = etSearch.getText().toString().trim();
        filterReports(query);
    }

    private void filterReports(String query) {
        if (query.isEmpty()) {
            reportList = dbHelper.getAllReports();
        } else {
            reportList = dbHelper.searchReports(query);
        }

        adapter.updateList(reportList);
        tvTotalBadge.setText(reportList.size() + " Reports");

        if (reportList.isEmpty()) {
            rvReports.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvReports.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
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
                .setTitle("Delete Report Confirmation")
                .setMessage("Are you sure you want to permanently delete Report #" + report.getReportId() + " for " + report.getPatientName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    int rows = dbHelper.deleteReport(report.getReportId());
                    if (rows > 0) {
                        Toast.makeText(this, getString(R.string.msg_report_deleted), Toast.LENGTH_SHORT).show();
                        loadReports();
                    } else {
                        Toast.makeText(this, "Failed to delete report.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onGeneratePdf(LabReport report) {
        File pdf = PdfGenerator.generateLabReportPdf(this, report);
        if (pdf != null) {
            Toast.makeText(this, "PDF generated: " + pdf.getName(), Toast.LENGTH_SHORT).show();
            PdfGenerator.openPdfFile(this, pdf);
        } else {
            Toast.makeText(this, "Error generating PDF report.", Toast.LENGTH_SHORT).show();
        }
    }
}
