package com.example.medilab.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.R;
import com.example.medilab.database.DatabaseHelper;
import com.example.medilab.models.MedicalTest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Activity for creating a new Medical Test entry in SQLite.
 * Validates test name, category, pricing, and biological reference ranges.
 */
public class AddMedicalTestActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilPrice, tilNormalRange, tilDescription;
    private TextInputEditText etName, etPrice, etNormalRange, etDescription;
    private Spinner spinnerCategory;
    private MaterialButton btnSave, btnClear, btnBack;

    private DatabaseHelper dbHelper;

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
        setContentView(R.layout.activity_add_medical_test);

        dbHelper = new DatabaseHelper(this);

        initViews();
        setupCategorySpinner();
        setupListeners();
    }

    private void initViews() {
        tilName = findViewById(R.id.til_test_name);
        tilPrice = findViewById(R.id.til_test_price);
        tilNormalRange = findViewById(R.id.til_normal_range);
        tilDescription = findViewById(R.id.til_test_description);

        etName = findViewById(R.id.et_test_name);
        etPrice = findViewById(R.id.et_test_price);
        etNormalRange = findViewById(R.id.et_normal_range);
        etDescription = findViewById(R.id.et_test_description);

        spinnerCategory = findViewById(R.id.spinner_test_category);

        btnSave = findViewById(R.id.btn_save_test);
        btnClear = findViewById(R.id.btn_clear_test);
        btnBack = findViewById(R.id.btn_back_add_test);
    }

    private void setupCategorySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, CATEGORIES);
        spinnerCategory.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnClear.setOnClickListener(v -> clearForm());
        btnSave.setOnClickListener(v -> saveMedicalTest());
    }

    private void clearForm() {
        etName.setText("");
        etPrice.setText("");
        etNormalRange.setText("");
        etDescription.setText("");
        spinnerCategory.setSelection(0);

        tilName.setError(null);
        tilPrice.setError(null);
    }

    private void saveMedicalTest() {
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

        MedicalTest test = new MedicalTest(name, category, price, normalRange, desc);
        long id = dbHelper.addMedicalTest(test);

        if (id != -1) {
            Toast.makeText(this, "Medical Test #" + id + " saved successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save test", Toast.LENGTH_SHORT).show();
        }
    }
}
