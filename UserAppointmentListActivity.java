package com.example.medilab.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.medilab.adapters.AppointmentAdapter;
import com.example.medilab.database.DatabaseHelper;
import com.example.medilab.models.Appointment;
import com.example.medilab.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class UserAppointmentListActivity extends AppCompatActivity implements AppointmentAdapter.OnAppointmentActionListener {

    private ImageButton btnBack;
    private MaterialButton btnNewBooking;
    private ChipGroup chipGroupAptStatus;
    private RecyclerView rvAppointments;
    private TextView tvEmptyAppointments;
    private BottomNavigationView bottomNavigationView;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private AppointmentAdapter adapter;

    private String currentFilter = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_appointment_list);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupBottomNavigation();
        setupFilterChips();
        loadAppointments();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAppointments();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnNewBooking = findViewById(R.id.btnNewBooking);
        chipGroupAptStatus = findViewById(R.id.chipGroupAptStatus);
        rvAppointments = findViewById(R.id.rvAppointments);
        tvEmptyAppointments = findViewById(R.id.tvEmptyAppointments);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        rvAppointments.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppointmentAdapter(this, new ArrayList<>(), this);
        rvAppointments.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
        btnNewBooking.setOnClickListener(v -> startActivity(new Intent(this, BookTestActivity.class)));
        bottomNavigationView.setSelectedItemId(R.id.nav_user_appointments);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_user_appointments) {
                return true;
            } else if (id == R.id.nav_user_home) {
                startActivity(new Intent(this, UserDashboardActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_user_tests) {
                startActivity(new Intent(this, UserTestListActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_user_reports) {
                startActivity(new Intent(this, UserReportListActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_user_profile) {
                startActivity(new Intent(this, UserProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private void setupFilterChips() {
        chipGroupAptStatus.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentFilter = "All";
            } else {
                int checkedId = checkedIds.get(0);
                Chip chip = findViewById(checkedId);
                currentFilter = chip != null ? chip.getText().toString() : "All";
            }
            loadAppointments();
        });
    }

    private void loadAppointments() {
        int userId = sessionManager.getUserId();
        ArrayList<Appointment> list = dbHelper.getAppointmentsByUser(userId, currentFilter);
        adapter.updateList(list);

        if (list.isEmpty()) {
            rvAppointments.setVisibility(View.GONE);
            tvEmptyAppointments.setVisibility(View.VISIBLE);
        } else {
            rvAppointments.setVisibility(View.VISIBLE);
            tvEmptyAppointments.setVisibility(View.GONE);
        }
    }

    @Override
    public void onReschedule(Appointment appointment) {
        // Date & Time picker for rescheduling
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String newDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);

            TimePickerDialog timePicker = new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                String amPm = hourOfDay >= 12 ? "PM" : "AM";
                int displayHour = hourOfDay > 12 ? hourOfDay - 12 : (hourOfDay == 0 ? 12 : hourOfDay);
                String newTime = String.format(Locale.getDefault(), "%02d:%02d %s", displayHour, minute, amPm);

                // Update appointment in database
                boolean success = dbHelper.rescheduleAppointment(appointment.getAppointmentId(), newDate, newTime) > 0;
                if (success) {
                    Toast.makeText(this, "Appointment rescheduled to " + newDate + " at " + newTime, Toast.LENGTH_SHORT).show();
                    loadAppointments();
                } else {
                    Toast.makeText(this, "Failed to reschedule appointment", Toast.LENGTH_SHORT).show();
                }
            }, 10, 0, false);
            timePicker.show();
        }, year, month, day);

        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePicker.show();
    }

    @Override
    public void onCancel(Appointment appointment) {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Appointment")
                .setMessage("Are you sure you want to cancel Appointment #" + appointment.getAppointmentId() + " (" + appointment.getTestName() + ")?")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    boolean success = dbHelper.cancelAppointment(appointment.getAppointmentId()) > 0;
                    if (success) {
                        Toast.makeText(this, "Appointment cancelled", Toast.LENGTH_SHORT).show();
                        loadAppointments();
                    } else {
                        Toast.makeText(this, "Failed to cancel appointment", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
