package com.example.medilab.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.R;
import com.example.medilab.database.DatabaseHelper;
import com.example.medilab.models.Appointment;
import com.example.medilab.models.Payment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class PaymentActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvSummaryTestName, tvSummaryPatient, tvSummaryDateTime, tvSummaryCollection, tvTotalPayable;
    private RadioGroup rgPaymentMethods;
    private RadioButton rbUPI, rbCard, rbCash;
    private TextInputLayout tilUpiId;
    private MaterialButton btnPayNow;

    private DatabaseHelper dbHelper;

    private int userId;
    private int patientId;
    private String patientName;
    private String patientPhone;
    private int testId;
    private String testName;
    private double testPrice;
    private String appointmentDate;
    private String appointmentTime;
    private String collectionType;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        dbHelper = new DatabaseHelper(this);

        extractIntentData();
        initViews();
        setupListeners();
    }

    private void extractIntentData() {
        Intent intent = getIntent();
        userId = intent.getIntExtra("user_id", 0);
        patientId = intent.getIntExtra("patient_id", 0);
        patientName = intent.getStringExtra("patient_name");
        patientPhone = intent.getStringExtra("patient_phone");
        testId = intent.getIntExtra("test_id", 0);
        testName = intent.getStringExtra("test_name");
        testPrice = intent.getDoubleExtra("test_price", 0.0);
        appointmentDate = intent.getStringExtra("appointment_date");
        appointmentTime = intent.getStringExtra("appointment_time");
        collectionType = intent.getStringExtra("collection_type");
        address = intent.getStringExtra("address");
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvSummaryTestName = findViewById(R.id.tvSummaryTestName);
        tvSummaryPatient = findViewById(R.id.tvSummaryPatient);
        tvSummaryDateTime = findViewById(R.id.tvSummaryDateTime);
        tvSummaryCollection = findViewById(R.id.tvSummaryCollection);
        tvTotalPayable = findViewById(R.id.tvTotalPayable);

        rgPaymentMethods = findViewById(R.id.rgPaymentMethods);
        rbUPI = findViewById(R.id.rbUPI);
        rbCard = findViewById(R.id.rbCard);
        rbCash = findViewById(R.id.rbCash);
        tilUpiId = findViewById(R.id.tilUpiId);
        btnPayNow = findViewById(R.id.btnPayNow);

        btnBack.setOnClickListener(v -> finish());

        // Bind Summary
        tvSummaryTestName.setText(testName);
        tvSummaryPatient.setText("Patient: " + patientName + " (" + patientPhone + ")");
        tvSummaryDateTime.setText("Schedule: " + appointmentDate + " at " + appointmentTime);
        tvSummaryCollection.setText("Mode: " + collectionType);
        tvTotalPayable.setText(String.format(Locale.getDefault(), "₹ %.2f", testPrice));
    }

    private void setupListeners() {
        rgPaymentMethods.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbUPI) {
                tilUpiId.setVisibility(View.VISIBLE);
                btnPayNow.setText("PAY & CONFIRM APPOINTMENT");
            } else if (checkedId == R.id.rbCard) {
                tilUpiId.setVisibility(View.GONE);
                btnPayNow.setText("PAY & CONFIRM APPOINTMENT");
            } else {
                tilUpiId.setVisibility(View.GONE);
                btnPayNow.setText("CONFIRM APPOINTMENT (PAY ON VISIT)");
            }
        });

        btnPayNow.setOnClickListener(v -> processPayment());
    }

    private void processPayment() {
        String method = "UPI";
        if (rbCard.isChecked()) {
            method = "Card";
        } else if (rbCash.isChecked()) {
            method = "Cash on Collection";
        }

        final String selectedMethod = method;

        // Progress dialog simulation
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Processing payment and securing appointment slot…");
        dialog.setCancelable(false);
        dialog.show();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            dialog.dismiss();

            // 1. Create Appointment in SQLite
            Appointment apt = new Appointment(0, userId, patientId, testId,
                    appointmentDate, appointmentTime, collectionType, address, "Confirmed", "");
            long appointmentId = dbHelper.addAppointment(apt);

            if (appointmentId <= 0) {
                Toast.makeText(PaymentActivity.this, "Failed to create appointment record.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Generate Unique Transaction ID
            String datePrefix = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
            int randomSuffix = 1000 + new Random().nextInt(9000);
            String txnId = "TXN" + datePrefix + randomSuffix;

            String today = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
            String paymentStatus = selectedMethod.equalsIgnoreCase("Cash on Collection") ? "Pending" : "Paid";

            // 3. Record Payment in SQLite
            Payment pay = new Payment(0, userId, (int) appointmentId, testPrice,
                    selectedMethod, txnId, today, paymentStatus);
            long payId = dbHelper.addPayment(pay);

            // 4. Navigate to Payment Success Screen
            Intent successIntent = new Intent(PaymentActivity.this, PaymentSuccessActivity.class);
            successIntent.putExtra("appointment_id", (int) appointmentId);
            successIntent.putExtra("transaction_id", txnId);
            successIntent.putExtra("payment_method", selectedMethod);
            successIntent.putExtra("amount", testPrice);
            successIntent.putExtra("test_name", testName);
            successIntent.putExtra("patient_name", patientName);
            successIntent.putExtra("appointment_date", appointmentDate);
            successIntent.putExtra("appointment_time", appointmentTime);
            successIntent.putExtra("payment_status", paymentStatus);
            startActivity(successIntent);
            finish();

        }, 1200);
    }
}
