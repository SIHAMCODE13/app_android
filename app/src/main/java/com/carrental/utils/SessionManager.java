package com.carrental.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "CarRentalSession";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "role";
    private static final String KEY_CLIENT_ID = "client_id";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(int userId, String username, String role, int clientId) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_ROLE, role);
        editor.putInt(KEY_CLIENT_ID, clientId);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    public String getUsername() {
        return pref.getString(KEY_USERNAME, null);
    }

    public String getRole() {
        return pref.getString(KEY_ROLE, null);
    }

    public int getClientId() {
        return pref.getInt(KEY_CLIENT_ID, -1);
    }

    public boolean isAdmin() {
        return getRole() != null && getRole().equals("Administrateur");
    }

    public boolean isEmployee() {
        return getRole() != null && getRole().equals("Employé");
    }

    public boolean isClient() {
        return getRole() != null && getRole().equals("Client");
    }

    public boolean canDelete() {
        return isAdmin();
    }

    public boolean canModify() {
        return isAdmin() || isEmployee();
    }

    public boolean canViewAll() {
        return isAdmin() || isEmployee();
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}