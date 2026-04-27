package com.carrental.utils;

import android.util.Patterns;

public class ValidationUtils {

    public static boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.length() >= 8 && phone.matches("[0-9]+");
    }

    public static boolean isValidPrice(double price) {
        return price > 0;
    }

    public static boolean isValidYear(int year) {
        return year >= 1900 && year <= java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
    }

    public static boolean isValidDateRange(String startDate, String endDate) {
        return startDate.compareTo(endDate) < 0;
    }
}