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
import com.example.medilab.adapters.MedicalTestAdapter;
import com.example.medilab.database.DatabaseHelper;
import com.example.medilab.models.MedicalTest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;

/**
 * Activity presenting the catalog of Medical Laboratory Tests.
 * Demonstrates CRUD (Create, Read, Update, Delete) and search operations.
 */
public class MedicalTestListActivity extends AppCompatActivity implements MedicalTestAdapter.OnTestActionListener {

    private RecyclerView rvTests;
    private MedicalTestAdapter adapter;
    private DatabaseHelper dbHelper;
    private ArrayList<MedicalTest> testList = new ArrayList<>();

    private EditText etSearch;
    private ImageButton btnClearSearch;
    private TextView tvTotalBadge;
    private LinearLayout layoutEmpty;
    private ExtendedFloatingActionButton fabAdd;
    private MaterialButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_test_list);

        dbHelper = new DatabaseHelper(this);

        initViews();
        setupRecyclerView();
        setupSearch();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMedicalTests();
    }

    private void initViews() {
        rvTests = findViewById(R.id.rv_tests);
        etSearch = findViewById(R.id.et_search_test);
        btnClearSearch = findViewById(R.id.btn_clear_search_test);
        tvTotalBadge = findViewById(R.id.tv_test_total_badge);
        layoutEmpty = findViewById(R.id.layout_empty_tests);
        fabAdd = findViewById(R.id.fab_add_test);
        btnBack = findViewById(R.id.btn_back_tests);
    }

    private void setupRecyclerView() {
        rvTests.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MedicalTestAdapter(this, testList, this);
        rvTests.setAdapter(adapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                btnClearSearch.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                filterTests(query);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnClearSearch.setOnClickListener(v -> etSearch.setText(""));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        fabAdd.setOnClickListener(v -> startActivity(new Intent(this, AddMedicalTestActivity.class)));
    }

    private void loadMedicalTests() {
        String query = etSearch.getText().toString().trim();
        filterTests(query);
    }

    private void filterTests(String query) {
        if (query.isEmpty()) {
            testList = dbHelper.getAllMedicalTests();
        } else {
            testList = dbHelper.searchMedicalTests(query);
        }

        adapter.updateList(testList);
        tvTotalBadge.setText(testList.size() + " Tests");

        if (testList.isEmpty()) {
            rvTests.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvTests.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewTest(MedicalTest test) {
        Intent intent = new Intent(this, MedicalTestDetailsActivity.class);
        intent.putExtra("test_id", test.getId());
        startActivity(intent);
    }

    @Override
    public void onEditTest(MedicalTest test) {
        Intent intent = new Intent(this, EditMedicalTestActivity.class);
        intent.putExtra("test_id", test.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteTest(MedicalTest test) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Medical Test")
                .setMessage("Are you sure you want to remove test \"" + test.getTestName() + "\"?\n\nWarning: Associated diagnostic reports will also be deleted.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    int rows = dbHelper.deleteMedicalTest(test.getId());
                    if (rows > 0) {
                        Toast.makeText(this, getString(R.string.msg_test_deleted), Toast.LENGTH_SHORT).show();
                        loadMedicalTests();
                    } else {
                        Toast.makeText(this, "Failed to delete test", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
