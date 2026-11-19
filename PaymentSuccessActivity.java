package com.example.medilab.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.R;
import com.example.medilab.database.DatabaseHelper;
import com.example.medilab.models.Payment;
import com.example.medilab.utils.PdfGenerator;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.util.Locale;

public class PaymentSuccessActivity extends AppCompatActivity {

    private TextView tvReceiptAptId, tvReceiptTxnId, tvReceiptTest, tvReceiptPatient, tvReceiptSchedule, tvReceiptMethod, tvReceiptAmount;
    private MaterialButton btnDownloadInvoice, btnGoDashboard;

    private int appointmentId;
    private String txnId;
    private String method;
    private double amount;
    private String testName;
    private String patientName;
    private String appointmentDate;
    private String appointmentTime;
    private String paymentStatus;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        dbHelper = new DatabaseHelper(this);

        extractIntentData();
        initViews();
        setupListeners();
    }

    private void extractIntentData() {
        Intent intent = getIntent();
        appointmentId = intent.getIntExtra("appointment_id", 0);
        txnId = intent.getStringExtra("transaction_id");
        method = intent.getStringExtra("payment_method");
        amount = intent.getDoubleExtra("amount", 0.0);
        testName = intent.getStringExtra("test_name");
        patientName = intent.getStringExtra("patient_name");
        appointmentDate = intent.getStringExtra("appointment_date");
        appointmentTime = intent.getStringExtra("appointment_time");
        paymentStatus = intent.getStringExtra("payment_status");
    }

    private void initViews() {
        tvReceiptAptId = findViewById(R.id.tvReceiptAptId);
        tvReceiptTxnId = findViewById(R.id.tvReceiptTxnId);
        tvReceiptTest = findViewById(R.id.tvReceiptTest);
        tvReceiptPatient = findViewById(R.id.tvReceiptPatient);
        tvReceiptSchedule = findViewById(R.id.tvReceiptSchedule);
        tvReceiptMethod = findViewById(R.id.tvReceiptMethod);
        tvReceiptAmount = findViewById(R.id.tvReceiptAmount);

        btnDownloadInvoice = findViewById(R.id.btnDownloadInvoice);
        btnGoDashboard = findViewById(R.id.btnGoDashboard);

        tvReceiptAptId.setText("#" + appointmentId);
        tvReceiptTxnId.setText(txnId);
        tvReceiptTest.setText(testName);
        tvReceiptPatient.setText(patientName);
        tvReceiptSchedule.setText(appointmentDate + " at " + appointmentTime);
        tvReceiptMethod.setText(method + " (" + paymentStatus + ")");
        tvReceiptAmount.setText(String.format(Locale.getDefault(), "₹ %.2f", amount));
    }

    private void setupListeners() {
        btnDownloadInvoice.setOnClickListener(v -> {
            Payment pay = dbHelper.getPaymentByTransactionId(txnId);
            if (pay == null) {
                pay = new Payment(0, 0, appointmentId, amount, method, txnId, appointmentDate, paymentStatus);
            }
            File pdf = PdfGenerator.generatePaymentInvoicePdf(this, pay, patientName, testName, appointmentDate);
            if (pdf != null) {
                PdfGenerator.openPdfFile(this, pdf);
            } else {
                Toast.makeText(this, "Failed to export invoice PDF", Toast.LENGTH_SHORT).show();
            }
        });

        btnGoDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
