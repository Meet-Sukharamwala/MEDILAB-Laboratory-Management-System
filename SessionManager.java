package com.example.medilab.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manages dual-role session (Staff / Admin vs Patient / User) using Android SharedPreferences.
 */
public class SessionManager {

    private static final String PREF_NAME = "MedilabSessionPref";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ROLE = "userRole"; // "ADMIN" or "USER"
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_PATIENT_ID = "patientId";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_PHONE = "userPhone";

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Creates admin login session (backward-compatible).
     */
    public void createLoginSession(String username) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ROLE, ROLE_ADMIN);
        editor.putString(KEY_USERNAME, username);
        editor.putInt(KEY_USER_ID, 0);
        editor.apply();
    }

    /**
     * Creates patient/user login session.
     */
    public void createUserSession(int userId, int patientId, String name, String email, String phone) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ROLE, ROLE_USER);
        editor.putInt(KEY_USER_ID, userId);
        editor.putInt(KEY_PATIENT_ID, patientId);
        editor.putString(KEY_USERNAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_PHONE, phone);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserRole() {
        return pref.getString(KEY_USER_ROLE, ROLE_ADMIN);
    }

    public boolean isAdmin() {
        return ROLE_ADMIN.equals(getUserRole());
    }

    public String getUsername() {
        return pref.getString(KEY_USERNAME, "User");
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, 0);
    }

    public int getPatientId() {
        return pref.getInt(KEY_PATIENT_ID, 0);
    }

    public void setPatientId(int patientId) {
        editor.putInt(KEY_PATIENT_ID, patientId);
        editor.apply();
    }

    public void updateUserInfo(String name, String phone) {
        editor.putString(KEY_USERNAME, name);
        editor.putString(KEY_USER_PHONE, phone);
        editor.apply();
    }

    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, "");
    }

    public String getUserPhone() {
        return pref.getString(KEY_USER_PHONE, "");
    }

    public void logoutUser() {
        editor.clear();
        editor.apply();
    }
}
