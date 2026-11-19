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
import com.example.medilab.models.MedicalTest;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class UserTestDetailsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvDetailCategory, tvDetailTestName, tvDetailPrice, tvDetailDescription, tvDetailNormalRange;
    private MaterialButton btnBookThisTest;

    private DatabaseHelper dbHelper;
    private int testId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_test_details);

        dbHelper = new DatabaseHelper(this);
        testId = getIntent().getIntExtra("test_id", -1);

        initViews();
        loadTestDetails();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        tvDetailTestName = findViewById(R.id.tvDetailTestName);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        tvDetailNormalRange = findViewById(R.id.tvDetailNormalRange);
        btnBookThisTest = findViewById(R.id.btnBookThisTest);

        btnBack.setOnClickListener(v -> finish());

        btnBookThisTest.setOnClickListener(v -> {
            if (testId != -1) {
                Intent intent = new Intent(this, BookTestActivity.class);
                intent.putExtra("selected_test_id", testId);
                startActivity(intent);
            }
        });
    }

    private void loadTestDetails() {
        if (testId == -1) {
            Toast.makeText(this, "Invalid test ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        MedicalTest test = dbHelper.getMedicalTest(testId);
        if (test == null) {
            Toast.makeText(this, "Medical test not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvDetailCategory.setText(test.getCategory());
        tvDetailTestName.setText(test.getTestName());
        tvDetailPrice.setText(String.format(Locale.getDefault(), "₹ %.2f", test.getPrice()));
        tvDetailDescription.setText(test.getDescription() != null && !test.getDescription().isEmpty()
                ? test.getDescription() : "Standard diagnostic procedure conducted under clinical laboratory parameters.");
        tvDetailNormalRange.setText(test.getNormalRange() != null && !test.getNormalRange().isEmpty()
                ? test.getNormalRange() : "Reference ranges are established based on age, gender and pathology guidelines.");
    }
}
