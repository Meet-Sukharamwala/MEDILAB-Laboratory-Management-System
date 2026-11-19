package com.example.medilab.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.medilab.models.Appointment;
import com.example.medilab.models.FeedbackItem;
import com.example.medilab.models.LabReport;
import com.example.medilab.models.MedicalTest;
import com.example.medilab.models.NotificationItem;
import com.example.medilab.models.Patient;
import com.example.medilab.models.Payment;
import com.example.medilab.models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * SQLiteOpenHelper implementation for MEDILAB.
 * Manages database creation, table schemas, foreign key constraints,
 * and comprehensive CRUD operations for Patients, Medical Tests, Lab Reports,
 * and the User/Patient side (Users, Appointments, Payments, Notifications, Feedback).
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    // Database information
    public static final String DATABASE_NAME = "medilab.db";
    public static final int DATABASE_VERSION = 2; // Upgraded for user-side tables

    // Table Names
    public static final String TABLE_PATIENTS = "patients";
    public static final String TABLE_MEDICAL_TESTS = "medical_tests";
    public static final String TABLE_LAB_REPORTS = "lab_reports";
    public static final String TABLE_USERS = "users";
    public static final String TABLE_APPOINTMENTS = "appointments";
    public static final String TABLE_PAYMENTS = "payments";
    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String TABLE_FEEDBACK = "feedback";

    // Patients Table Columns
    public static final String COL_PATIENT_ID = "patient_id";
    public static final String COL_PATIENT_NAME = "name";
    public static final String COL_PATIENT_AGE = "age";
    public static final String COL_PATIENT_GENDER = "gender";
    public static final String COL_PATIENT_PHONE = "phone";
    public static final String COL_PATIENT_EMAIL = "email";
    public static final String COL_PATIENT_ADDRESS = "address";
    public static final String COL_PATIENT_BLOOD_GROUP = "blood_group";

    // Medical Tests Table Columns
    public static final String COL_TEST_ID = "test_id";
    public static final String COL_TEST_NAME = "test_name";
    public static final String COL_TEST_CATEGORY = "category";
    public static final String COL_TEST_PRICE = "price";
    public static final String COL_TEST_NORMAL_RANGE = "normal_range";
    public static final String COL_TEST_DESCRIPTION = "description";

    // Lab Reports Table Columns
    public static final String COL_REPORT_ID = "report_id";
    public static final String COL_REPORT_PATIENT_ID = "patient_id";
    public static final String COL_REPORT_TEST_ID = "test_id";
    public static final String COL_REPORT_DATE = "test_date";
    public static final String COL_REPORT_RESULT = "result";
    public static final String COL_REPORT_REMARKS = "remarks";

    // Users Table Columns
    public static final String COL_USER_ID = "user_id";
    public static final String COL_USER_NAME = "name";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PHONE = "phone";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_DOB = "dob";
    public static final String COL_USER_GENDER = "gender";
    public static final String COL_USER_BLOOD_GROUP = "blood_group";
    public static final String COL_USER_ADDRESS = "address";
    public static final String COL_USER_CREATED_AT = "created_at";

    // Appointments Table Columns
    public static final String COL_APT_ID = "appointment_id";
    public static final String COL_APT_USER_ID = "user_id";
    public static final String COL_APT_PATIENT_ID = "patient_id";
    public static final String COL_APT_TEST_ID = "test_id";
    public static final String COL_APT_DATE = "appointment_date";
    public static final String COL_APT_TIME = "appointment_time";
    public static final String COL_APT_COLLECTION_TYPE = "collection_type";
    public static final String COL_APT_ADDRESS = "address";
    public static final String COL_APT_STATUS = "status";
    public static final String COL_APT_CREATED_AT = "created_at";

    // Payments Table Columns
    public static final String COL_PAY_ID = "payment_id";
    public static final String COL_PAY_USER_ID = "user_id";
    public static final String COL_PAY_APT_ID = "appointment_id";
    public static final String COL_PAY_AMOUNT = "amount";
    public static final String COL_PAY_METHOD = "payment_method";
    public static final String COL_PAY_TXN_ID = "transaction_id";
    public static final String COL_PAY_DATE = "payment_date";
    public static final String COL_PAY_STATUS = "status";

    // Notifications Table Columns
    public static final String COL_NOTIF_ID = "notification_id";
    public static final String COL_NOTIF_USER_ID = "user_id";
    public static final String COL_NOTIF_TITLE = "title";
    public static final String COL_NOTIF_MESSAGE = "message";
    public static final String COL_NOTIF_CREATED_AT = "created_at";
    public static final String COL_NOTIF_IS_READ = "is_read";

    // Feedback Table Columns
    public static final String COL_FB_ID = "feedback_id";
    public static final String COL_FB_USER_ID = "user_id";
    public static final String COL_FB_APT_ID = "appointment_id";
    public static final String COL_FB_RATING = "rating";
    public static final String COL_FB_COMMENT = "comment";
    public static final String COL_FB_CREATED_AT = "created_at";

    // SQL statement to create Patients table
    private static final String CREATE_TABLE_PATIENTS = "CREATE TABLE " + TABLE_PATIENTS + " ("
            + COL_PATIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_PATIENT_NAME + " TEXT NOT NULL, "
            + COL_PATIENT_AGE + " INTEGER, "
            + COL_PATIENT_GENDER + " TEXT, "
            + COL_PATIENT_PHONE + " TEXT, "
            + COL_PATIENT_EMAIL + " TEXT, "
            + COL_PATIENT_ADDRESS + " TEXT, "
            + COL_PATIENT_BLOOD_GROUP + " TEXT"
            + ");";

    // SQL statement to create Medical Tests table
    private static final String CREATE_TABLE_MEDICAL_TESTS = "CREATE TABLE " + TABLE_MEDICAL_TESTS + " ("
            + COL_TEST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_TEST_NAME + " TEXT NOT NULL, "
            + COL_TEST_CATEGORY + " TEXT, "
            + COL_TEST_PRICE + " REAL, "
            + COL_TEST_NORMAL_RANGE + " TEXT, "
            + COL_TEST_DESCRIPTION + " TEXT"
            + ");";

    // SQL statement to create Lab Reports table with Foreign Keys
    private static final String CREATE_TABLE_LAB_REPORTS = "CREATE TABLE " + TABLE_LAB_REPORTS + " ("
            + COL_REPORT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_REPORT_PATIENT_ID + " INTEGER NOT NULL, "
            + COL_REPORT_TEST_ID + " INTEGER NOT NULL, "
            + COL_REPORT_DATE + " TEXT, "
            + COL_REPORT_RESULT + " TEXT, "
            + COL_REPORT_REMARKS + " TEXT, "
            + "FOREIGN KEY (" + COL_REPORT_PATIENT_ID + ") REFERENCES " + TABLE_PATIENTS + "(" + COL_PATIENT_ID + ") ON DELETE CASCADE, "
            + "FOREIGN KEY (" + COL_REPORT_TEST_ID + ") REFERENCES " + TABLE_MEDICAL_TESTS + "(" + COL_TEST_ID + ") ON DELETE CASCADE"
            + ");";

    // SQL statement to create Users table
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " ("
            + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_USER_NAME + " TEXT NOT NULL, "
            + COL_USER_EMAIL + " TEXT UNIQUE NOT NULL, "
            + COL_USER_PHONE + " TEXT UNIQUE NOT NULL, "
            + COL_USER_PASSWORD + " TEXT NOT NULL, "
            + COL_USER_DOB + " TEXT, "
            + COL_USER_GENDER + " TEXT, "
            + COL_USER_BLOOD_GROUP + " TEXT, "
            + COL_USER_ADDRESS + " TEXT, "
            + COL_USER_CREATED_AT + " TEXT"
            + ");";

    // SQL statement to create Appointments table
    private static final String CREATE_TABLE_APPOINTMENTS = "CREATE TABLE " + TABLE_APPOINTMENTS + " ("
            + COL_APT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_APT_USER_ID + " INTEGER NOT NULL, "
            + COL_APT_PATIENT_ID + " INTEGER NOT NULL, "
            + COL_APT_TEST_ID + " INTEGER NOT NULL, "
            + COL_APT_DATE + " TEXT NOT NULL, "
            + COL_APT_TIME + " TEXT NOT NULL, "
            + COL_APT_COLLECTION_TYPE + " TEXT NOT NULL, "
            + COL_APT_ADDRESS + " TEXT, "
            + COL_APT_STATUS + " TEXT NOT NULL, "
            + COL_APT_CREATED_AT + " TEXT, "
            + "FOREIGN KEY (" + COL_APT_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ") ON DELETE CASCADE, "
            + "FOREIGN KEY (" + COL_APT_PATIENT_ID + ") REFERENCES " + TABLE_PATIENTS + "(" + COL_PATIENT_ID + ") ON DELETE CASCADE, "
            + "FOREIGN KEY (" + COL_APT_TEST_ID + ") REFERENCES " + TABLE_MEDICAL_TESTS + "(" + COL_TEST_ID + ") ON DELETE CASCADE"
            + ");";

    // SQL statement to create Payments table
    private static final String CREATE_TABLE_PAYMENTS = "CREATE TABLE " + TABLE_PAYMENTS + " ("
            + COL_PAY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_PAY_USER_ID + " INTEGER NOT NULL, "
            + COL_PAY_APT_ID + " INTEGER NOT NULL, "
            + COL_PAY_AMOUNT + " REAL NOT NULL, "
            + COL_PAY_METHOD + " TEXT, "
            + COL_PAY_TXN_ID + " TEXT, "
            + COL_PAY_DATE + " TEXT, "
            + COL_PAY_STATUS + " TEXT, "
            + "FOREIGN KEY (" + COL_PAY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ") ON DELETE CASCADE, "
            + "FOREIGN KEY (" + COL_PAY_APT_ID + ") REFERENCES " + TABLE_APPOINTMENTS + "(" + COL_APT_ID + ") ON DELETE CASCADE"
            + ");";

    // SQL statement to create Notifications table
    private static final String CREATE_TABLE_NOTIFICATIONS = "CREATE TABLE " + TABLE_NOTIFICATIONS + " ("
            + COL_NOTIF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_NOTIF_USER_ID + " INTEGER NOT NULL, "
            + COL_NOTIF_TITLE + " TEXT NOT NULL, "
            + COL_NOTIF_MESSAGE + " TEXT NOT NULL, "
            + COL_NOTIF_CREATED_AT + " TEXT, "
            + COL_NOTIF_IS_READ + " INTEGER DEFAULT 0, "
            + "FOREIGN KEY (" + COL_NOTIF_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ") ON DELETE CASCADE"
            + ");";

    // SQL statement to create Feedback table
    private static final String CREATE_TABLE_FEEDBACK = "CREATE TABLE " + TABLE_FEEDBACK + " ("
            + COL_FB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_FB_USER_ID + " INTEGER NOT NULL, "
            + COL_FB_APT_ID + " INTEGER, "
            + COL_FB_RATING + " INTEGER, "
            + COL_FB_COMMENT + " TEXT, "
            + COL_FB_CREATED_AT + " TEXT, "
            + "FOREIGN KEY (" + COL_FB_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ") ON DELETE CASCADE"
            + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_PATIENTS);
            db.execSQL(CREATE_TABLE_MEDICAL_TESTS);
            db.execSQL(CREATE_TABLE_LAB_REPORTS);
            db.execSQL(CREATE_TABLE_USERS);
            db.execSQL(CREATE_TABLE_APPOINTMENTS);
            db.execSQL(CREATE_TABLE_PAYMENTS);
            db.execSQL(CREATE_TABLE_NOTIFICATIONS);
            db.execSQL(CREATE_TABLE_FEEDBACK);
            Log.d(TAG, "All tables created successfully in " + DATABASE_NAME);
        } catch (Exception e) {
            Log.e(TAG, "Error creating database tables", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        if (oldVersion < 2) {
            try {
                db.execSQL(CREATE_TABLE_USERS);
                db.execSQL(CREATE_TABLE_APPOINTMENTS);
                db.execSQL(CREATE_TABLE_PAYMENTS);
                db.execSQL(CREATE_TABLE_NOTIFICATIONS);
                db.execSQL(CREATE_TABLE_FEEDBACK);
                Log.d(TAG, "User side tables successfully added in v2 upgrade.");
            } catch (Exception e) {
                Log.e(TAG, "Error upgrading database to v2", e);
            }
        }
    }

    // =========================================================================
    // PATIENT CRUD OPERATIONS (ADMIN SIDE)
    // =========================================================================

    public long addPatient(Patient patient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PATIENT_NAME, patient.getName());
        values.put(COL_PATIENT_AGE, patient.getAge());
        values.put(COL_PATIENT_GENDER, patient.getGender());
        values.put(COL_PATIENT_PHONE, patient.getPhone());
        values.put(COL_PATIENT_EMAIL, patient.getEmail());
        values.put(COL_PATIENT_ADDRESS, patient.getAddress());
        values.put(COL_PATIENT_BLOOD_GROUP, patient.getBloodGroup());
        return db.insert(TABLE_PATIENTS, null, values);
    }

    public Patient getPatient(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_PATIENTS, null,
                    COL_PATIENT_ID + "=?", new String[]{String.valueOf(id)},
                    null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return extractPatientFromCursor(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching patient with id: " + id, e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    public Patient getPatientById(int id) {
        return getPatient(id);
    }

    public Patient getPatientByPhone(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_PATIENTS, null,
                    COL_PATIENT_PHONE + "=?", new String[]{phone},
                    null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return extractPatientFromCursor(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching patient by phone: " + phone, e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    public ArrayList<Patient> getAllPatients() {
        ArrayList<Patient> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_PATIENTS, null, null, null, null, null, COL_PATIENT_NAME + " ASC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(extractPatientFromCursor(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching all patients", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    public ArrayList<Patient> searchPatients(String keyword) {
        ArrayList<Patient> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String selection = COL_PATIENT_NAME + " LIKE ? OR " + COL_PATIENT_PHONE + " LIKE ? OR " + COL_PATIENT_BLOOD_GROUP + " LIKE ?";
            String pattern = "%" + keyword + "%";
            String[] args = new String[]{pattern, pattern, pattern};
            cursor = db.query(TABLE_PATIENTS, null, selection, args, null, null, COL_PATIENT_NAME + " ASC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(extractPatientFromCursor(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error searching patients with keyword: " + keyword, e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    public int updatePatient(Patient patient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PATIENT_NAME, patient.getName());
        values.put(COL_PATIENT_AGE, patient.getAge());
        values.put(COL_PATIENT_GENDER, patient.getGender());
        values.put(COL_PATIENT_PHONE, patient.getPhone());
        values.put(COL_PATIENT_EMAIL, patient.getEmail());
        values.put(COL_PATIENT_ADDRESS, patient.getAddress());
        values.put(COL_PATIENT_BLOOD_GROUP, patient.getBloodGroup());
        return db.update(TABLE_PATIENTS, values, COL_PATIENT_ID + "=?", new String[]{String.valueOf(patient.getPatientId())});
    }

    public int deletePatient(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PATIENTS, COL_PATIENT_ID + "=?", new String[]{String.valueOf(id)});
    }

    private Patient extractPatientFromCursor(Cursor cursor) {
        Patient p = new Patient();
        p.setPatientId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_PATIENT_ID)));
        p.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_PATIENT_NAME)));
        p.setAge(cursor.getInt(cursor.getColumnIndexOrThrow(COL_PATIENT_AGE)));
        p.setGender(cursor.getString(cursor.getColumnIndexOrThrow(COL_PATIENT_GENDER)));
        p.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COL_PATIENT_PHONE)));
        p.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_PATIENT_EMAIL)));
        p.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(COL_PATIENT_ADDRESS)));
        p.setBloodGroup(cursor.getString(cursor.getColumnIndexOrThrow(COL_PATIENT_BLOOD_GROUP)));
        return p;
    }

    // =========================================================================
    // MEDICAL TEST CRUD OPERATIONS
    // =========================================================================

    public long addMedicalTest(MedicalTest test) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TEST_NAME, test.getTestName());
        values.put(COL_TEST_CATEGORY, test.getCategory());
        values.put(COL_TEST_PRICE, test.getPrice());
        values.put(COL_TEST_NORMAL_RANGE, test.getNormalRange());
        values.put(COL_TEST_DESCRIPTION, test.getDescription());
        return db.insert(TABLE_MEDICAL_TESTS, null, values);
    }

    public MedicalTest getMedicalTest(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_MEDICAL_TESTS, null,
                    COL_TEST_ID + "=?", new String[]{String.valueOf(id)},
                    null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return extractMedicalTestFromCursor(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching medical test with id: " + id, e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    public MedicalTest getMedicalTestById(int id) {
        return getMedicalTest(id);
    }

    public ArrayList<MedicalTest> getAllMedicalTests() {
        ArrayList<MedicalTest> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_MEDICAL_TESTS, null, null, null, null, null, COL_TEST_NAME + " ASC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(extractMedicalTestFromCursor(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching all medical tests", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    public ArrayList<MedicalTest> searchMedicalTests(String keyword) {
        ArrayList<MedicalTest> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String selection = COL_TEST_NAME + " LIKE ? OR " + COL_TEST_CATEGORY + " LIKE ?";
            String pattern = "%" + keyword + "%";
            String[] args = new String[]{pattern, pattern};
            cursor = db.query(TABLE_MEDICAL_TESTS, null, selection, args, null, null, COL_TEST_NAME + " ASC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(extractMedicalTestFromCursor(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error searching tests: " + keyword, e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    public ArrayList<MedicalTest> filterMedicalTests(String category, String keyword) {
        ArrayList<MedicalTest> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String selection;
            String[] args;
            boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
            boolean hasCategory = category != null && !category.equalsIgnoreCase("All");

            if (hasCategory && hasKeyword) {
                selection = COL_TEST_CATEGORY + "=? AND (" + COL_TEST_NAME + " LIKE ? OR " + COL_TEST_DESCRIPTION + " LIKE ?)";
                String pat = "%" + keyword.trim() + "%";
                args = new String[]{category, pat, pat};
            } else if (hasCategory) {
                selection = COL_TEST_CATEGORY + "=?";
                args = new String[]{category};
            } else if (hasKeyword) {
                selection = COL_TEST_NAME + " LIKE ? OR " + COL_TEST_CATEGORY + " LIKE ? OR " + COL_TEST_DESCRIPTION + " LIKE ?";
                String pat = "%" + keyword.trim() + "%";
                args = new String[]{pat, pat, pat};
            } else {
                selection = null;
                args = null;
            }

            cursor = db.query(TABLE_MEDICAL_TESTS, null, selection, args, null, null, COL_TEST_NAME + " ASC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(extractMedicalTestFromCursor(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error filtering tests", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    public int updateMedicalTest(MedicalTest test) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TEST_NAME, test.getTestName());
        values.put(COL_TEST_CATEGORY, test.getCategory());
        values.put(COL_TEST_PRICE, test.getPrice());
        values.put(COL_TEST_NORMAL_RANGE, test.getNormalRange());
        values.put(COL_TEST_DESCRIPTION, test.getDescription());
        return db.update(TABLE_MEDICAL_TESTS, values, COL_TEST_ID + "=?", new String[]{String.valueOf(test.getTestId())});
    }

    public int deleteMedicalTest(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_MEDICAL_TESTS, COL_TEST_ID + "=?", new String[]{String.valueOf(id)});
    }

    private MedicalTest extractMedicalTestFromCursor(Cursor cursor) {
        MedicalTest t = new MedicalTest();
        t.setTestId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_TEST_ID)));
        t.setTestName(cursor.getString(cursor.getColumnIndexOrThrow(COL_TEST_NAME)));
        t.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_TEST_CATEGORY)));
        t.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_TEST_PRICE)));
        t.setNormalRange(cursor.getString(cursor.getColumnIndexOrThrow(COL_TEST_NORMAL_RANGE)));
        t.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_TEST_DESCRIPTION)));
        return t;
    }

    // =========================================================================
    // LAB REPORT CRUD OPERATIONS (WITH RELATIONAL JOINS)
    // =========================================================================

    public long addReport(LabReport report) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_REPORT_PATIENT_ID, report.getPatientId());
        values.put(COL_REPORT_TEST_ID, report.getTestId());
        values.put(COL_REPORT_DATE, report.getTestDate());
        values.put(COL_REPORT_RESULT, report.getResult());
        values.put(COL_REPORT_REMARKS, report.getRemarks());
        long id = db.insert(TABLE_LAB_REPORTS, null, values);

        // Also create a notification for user if linked to patient
        if (id > 0) {
            try {
                User user = getUserByPatientId(report.getPatientId());
                if (user != null) {
                    addNotification(new NotificationItem(user.getUserId(),
                            "Lab Report Ready",
                            "Your laboratory report is now available for viewing and PDF download.",
                            report.getTestDate()));
                }
            } catch (Exception ignored) {
            }
        }
        return id;
    }

    public LabReport getReport(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String sql = "SELECT r." + COL_REPORT_ID + ", r." + COL_REPORT_PATIENT_ID + ", r." + COL_REPORT_TEST_ID + ", "
                    + "r." + COL_REPORT_DATE + ", r." + COL_REPORT_RESULT + ", r." + COL_REPORT_REMARKS + ", "
                    + "p." + COL_PATIENT_NAME + ", p." + COL_PATIENT_AGE + ", p." + COL_PATIENT_GENDER + ", "
                    + "p." + COL_PATIENT_PHONE + ", p." + COL_PATIENT_BLOOD_GROUP + ", "
                    + "t." + COL_TEST_NAME + ", t." + COL_TEST_CATEGORY + ", t." + COL_TEST_PRICE + ", "
                    + "t." + COL_TEST_NORMAL_RANGE + " "
                    + "FROM " + TABLE_LAB_REPORTS + " r "
                    + "INNER JOIN " + TABLE_PATIENTS + " p ON r." + COL_REPORT_PATIENT_ID + " = p." + COL_PATIENT_ID + " "
                    + "INNER JOIN " + TABLE_MEDICAL_TESTS + " t ON r." + COL_REPORT_TEST_ID + " = t." + COL_TEST_ID + " "
                    + "WHERE r." + COL_REPORT_ID + " = ?";

            cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});
            if (cursor != null && cursor.moveToFirst()) {
                return extractJoinedReportFromCursor(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching joined report id: " + id, e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    public LabReport getReportById(int id) {
        return getReport(id);
    }

    public LabReport getLabReport(int id) {
        return getReport(id);
    }

    public ArrayList<LabReport> getReportsByPatientId(int patientId) {
        return getReportsByPatient(patientId);
    }

    public ArrayList<LabReport> getAllReports() {
        ArrayList<LabReport> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String sql = "SELECT r." + COL_REPORT_ID + ", r." + COL_REPORT_PATIENT_ID + ", r." + COL_REPORT_TEST_ID + ", "
                    + "r." + COL_REPORT_DATE + ", r." + COL_REPORT_RESULT + ", r." + COL_REPORT_REMARKS + ", "
                    + "p." + COL_PATIENT_NAME + ", p." + COL_PATIENT_AGE + ", p." + COL_PATIENT_GENDER + ", "
                    + "p." + COL_PATIENT_PHONE + ", p." + COL_PATIENT_BLOOD_GROUP + ", "
                    + "t." + COL_TEST_NAME + ", t." + COL_TEST_CATEGORY + ", t." + COL_TEST_PRICE + ", "
                    + "t." + COL_TEST_NORMAL_RANGE + " "
                    + "FROM " + TABLE_LAB_REPORTS + " r "
                    + "INNER JOIN " + TABLE_PATIENTS + " p ON r." + COL_REPORT_PATIENT_ID + " = p." + COL_PATIENT_ID + " "
                    + "INNER JOIN " + TABLE_MEDICAL_TESTS + " t ON r." + COL_REPORT_TEST_ID + " = t." + COL_TEST_ID + " "
                    + "ORDER BY r." + COL_REPORT_ID + " DESC";

            cursor = db.rawQuery(sql, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(extractJoinedReportFromCursor(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching all joined reports", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    public ArrayList<LabReport> getReportsByPatient(int patientId) {
        ArrayList<LabReport> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String sql = "SELECT r." + COL_REPORT_ID + ", r." + COL_REPORT_PATIENT_ID + ", r." + COL_REPORT_TEST_ID + ", "
                    + "r." + COL_REPORT_DATE + ", r." + COL_REPORT_RESULT + ", r." + COL_REPORT_REMARKS + ", "
                    + "p." + COL_PATIENT_NAME + ", p." + COL_PATIENT_AGE + ", p." + COL_PATIENT_GENDER + ", "
                    + "p." + COL_PATIENT_PHONE + ", p." + COL_PATIENT_BLOOD_GROUP + ", "
                    + "t." + COL_TEST_NAME + ", t." + COL_TEST_CATEGORY + ", t." + COL_TEST_PRICE + ", "
                    + "t." + COL_TEST_NORMAL_RANGE + " "
                    + "FROM " + TABLE_LAB_REPORTS + " r "
                    + "INNER JOIN " + TABLE_PATIENTS + " p ON r." + COL_REPORT_PATIENT_ID + " = p." + COL_PATIENT_ID + " "
                    + "INNER JOIN " + TABLE_MEDICAL_TESTS + " t ON r." + COL_REPORT_TEST_ID + " = t." + COL_TEST_ID + " "
                    + "WHERE r." + COL_REPORT_PATIENT_ID + " = ? "
                    + "ORDER BY r." + COL_REPORT_ID + " DESC";

            cursor = db.rawQuery(sql, new String[]{String.valueOf(patientId)});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(extractJoinedReportFromCursor(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching reports for patient: " + patientId, e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    public ArrayList<LabReport> searchReports(String keyword) {
        ArrayList<LabReport> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String sql = "SELECT r." + COL_REPORT_ID + ", r." + COL_REPORT_PATIENT_ID + ", r." + COL_REPORT_TEST_ID + ", "
                    + "r." + COL_REPORT_DATE + ", r." + COL_REPORT_RESULT + ", r." + COL_REPORT_REMARKS + ", "
                    + "p." + COL_PATIENT_NAME + ", p." + COL_PATIENT_AGE + ", p." + COL_PATIENT_GENDER + ", "
                    + "p." + COL_PATIENT_PHONE + ", p." + COL_PATIENT_BLOOD_GROUP + ", "
                    + "t." + COL_TEST_NAME + ", t." + COL_TEST_CATEGORY + ", t." + COL_TEST_PRICE + ", "
                    + "t." + COL_TEST_NORMAL_RANGE + " "
                    + "FROM " + TABLE_LAB_REPORTS + " r "
                    + "INNER JOIN " + TABLE_PATIENTS + " p ON r." + COL_REPORT_PATIENT_ID + " = p." + COL_PATIENT_ID + " "
                    + "INNER JOIN " + TABLE_MEDICAL_TESTS + " t ON r." + COL_REPORT_TEST_ID + " = t." + COL_TEST_ID + " "
                    + "WHERE p." + COL_PATIENT_NAME + " LIKE ? OR t." + COL_TEST_NAME + " LIKE ? OR CAST(r." + COL_REPORT_ID + " AS TEXT) LIKE ? "
                    + "ORDER BY r." + COL_REPORT_ID + " DESC";

            String pat = "%" + keyword + "%";
            cursor = db.rawQuery(sql, new String[]{pat, pat, pat});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(extractJoinedReportFromCursor(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error searching reports: " + keyword, e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    public int updateReport(LabReport report) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_REPORT_PATIENT_ID, report.getPatientId());
        values.put(COL_REPORT_TEST_ID, report.getTestId());
        values.put(COL_REPORT_DATE, report.getTestDate());
        values.put(COL_REPORT_RESULT, report.getResult());
        values.put(COL_REPORT_REMARKS, report.getRemarks());
        return db.update(TABLE_LAB_REPORTS, values, COL_REPORT_ID + "=?", new String[]{String.valueOf(report.getReportId())});
    }

    public int deleteReport(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_LAB_REPORTS, COL_REPORT_ID + "=?", new String[]{String.valueOf(id)});
    }

    private LabReport extractJoinedReportFromCursor(Cursor cursor) {
        LabReport r = new LabReport();
        r.setReportId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_REPORT_ID)));
        r.setPatientId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_REPORT_PATIENT_ID)));
        r.setTestId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_REPORT_TEST_ID)));
        r.setTestDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_REPORT_DATE)));
        r.setResult(cursor.getString(cursor.getColumnIndexOrThrow(COL_REPORT_RESULT)));
        r.setRemarks(cursor.getString(cursor.getColumnIndexOrThrow(COL_REPORT_REMARKS)));

        r.setPatientName(cursor.getString(cursor.getColumnIndexOrThrow(COL_PATIENT_NAME)));
        r.setPatientAge(cursor.getInt(cursor.getColumnIndexOrThrow(COL_PATIENT_AGE)));
        r.setPatientGender(cursor.getString(cursor.getColumnIndexOrThrow(COL_PATIENT_GENDER)));
        r.setPatientPhone(cursor.getString(cursor.getColumnIndexOrThrow(COL_PATIENT_PHONE)));
        r.setPatientBloodGroup(cursor.getString(cursor.getColumnIndexOrThrow(COL_PATIENT_BLOOD_GROUP)));

        r.setTestName(cursor.getString(cursor.getColumnIndexOrThrow(COL_TEST_NAME)));
        r.setTestCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_TEST_CATEGORY)));
        r.setTestPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_TEST_PRICE)));
        r.setNormalRange(cursor.getString(cursor.getColumnIndexOrThrow(COL_TEST_NORMAL_RANGE)));
        return r;
    }

    // =========================================================================
    // USER / PATIENT AUTHENTICATION & MANAGEMENT
    // =========================================================================

    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, user.getName());
        values.put(COL_USER_EMAIL, user.getEmail().trim().toLowerCase(Locale.ROOT));
        values.put(COL_USER_PHONE, user.getPhone().trim());
        values.put(COL_USER_PASSWORD, user.getPassword());
        values.put(COL_USER_DOB, user.getDob());
        values.put(COL_USER_GENDER, user.getGender());
        values.put(COL_USER_BLOOD_GROUP, user.getBloodGroup());
        values.put(COL_USER_ADDRESS, user.getAddress());

        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        values.put(COL_USER_CREATED_AT, now);

        long userId = db.insert(TABLE_USERS, null, values);

        if (userId > 0) {
            // Also link or create corresponding record in patients table
            Patient existingPatient = getPatientByPhone(user.getPhone().trim());
            int patientId;
            if (existingPatient == null) {
                Patient p = new Patient(user.getName(), 25, user.getGender(),
                        user.getPhone().trim(), user.getEmail().trim().toLowerCase(Locale.ROOT),
                        user.getAddress(), user.getBloodGroup());
                patientId = (int) addPatient(p);
            } else {
                patientId = existingPatient.getPatientId();
            }

            // Create welcome notification
            addNotification(new NotificationItem((int) userId,
                    "Welcome to MEDILAB",
                    "Your patient account has been successfully created. You can now book diagnostic tests and access lab reports.",
                    new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())));
        }
        return userId;
    }

    public User authenticateUser(String emailOrPhone, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String clean = emailOrPhone.trim().toLowerCase(Locale.ROOT);
            String sql = "SELECT * FROM " + TABLE_USERS + " WHERE ("
                    + COL_USER_EMAIL + " = ? OR " + COL_USER_PHONE + " = ?) AND "
                    + COL_USER_PASSWORD + " = ?";
            cursor = db.rawQuery(sql, new String[]{clean, clean, password});
            if (cursor != null && cursor.moveToFirst()) {
                return extractUserFromCursor(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error authenticating user", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    public boolean checkUserExists(String emailOrPhone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String clean = emailOrPhone.trim().toLowerCase(Locale.ROOT);
            String sql = "SELECT " + COL_USER_ID + " FROM " + TABLE_USERS + " WHERE "
                    + COL_USER_EMAIL + " = ? OR " + COL_USER_PHONE + " = ?";
            cursor = db.rawQuery(sql, new String[]{clean, clean});
            return cursor != null && cursor.getCount() > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error checking if user exists", e);
            return false;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USERS, null, COL_USER_ID + "=?",
                    new String[]{String.valueOf(userId)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return extractUserFromCursor(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user by id: " + userId, e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    public User getUserByPatientId(int patientId) {
        Patient p = getPatient(patientId);
        if (p == null) return null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USERS, null, COL_USER_PHONE + "=? OR " + COL_USER_EMAIL + "=?",
                    new String[]{p.getPhone(), p.getEmail()}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return extractUserFromCursor(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user by patient id: " + patientId, e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, user.getName());
        values.put(COL_USER_PHONE, user.getPhone());
        values.put(COL_USER_DOB, user.getDob());
        values.put(COL_USER_GENDER, user.getGender());
        values.put(COL_USER_BLOOD_GROUP, user.getBloodGroup());
        values.put(COL_USER_ADDRESS, user.getAddress());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            values.put(COL_USER_PASSWORD, user.getPassword());
        }
        return db.update(TABLE_USERS, values, COL_USER_ID + "=?", new String[]{String.valueOf(user.getUserId())});
    }

    public boolean updateUserProfile(int userId, String name, String phone, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, name);
        values.put(COL_USER_PHONE, phone);
        values.put(COL_USER_ADDRESS, address);
        int rows = db.update(TABLE_USERS, values, COL_USER_ID + "=?", new String[]{String.valueOf(userId)});
        return rows > 0;
    }

    private User extractUserFromCursor(Cursor cursor) {
        User u = new User();
        u.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)));
        u.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME)));
        u.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL)));
        u.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PHONE)));
        u.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PASSWORD)));
        u.setDob(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_DOB)));
        u.setGender(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_GENDER)));
        u.setBloodGroup(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_BLOOD_GROUP)));
        u.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ADDRESS)));
        u.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_CREATED_AT)));
        return u;
    }

    // =========================================================================
    // APPOINTMENTS (SAMPLE COLLECTION)
    // =========================================================================

    public long addAppointment(Appointment apt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_APT_USER_ID, apt.getUserId());
        values.put(COL_APT_PATIENT_ID, apt.getPatientId());
        values.put(COL_APT_TEST_ID, apt.getTestId());
        values.put(COL_APT_DATE, apt.getAppointmentDate());
        values.put(COL_APT_TIME, apt.getAppointmentTime());
        values.put(COL_APT_COLLECTION_TYPE, apt.getCollectionType());
        values.put(COL_APT_ADDRESS, apt.getAddress());
        values.put(COL_APT_STATUS, apt.getStatus());

        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        values.put(COL_APT_CREATED_AT, now);

        long aptId = db.insert(TABLE_APPOINTMENTS, null, values);

        if (aptId > 0) {
            addNotification(new NotificationItem(apt.getUserId(),
                    "Appointment Booked",
                    "Your lab sample collection appointment is scheduled for " + apt.getAppointmentDate() + " at " + apt.getAppointmentTime() + ".",
                    apt.getAppointmentDate()));
        }
        return aptId;
    }

    public Appointment getAppointmentById(int appointmentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String sql = "SELECT a.*, p." + COL_PATIENT_NAME + ", p." + COL_PATIENT_PHONE + ", "
                    + "t." + COL_TEST_NAME + ", t." + COL_TEST_CATEGORY + ", t." + COL_TEST_PRICE + " "
                    + "FROM " + TABLE_APPOINTMENTS + " a "
                    + "INNER JOIN " + TABLE_PATIENTS + " p ON a." + COL_APT_PATIENT_ID + " = p." + COL_PATIENT_ID + " "
                    + "INNER JOIN " + TABLE_MEDICAL_TESTS + " t ON a." + COL_APT_TEST_ID + " = t." + COL_TEST_ID + " "
                    + "WHERE a." + COL_APT_ID + " = ?";

            cursor = db.rawQuery(sql, new String[]{String.valueOf(appointmentId)});
            if (cursor != null && cursor.moveToFirst()) {
                return extractJoinedAppointmentFromCursor(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching appointment: " + appointmentId, e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    public ArrayList<Appointment> getAppointmentsByUser(int userId, String statusFilter) {
        ArrayList<Appointment> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String sql = "SELECT a.*, p." + COL_PATIENT_NAME + ", p." + COL_PATIENT_PHONE + ", "
                    + "t." + COL_TEST_NAME + ", t." + COL_TEST_CATEGORY + ", t." + COL_TEST_PRICE + " "
                    + "FROM " + TABLE_APPOINTMENTS + " a "
                    + "INNER JOIN " + TABLE_PATIENTS + " p ON a." + COL_APT_PATIENT_ID + " = p." + COL_PATIENT_ID + " "
                    + "INNER JOIN " + TABLE_MEDICAL_TESTS + " t ON a." + COL_APT_TEST_ID + " = t." + COL_TEST_ID + " "
                    + "WHERE a." + COL_APT_USER_ID + " = ? ";

            if (statusFilter != null && !statusFilter.equalsIgnoreCase("All")) {
                sql += "AND a." + COL_APT_STATUS + " = ? ";
                sql += "ORDER BY a." + COL_APT_ID + " DESC";
                cursor = db.rawQuery(sql, new String[]{String.valueOf(userId), statusFilter});
            } else {
                sql += "ORDER BY a." + COL_APT_ID + " DESC";
                cursor = db.rawQuery(sql, new String[]{String.valueOf(userId)});
            }

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(extractJoinedAppointmentFromCursor(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching user appointments", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    public int rescheduleAppointment(int appointmentId, String newDate, String newTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_APT_DATE, newDate);
        values.put(COL_APT_TIME, newTime);
        int rows = db.update(TABLE_APPOINTMENTS, values, COL_APT_ID + "=?", new String[]{String.valueOf(appointmentId)});

        if (rows > 0) {
            Appointment apt = getAppointmentById(appointmentId);
            if (apt != null) {
                addNotification(new NotificationItem(apt.getUserId(),
                        "Appointment Rescheduled",
                        "Your sample collection appointment has been rescheduled to " + newDate + " at " + newTime + ".",
                        newDate));
            }
        }
        return rows;
    }

    public int cancelAppointment(int appointmentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_APT_STATUS, "Cancelled");
        int rows = db.update(TABLE_APPOINTMENTS, values, COL_APT_ID + "=?", new String[]{String.valueOf(appointmentId)});

        if (rows > 0) {
            Appointment apt = getAppointmentById(appointmentId);
            if (apt != null) {
                addNotification(new NotificationItem(apt.getUserId(),
                        "Appointment Cancelled",
                        "Your appointment #" + appointmentId + " for " + apt.getTestName() + " has been cancelled.",
                        new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())));
            }
        }
        return rows;
    }

    public int updateAppointmentStatus(int appointmentId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_APT_STATUS, status);
        return db.update(TABLE_APPOINTMENTS, values, COL_APT_ID + "=?", new String[]{String.valueOf(appointmentId)});
    }

    private Appointment extractJoinedAppointmentFromCursor(Cursor cursor) {
        Appointment a = new Appointment();
        a.setAppointmentId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_APT_ID)));
        a.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_APT_USER_ID)));
        a.setPatientId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_APT_PATIENT_ID)));
        a.setTestId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_APT_TEST_ID)));
        a.setAppointmentDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_APT_DATE)));
        a.setAppointmentTime(cursor.getString(cursor.getColumnIndexOrThrow(COL_APT_TIME)));
        a.setCollectionType(cursor.getString(cursor.getColumnIndexOrThrow(COL_APT_COLLECTION_TYPE)));
        a.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(COL_APT_ADDRESS)));
        a.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COL_APT_STATUS)));
        a.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COL_APT_CREATED_AT)));

        a.setPatientName(cursor.getString(cursor.getColumnIndexOrThrow(COL_PATIENT_NAME)));
        a.setPatientPhone(cursor.getString(cursor.getColumnIndexOrThrow(COL_PATIENT_PHONE)));
        a.setTestName(cursor.getString(cursor.getColumnIndexOrThrow(COL_TEST_NAME)));
        a.setTestCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_TEST_CATEGORY)));
        a.setTestPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_TEST_PRICE)));
        return a;
    }

    // =========================================================================
    // PAYMENTS (SIMULATED TRANSACTIONS)
    // =========================================================================

    public long addPayment(Payment payment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PAY_USER_ID, payment.getUserId());
        values.put(COL_PAY_APT_ID, payment.getAppointmentId());
        values.put(COL_PAY_AMOUNT, payment.getAmount());
        values.put(COL_PAY_METHOD, payment.getPaymentMethod());
        values.put(COL_PAY_TXN_ID, payment.getTransactionId());
        values.put(COL_PAY_DATE, payment.getPaymentDate());
        values.put(COL_PAY_STATUS, payment.getStatus());

        long payId = db.insert(TABLE_PAYMENTS, null, values);

        if (payId > 0) {
            String title = payment.getStatus().equalsIgnoreCase("Paid") ? "Payment Successful" : "Payment Pending";
            String msg = "Payment of ₹" + payment.getAmount() + " via " + payment.getPaymentMethod()
                    + " (Txn: " + payment.getTransactionId() + ") was recorded.";
            addNotification(new NotificationItem(payment.getUserId(), title, msg, payment.getPaymentDate()));
        }
        return payId;
    }

    public ArrayList<Payment> getPaymentsByUser(int userId) {
        ArrayList<Payment> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String sql = "SELECT py.*, t." + COL_TEST_NAME + ", p." + COL_PATIENT_NAME + ", a." + COL_APT_DATE + " "
                    + "FROM " + TABLE_PAYMENTS + " py "
                    + "INNER JOIN " + TABLE_APPOINTMENTS + " a ON py." + COL_PAY_APT_ID + " = a." + COL_APT_ID + " "
                    + "INNER JOIN " + TABLE_MEDICAL_TESTS + " t ON a." + COL_APT_TEST_ID + " = t." + COL_TEST_ID + " "
                    + "INNER JOIN " + TABLE_PATIENTS + " p ON a." + COL_APT_PATIENT_ID + " = p." + COL_PATIENT_ID + " "
                    + "WHERE py." + COL_PAY_USER_ID + " = ? "
                    + "ORDER BY py." + COL_PAY_ID + " DESC";

            cursor = db.rawQuery(sql, new String[]{String.valueOf(userId)});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(extractJoinedPaymentFromCursor(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching user payments", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    public Payment getPaymentByTransactionId(String txnId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String sql = "SELECT py.*, t." + COL_TEST_NAME + ", p." + COL_PATIENT_NAME + ", a." + COL_APT_DATE + " "
                    + "FROM " + TABLE_PAYMENTS + " py "
                    + "INNER JOIN " + TABLE_APPOINTMENTS + " a ON py." + COL_PAY_APT_ID + " = a." + COL_APT_ID + " "
                    + "INNER JOIN " + TABLE_MEDICAL_TESTS + " t ON a." + COL_APT_TEST_ID + " = t." + COL_TEST_ID + " "
                    + "INNER JOIN " + TABLE_PATIENTS + " p ON a." + COL_APT_PATIENT_ID + " = p." + COL_PATIENT_ID + " "
                    + "WHERE py." + COL_PAY_TXN_ID + " = ?";

            cursor = db.rawQuery(sql, new String[]{txnId});
            if (cursor != null && cursor.moveToFirst()) {
                return extractJoinedPaymentFromCursor(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching payment by txn: " + txnId, e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    private Payment extractJoinedPaymentFromCursor(Cursor cursor) {
        Payment p = new Payment();
        p.setPaymentId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_PAY_ID)));
        p.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_PAY_USER_ID)));
        p.setAppointmentId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_PAY_APT_ID)));
        p.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PAY_AMOUNT)));
        p.setPaymentMethod(cursor.getString(cursor.getColumnIndexOrThrow(COL_PAY_METHOD)));
        p.setTransactionId(cursor.getString(cursor.getColumnIndexOrThrow(COL_PAY_TXN_ID)));
        p.setPaymentDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_PAY_DATE)));
        p.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COL_PAY_STATUS)));

        p.setTestName(cursor.getString(cursor.getColumnIndexOrThrow(COL_TEST_NAME)));
        p.setPatientName(cursor.getString(cursor.getColumnIndexOrThrow(COL_PATIENT_NAME)));
        p.setAppointmentDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_APT_DATE)));
        return p;
    }

    // =========================================================================
    // USER-RESTRICTED LAB REPORTS (PRIVACY-ENFORCED IN SQL)
    // =========================================================================

    /**
     * Retrieves ONLY reports belonging to the specified logged-in user.
     * Enforced at the SQLite JOIN query level.
     */
    public ArrayList<LabReport> getReportsForUser(int userId, String userPhone, String userEmail) {
        ArrayList<LabReport> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String sql = "SELECT r." + COL_REPORT_ID + ", r." + COL_REPORT_PATIENT_ID + ", r." + COL_REPORT_TEST_ID + ", "
                    + "r." + COL_REPORT_DATE + ", r." + COL_REPORT_RESULT + ", r." + COL_REPORT_REMARKS + ", "
                    + "p." + COL_PATIENT_NAME + ", p." + COL_PATIENT_AGE + ", p." + COL_PATIENT_GENDER + ", "
                    + "p." + COL_PATIENT_PHONE + ", p." + COL_PATIENT_BLOOD_GROUP + ", "
                    + "t." + COL_TEST_NAME + ", t." + COL_TEST_CATEGORY + ", t." + COL_TEST_PRICE + ", "
                    + "t." + COL_TEST_NORMAL_RANGE + " "
                    + "FROM " + TABLE_LAB_REPORTS + " r "
                    + "INNER JOIN " + TABLE_PATIENTS + " p ON r." + COL_REPORT_PATIENT_ID + " = p." + COL_PATIENT_ID + " "
                    + "INNER JOIN " + TABLE_MEDICAL_TESTS + " t ON r." + COL_REPORT_TEST_ID + " = t." + COL_TEST_ID + " "
                    + "WHERE (p." + COL_PATIENT_PHONE + " = ? OR p." + COL_PATIENT_EMAIL + " = ? OR "
                    + "r." + COL_REPORT_PATIENT_ID + " IN (SELECT a." + COL_APT_PATIENT_ID + " FROM " + TABLE_APPOINTMENTS + " a WHERE a." + COL_APT_USER_ID + " = ?)) "
                    + "ORDER BY r." + COL_REPORT_ID + " DESC";

            cursor = db.rawQuery(sql, new String[]{userPhone, userEmail, String.valueOf(userId)});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(extractJoinedReportFromCursor(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching user-specific reports", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    /**
     * Security check: Verifies if a specific report belongs to this user before viewing/downloading.
     */
    public boolean isReportOwnedByUser(int reportId, int userId, String userPhone, String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String sql = "SELECT r." + COL_REPORT_ID + " "
                    + "FROM " + TABLE_LAB_REPORTS + " r "
                    + "INNER JOIN " + TABLE_PATIENTS + " p ON r." + COL_REPORT_PATIENT_ID + " = p." + COL_PATIENT_ID + " "
                    + "WHERE r." + COL_REPORT_ID + " = ? AND ("
                    + "p." + COL_PATIENT_PHONE + " = ? OR p." + COL_PATIENT_EMAIL + " = ? OR "
                    + "r." + COL_REPORT_PATIENT_ID + " IN (SELECT a." + COL_APT_PATIENT_ID + " FROM " + TABLE_APPOINTMENTS + " a WHERE a." + COL_APT_USER_ID + " = ?))";

            cursor = db.rawQuery(sql, new String[]{String.valueOf(reportId), userPhone, userEmail, String.valueOf(userId)});
            return cursor != null && cursor.moveToFirst();
        } catch (Exception e) {
            Log.e(TAG, "Error checking report ownership", e);
            return false;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    // =========================================================================
    // NOTIFICATIONS
    // =========================================================================

    public long addNotification(NotificationItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOTIF_USER_ID, item.getUserId());
        values.put(COL_NOTIF_TITLE, item.getTitle());
        values.put(COL_NOTIF_MESSAGE, item.getMessage());
        values.put(COL_NOTIF_CREATED_AT, item.getCreatedAt());
        values.put(COL_NOTIF_IS_READ, item.isRead() ? 1 : 0);
        return db.insert(TABLE_NOTIFICATIONS, null, values);
    }

    public ArrayList<NotificationItem> getNotificationsByUser(int userId) {
        ArrayList<NotificationItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NOTIFICATIONS, null, COL_NOTIF_USER_ID + "=?",
                    new String[]{String.valueOf(userId)}, null, null, COL_NOTIF_ID + " DESC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    NotificationItem item = new NotificationItem();
                    item.setNotificationId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_NOTIF_ID)));
                    item.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_NOTIF_USER_ID)));
                    item.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTIF_TITLE)));
                    item.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTIF_MESSAGE)));
                    item.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTIF_CREATED_AT)));
                    item.setRead(cursor.getInt(cursor.getColumnIndexOrThrow(COL_NOTIF_IS_READ)) == 1);
                    list.add(item);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching user notifications", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    public int getUnreadNotificationCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String sql = "SELECT COUNT(*) FROM " + TABLE_NOTIFICATIONS + " WHERE "
                    + COL_NOTIF_USER_ID + " = ? AND " + COL_NOTIF_IS_READ + " = 0";
            cursor = db.rawQuery(sql, new String[]{String.valueOf(userId)});
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error counting unread notifications", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return 0;
    }

    public int markAllNotificationsRead(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOTIF_IS_READ, 1);
        return db.update(TABLE_NOTIFICATIONS, values, COL_NOTIF_USER_ID + "=?", new String[]{String.valueOf(userId)});
    }

    public int markNotificationsAsRead(int userId) {
        return markAllNotificationsRead(userId);
    }

    public int deleteNotification(int notifId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NOTIFICATIONS, COL_NOTIF_ID + "=?", new String[]{String.valueOf(notifId)});
    }

    // =========================================================================
    // FEEDBACK
    // =========================================================================

    public long addFeedback(FeedbackItem feedback) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FB_USER_ID, feedback.getUserId());
        values.put(COL_FB_APT_ID, feedback.getAppointmentId());
        values.put(COL_FB_RATING, feedback.getRating());
        values.put(COL_FB_COMMENT, feedback.getComment());
        values.put(COL_FB_CREATED_AT, feedback.getCreatedAt());
        return db.insert(TABLE_FEEDBACK, null, values);
    }

    public ArrayList<FeedbackItem> getFeedbackByUser(int userId) {
        ArrayList<FeedbackItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_FEEDBACK, null, COL_FB_USER_ID + "=?",
                    new String[]{String.valueOf(userId)}, null, null, COL_FB_ID + " DESC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    FeedbackItem item = new FeedbackItem();
                    item.setFeedbackId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_FB_ID)));
                    item.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_FB_USER_ID)));
                    item.setAppointmentId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_FB_APT_ID)));
                    item.setRating(cursor.getInt(cursor.getColumnIndexOrThrow(COL_FB_RATING)));
                    item.setComment(cursor.getString(cursor.getColumnIndexOrThrow(COL_FB_COMMENT)));
                    item.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COL_FB_CREATED_AT)));
                    list.add(item);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching user feedback", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    // =========================================================================
    // STATISTICAL COUNTS
    // =========================================================================

    public int getPatientCount() {
        return getCount(TABLE_PATIENTS);
    }

    public int getMedicalTestCount() {
        return getCount(TABLE_MEDICAL_TESTS);
    }

    public int getTestCount() {
        return getCount(TABLE_MEDICAL_TESTS);
    }

    public int getReportCount() {
        return getCount(TABLE_LAB_REPORTS);
    }

    public int getUserAppointmentCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_APPOINTMENTS + " WHERE " + COL_APT_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)});
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error counting user appointments", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return 0;
    }

    public int getUserPaymentCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PAYMENTS + " WHERE " + COL_PAY_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)});
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error counting user payments", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return 0;
    }

    public int getTodayReportCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int count = 0;
        try {
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String sql = "SELECT COUNT(*) FROM " + TABLE_LAB_REPORTS + " WHERE " + COL_REPORT_DATE + " = ?";
            cursor = db.rawQuery(sql, new String[]{today});
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting today's report count", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return count;
    }

    private int getCount(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null);
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error counting table " + tableName, e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return count;
    }

    // =========================================================================
    // SAMPLE DATA POPULATOR FOR BCA DEMONSTRATIONS
    // =========================================================================

    public void seedSampleDataIfEmpty() {
        if (getPatientCount() == 0) {
            // Seed Patients
            long p1 = addPatient(new Patient("Rahul Patel", 28, "Male", "9876543210", "rahul@example.com", "42 Navrangpura, Ahmedabad", "B+"));
            long p2 = addPatient(new Patient("Priya Shah", 34, "Female", "9876543211", "priya.shah@example.com", "15 SG Highway, Ahmedabad", "O+"));
            long p3 = addPatient(new Patient("Amit Desai", 45, "Male", "9876543212", "amit.desai@example.com", "88 Alkapuri, Vadodara", "A+"));

            // Seed Medical Tests
            long t1 = addMedicalTest(new MedicalTest("Complete Blood Count (CBC)", "Hematology", 450.0, "Hb: 13-17 g/dL, WBC: 4000-11000 /cumm, Platelets: 1.5-4.5 lakh", "Evaluates overall health and detects wide range of disorders including anemia and infection."));
            long t2 = addMedicalTest(new MedicalTest("Fasting Blood Sugar (FBS)", "Biochemistry", 120.0, "70 - 99 mg/dL", "Measures blood glucose after an overnight fast to screen for prediabetes and diabetes."));
            long t3 = addMedicalTest(new MedicalTest("Hemoglobin (Hb)", "Hematology", 150.0, "Male: 13.5 - 17.5 g/dL, Female: 12.0 - 15.5 g/dL", "Oxygen-carrying protein in red blood cells."));
            long t4 = addMedicalTest(new MedicalTest("Lipid Profile", "Biochemistry", 850.0, "Total Chol: < 200 mg/dL, Triglycerides: < 150 mg/dL, HDL: > 40 mg/dL", "Comprehensive screening for cardiovascular risk and coronary heart disease."));
            long t5 = addMedicalTest(new MedicalTest("Thyroid Profile (T3, T4, TSH)", "Immunology", 650.0, "TSH: 0.4 - 4.0 uIU/mL, Total T3: 80 - 200 ng/dL, Total T4: 5.0 - 12.0 ug/dL", "Evaluates thyroid gland function and metabolic health."));

            // Seed Sample Reports connecting Patients & Tests
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            if (p1 > 0 && t1 > 0) {
                addReport(new LabReport((int) p1, (int) t1, today, "Hb: 14.2 g/dL, RBC: 4.8 mil/cumm, WBC: 6,800 /cumm, Platelets: 2.8 lakh/cumm. All blood parameters within normal limits.", "Sample normal. No evidence of anemia or acute leukocytosis."));
            }
            if (p2 > 0 && t2 > 0) {
                addReport(new LabReport((int) p2, (int) t2, today, "Fasting Glucose: 92 mg/dL.", "Normoglycemic fasting glucose level. Recommended regular routine checkup in 6 months."));
            }
            if (p3 > 0 && t3 > 0) {
                addReport(new LabReport((int) p3, (int) t3, today, "Hemoglobin: 11.4 g/dL (Mildly Low).", "Mild microcytic anemia indicated. Dietary iron rich diet and follow-up recommended."));
            }

            // Seed default patient user for test account
            if (checkUserExists("rahul@example.com") == false) {
                User demoUser = new User("Rahul Patel", "rahul@example.com", "9876543210", "user123",
                        "1996-05-14", "Male", "B+", "42 Navrangpura, Ahmedabad");
                long uId = addUser(demoUser);

                // Seed a demo upcoming appointment for Rahul
                if (uId > 0 && p1 > 0 && t1 > 0) {
                    Appointment apt = new Appointment((int) 0, (int) uId, (int) p1, (int) t1,
                            "2026-09-20", "10:30 AM", "Home Sample Collection",
                            "42 Navrangpura, Ahmedabad", "Confirmed", today);
                    long aptId = addAppointment(apt);
                    if (aptId > 0) {
                        Payment pay = new Payment(0, (int) uId, (int) aptId, 450.0, "UPI", "TXN202609180001", today, "Paid");
                        addPayment(pay);
                    }
                }
            }
        }
    }
}
