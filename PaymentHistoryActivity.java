package com.example.medilab.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.medilab.adapters.PaymentAdapter;
import com.example.medilab.database.DatabaseHelper;
import com.example.medilab.models.Payment;
import com.example.medilab.utils.SessionManager;

import java.util.ArrayList;

public class PaymentHistoryActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private RecyclerView rvPayments;
    private TextView tvEmptyPayments;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private PaymentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        initViews();
        loadPayments();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        rvPayments = findViewById(R.id.rvPayments);
        tvEmptyPayments = findViewById(R.id.tvEmptyPayments);

        rvPayments.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PaymentAdapter(this, new ArrayList<>(), sessionManager.getUsername());
        rvPayments.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadPayments() {
        int userId = sessionManager.getUserId();
        ArrayList<Payment> payments = dbHelper.getPaymentsByUser(userId);
        adapter.updateList(payments);

        if (payments.isEmpty()) {
            rvPayments.setVisibility(View.GONE);
            tvEmptyPayments.setVisibility(View.VISIBLE);
        } else {
            rvPayments.setVisibility(View.VISIBLE);
            tvEmptyPayments.setVisibility(View.GONE);
        }
    }
}
