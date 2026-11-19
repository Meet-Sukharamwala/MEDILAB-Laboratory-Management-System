package com.example.medilab.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.R;
import com.example.medilab.database.DatabaseHelper;
import com.example.medilab.models.MedicalTest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Activity for modifying existing Medical Test record in SQLite.
 * Demonstrates UPDATE operation on test catalog.
 */
public class EditMedicalTestActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilPrice, tilNormalRange, tilDescription;
    private TextInputEditText etName, etPrice, etNormalRange, etDescription;
    private Spinner spinnerCategory;
    private TextView tvEditingBadge;
    private MaterialButton btnUpdate, btnBack;

    private DatabaseHelper dbHelper;
    private int testId = -1;
    private MedicalTest currentTest;

    private static final String[] CATEGORIES = {
            "Hematology",
            "Biochemistry",
            "Immunology",
            "Microbiology",
            "Pathology",
            "Endocrinology",
            "Serology",
            "General"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_medical_test);

        dbHelper = new DatabaseHelper(this);
        testId = getIntent().getIntExtra("test_id", -1);

        if (testId == -1) {
            Toast.makeText(this, "Error: Invalid test ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupCategorySpinner();
        loadTestData();
        setupListeners();
    }

    private void initViews() {
        tilName = findViewById(R.id.til_edit_test_name);
        tilPrice = findViewById(R.id.til_edit_test_price);
        tilNormalRange = findViewById(R.id.til_edit_normal_range);
        tilDescription = findViewById(R.id.til_edit_test_description);

        etName = findViewById(R.id.et_edit_test_name);
        etPrice = findViewById(R.id.et_edit_test_price);
        etNormalRange = findViewById(R.id.et_edit_normal_range);
        etDescription = findViewById(R.id.et_edit_test_description);

        spinnerCategory = findViewById(R.id.spinner_edit_test_category);
        tvEditingBadge = findViewById(R.id.tv_editing_test_badge);

        btnUpdate = findViewById(R.id.btn_update_test);
        btnBack = findViewById(R.id.btn_back_edit_test);
    }

    private void setupCategorySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, CATEGORIES);
        spinnerCategory.setAdapter(adapter);
    }

    private void loadTestData() {
        currentTest = dbHelper.getMedicalTestById(testId);
        if (currentTest == null) {
            Toast.makeText(this, "Medical Test not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvEditingBadge.setText("Editing Test #" + currentTest.getId());
        etName.setText(currentTest.getTestName());
        etPrice.setText(String.format(java.util.Locale.getDefault(), "%.2f", currentTest.getPrice()));
        etNormalRange.setText(currentTest.getNormalRange());
        etDescription.setText(currentTest.getDescription());

        for (int i = 0; i < CATEGORIES.length; i++) {
            if (CATEGORIES[i].equalsIgnoreCase(currentTest.getCategory())) {
                spinnerCategory.setSelection(i);
                break;
            }
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnUpdate.setOnClickListener(v -> updateMedicalTest());
    }

    private void updateMedicalTest() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String priceStr = etPrice.getText() != null ? etPrice.getText().toString().trim() : "";
        String normalRange = etNormalRange.getText() != null ? etNormalRange.getText().toString().trim() : "";
        String desc = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
        String category = spinnerCategory.getSelectedItem().toString();

        tilName.setError(null);
        tilPrice.setError(null);

        if (TextUtils.isEmpty(name)) {
            tilName.setError(getString(R.string.error_invalid_test_name));
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(priceStr)) {
            tilPrice.setError(getString(R.string.error_invalid_price));
            etPrice.requestFocus();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price < 0) {
                tilPrice.setError(getString(R.string.error_invalid_price));
                return;
            }
        } catch (NumberFormatException e) {
            tilPrice.setError(getString(R.string.error_invalid_price));
            return;
        }

        MedicalTest updated = new MedicalTest(testId, name, category, price, normalRange, desc);
        int rows = dbHelper.updateMedicalTest(updated);

        if (rows > 0) {
            Toast.makeText(this, getString(R.string.msg_test_updated), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update test.", Toast.LENGTH_SHORT).show();
        }
    }
}
