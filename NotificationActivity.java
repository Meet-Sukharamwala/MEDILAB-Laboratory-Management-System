package com.example.medilab.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.medilab.adapters.NotificationAdapter;
import com.example.medilab.database.DatabaseHelper;
import com.example.medilab.models.NotificationItem;
import com.example.medilab.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private MaterialButton btnMarkAllRead;
    private RecyclerView rvNotifications;
    private TextView tvEmptyNotifications;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        initViews();
        loadNotifications();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnMarkAllRead = findViewById(R.id.btnMarkAllRead);
        rvNotifications = findViewById(R.id.rvNotifications);
        tvEmptyNotifications = findViewById(R.id.tvEmptyNotifications);

        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(this, new ArrayList<>());
        rvNotifications.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        btnMarkAllRead.setOnClickListener(v -> {
            int userId = sessionManager.getUserId();
            dbHelper.markNotificationsAsRead(userId);
            Toast.makeText(this, "All notifications marked as read", Toast.LENGTH_SHORT).show();
            loadNotifications();
        });
    }

    private void loadNotifications() {
        int userId = sessionManager.getUserId();
        ArrayList<NotificationItem> list = dbHelper.getNotificationsByUser(userId);
        adapter.updateList(list);

        if (list.isEmpty()) {
            rvNotifications.setVisibility(View.GONE);
            tvEmptyNotifications.setVisibility(View.VISIBLE);
        } else {
            rvNotifications.setVisibility(View.VISIBLE);
            tvEmptyNotifications.setVisibility(View.GONE);
        }
    }
}
