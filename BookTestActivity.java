package com.example.medilab.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.R;
import com.example.medilab.database.DatabaseHelper;
import com.example.medilab.models.MedicalTest;
import com.example.medilab.models.Patient;
import com.example.medilab.models.User;
import com.example.medilab.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class BookTestActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private Spinner spinnerTest;
    private TextView tvPricePreview;
    private TextInputEditText etPatientName, etPatientPhone, etAppointmentDate, etAppointmentTime, etCollectionAddress;
    private TextInputLayout tilPatientName, tilPatientPhone, tilDate, tilTime, tilAddress;
    private RadioGroup rgCollectionType;
    private RadioButton rbHomeCollection, rbLabVisit;
    private MaterialButton btnProceedToPayment;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private ArrayList<MedicalTest> testList;
    private MedicalTest selectedTest;
    private int preSelectedTestId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_test);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        preSelectedTestId = getIntent().getIntExtra("selected_test_id", -1);

        initViews();
        loadTestsSpinner();
        prefillUserInfo();
        setupDateTimePickers();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        spinnerTest = findViewById(R.id.spinnerTest);
        tvPricePreview = findViewById(R.id.tvPricePreview);

        etPatientName = findViewById(R.id.etPatientName);
        etPatientPhone = findViewById(R.id.etPatientPhone);
        etAppointmentDate = findViewById(R.id.etAppointmentDate);
        etAppointmentTime = findViewById(R.id.etAppointmentTime);
        etCollectionAddress = findViewById(R.id.etCollectionAddress);

        tilPatientName = findViewById(R.id.tilPatientName);
        tilPatientPhone = findViewById(R.id.tilPatientPhone);
        tilDate = findViewById(R.id.tilDate);
        tilTime = findViewById(R.id.tilTime);
        tilAddress = findViewById(R.id.tilAddress);

        rgCollectionType = findViewById(R.id.rgCollectionType);
        rbHomeCollection = findViewById(R.id.rbHomeCollection);
        rbLabVisit = findViewById(R.id.rbLabVisit);
        btnProceedToPayment = findViewById(R.id.btnProceedToPayment);

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadTestsSpinner() {
        testList = dbHelper.getAllMedicalTests();
        if (testList.isEmpty()) {
            Toast.makeText(this, "No medical tests available for booking", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ArrayList<String> displayList = new ArrayList<>();
        int selectIndex = 0;
        for (int i = 0; i < testList.size(); i++) {
            MedicalTest t = testList.get(i);
            displayList.add(t.getTestName() + " (₹ " + String.format(Locale.getDefault(), "%.0f", t.getPrice()) + ")");
            if (t.getTestId() == preSelectedTestId) {
                selectIndex = i;
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, displayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTest.setAdapter(adapter);
        spinnerTest.setSelection(selectIndex);
        selectedTest = testList.get(selectIndex);
        tvPricePreview.setText(String.format(Locale.getDefault(), "₹ %.2f", selectedTest.getPrice()));

        spinnerTest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTest = testList.get(position);
                tvPricePreview.setText(String.format(Locale.getDefault(), "₹ %.2f", selectedTest.getPrice()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void prefillUserInfo() {
        int userId = sessionManager.getUserId();
        User user = dbHelper.getUserById(userId);
        if (user != null) {
            etPatientName.setText(user.getName());
            etPatientPhone.setText(user.getPhone());
            if (user.getAddress() != null && !user.getAddress().isEmpty()) {
                etCollectionAddress.setText(user.getAddress());
            }
        }

        // Set default appointment date to tomorrow
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        String tomorrow = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
        etAppointmentDate.setText(tomorrow);
        etAppointmentTime.setText("09:30 AM");
    }

    private void setupDateTimePickers() {
        etAppointmentDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(BookTestActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String formatted = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                                selectedYear, selectedMonth + 1, selectedDay);
                        etAppointmentDate.setText(formatted);
                    }, year, month, day);
            datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePicker.show();
        });

        etAppointmentTime.setOnClickListener(v -> {
            TimePickerDialog timePicker = new TimePickerDialog(BookTestActivity.this,
                    (view, hourOfDay, minute) -> {
                        String amPm = hourOfDay >= 12 ? "PM" : "AM";
                        int displayHour = hourOfDay > 12 ? hourOfDay - 12 : (hourOfDay == 0 ? 12 : hourOfDay);
                        String formatted = String.format(Locale.getDefault(), "%02d:%02d %s", displayHour, minute, amPm);
                        etAppointmentTime.setText(formatted);
                    }, 9, 30, false);
            timePicker.show();
        });
    }

    private void setupListeners() {
        rgCollectionType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbLabVisit) {
                tilAddress.setHint("Notes for Lab Visit (Optional)");
                etCollectionAddress.setText("MEDILAB Central Diagnostics Center, Main Branch");
            } else {
                tilAddress.setHint("Sample Collection Address *");
                User user = dbHelper.getUserById(sessionManager.getUserId());
                etCollectionAddress.setText(user != null ? user.getAddress() : "");
            }
        });

        btnProceedToPayment.setOnClickListener(v -> validateAndProceed());
    }

    private void validateAndProceed() {
        tilPatientName.setError(null);
        tilPatientPhone.setError(null);
        tilDate.setError(null);
        tilTime.setError(null);
        tilAddress.setError(null);

        String name = etPatientName.getText() != null ? etPatientName.getText().toString().trim() : "";
        String phone = etPatientPhone.getText() != null ? etPatientPhone.getText().toString().trim() : "";
        String date = etAppointmentDate.getText() != null ? etAppointmentDate.getText().toString().trim() : "";
        String time = etAppointmentTime.getText() != null ? etAppointmentTime.getText().toString().trim() : "";
        String address = etCollectionAddress.getText() != null ? etCollectionAddress.getText().toString().trim() : "";
        String collectionType = rbHomeCollection.isChecked() ? "Home Sample Collection" : "Lab Visit";

        if (TextUtils.isEmpty(name)) {
            tilPatientName.setError("Please enter patient name");
            etPatientName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone) || phone.length() < 10) {
            tilPatientPhone.setError("Please enter valid 10-digit phone number");
            etPatientPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(date)) {
            tilDate.setError("Please select appointment date");
            return;
        }

        if (TextUtils.isEmpty(time)) {
            tilTime.setError("Please select time slot");
            return;
        }

        if (rbHomeCollection.isChecked() && TextUtils.isEmpty(address)) {
            tilAddress.setError("Please enter address for sample collection");
            etCollectionAddress.requestFocus();
            return;
        }

        if (selectedTest == null) {
            Toast.makeText(this, "Please select a medical test", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check or create patient in database
        Patient patient = dbHelper.getPatientByPhone(phone);
        int patientId;
        if (patient == null) {
            Patient p = new Patient(name, 30, "Other", phone, sessionManager.getUserEmail(), address, "Unknown");
            patientId = (int) dbHelper.addPatient(p);
        } else {
            patientId = patient.getPatientId();
        }

        // Launch PaymentActivity with booking intent bundle
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("user_id", sessionManager.getUserId());
        intent.putExtra("patient_id", patientId);
        intent.putExtra("patient_name", name);
        intent.putExtra("patient_phone", phone);
        intent.putExtra("test_id", selectedTest.getTestId());
        intent.putExtra("test_name", selectedTest.getTestName());
        intent.putExtra("test_price", selectedTest.getPrice());
        intent.putExtra("appointment_date", date);
        intent.putExtra("appointment_time", time);
        intent.putExtra("collection_type", collectionType);
        intent.putExtra("address", address);
        startActivity(intent);
        finish();
    }
}
