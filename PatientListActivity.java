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
import com.example.medilab.adapters.PatientAdapter;
import com.example.medilab.database.DatabaseHelper;
import com.example.medilab.models.Patient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;

/**
 * Activity displaying the searchable list of registered Patients.
 * Supports CRUD actions: Create (FAB), Read/Search, Update, and Delete.
 */
public class PatientListActivity extends AppCompatActivity implements PatientAdapter.OnPatientActionListener {

    private RecyclerView rvPatients;
    private PatientAdapter adapter;
    private DatabaseHelper dbHelper;
    private ArrayList<Patient> patientList = new ArrayList<>();

    private EditText etSearch;
    private ImageButton btnClearSearch;
    private TextView tvTotalBadge;
    private LinearLayout layoutEmpty;
    private ExtendedFloatingActionButton fabAdd;
    private MaterialButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        dbHelper = new DatabaseHelper(this);

        initViews();
        setupRecyclerView();
        setupSearch();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPatients();
    }

    private void initViews() {
        rvPatients = findViewById(R.id.rv_patients);
        etSearch = findViewById(R.id.et_search_patient);
        btnClearSearch = findViewById(R.id.btn_clear_search_patient);
        tvTotalBadge = findViewById(R.id.tv_patient_total_badge);
        layoutEmpty = findViewById(R.id.layout_empty_patients);
        fabAdd = findViewById(R.id.fab_add_patient);
        btnBack = findViewById(R.id.btn_back_patients);
    }

    private void setupRecyclerView() {
        rvPatients.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PatientAdapter(this, patientList, this);
        rvPatients.setAdapter(adapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                btnClearSearch.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                filterPatients(query);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnClearSearch.setOnClickListener(v -> etSearch.setText(""));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        fabAdd.setOnClickListener(v -> startActivity(new Intent(this, AddPatientActivity.class)));
    }

    private void loadPatients() {
        String currentQuery = etSearch.getText().toString().trim();
        filterPatients(currentQuery);
    }

    private void filterPatients(String query) {
        if (query.isEmpty()) {
            patientList = dbHelper.getAllPatients();
        } else {
            patientList = dbHelper.searchPatients(query);
        }

        adapter.updateList(patientList);
        tvTotalBadge.setText(patientList.size() + " Total");

        if (patientList.isEmpty()) {
            rvPatients.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvPatients.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewPatient(Patient patient) {
        Intent intent = new Intent(this, PatientDetailsActivity.class);
        intent.putExtra("patient_id", patient.getId());
        startActivity(intent);
    }

    @Override
    public void onEditPatient(Patient patient) {
        Intent intent = new Intent(this, EditPatientActivity.class);
        intent.putExtra("patient_id", patient.getId());
        startActivity(intent);
    }

    @Override
    public void onDeletePatient(Patient patient) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Patient Confirmation")
                .setMessage("Are you sure you want to delete patient " + patient.getName() + " (ID #" + patient.getId() + ")?\n\nWarning: Associated lab reports will also be removed.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    int rows = dbHelper.deletePatient(patient.getId());
                    if (rows > 0) {
                        Toast.makeText(this, getString(R.string.msg_patient_deleted), Toast.LENGTH_SHORT).show();
                        loadPatients();
                    } else {
                        Toast.makeText(this, "Failed to delete patient.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
