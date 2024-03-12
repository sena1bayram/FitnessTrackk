package com.example.fitnesstrack.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Locale;

public class SharedPrefencesEngine {
    public static final String ADMIN ="admin";
    public static final String ADMIN_PASSWORD ="admin_password";
    public static final String USER_LOGIN_PASSWORD="user_login_password";
    public static final String USER_LOGIN_MAIL="user_login_mail";
    public static final String SHARED_PREFS_NAME = "BugReportPrefs";
    public static final String BUG_REPORT_COUNT_KEY = "BugReportCount";
    public static final String LAST_REPORT_DATE_KEY = "LastReportDate";
    public static final String PATIENT_ID_KEY = "PatientId";
    public static final String APP_LANGUAGE = "app_language";
    public static final String SWITCH_STATE_KEY = "preference_switch_state";
    public static final String BACKGROUND_COLOR = "background_color";

    // Kullanıcıya özel günlük gönderim kotası anahtarlarının başlangıcı
    private static final String USER_BUG_REPORT_COUNT_KEY_PREFIX = "BugReportCount_";

    private static final String APP_USER_IMAGE = "user_image";

    public static SharedPrefencesEngine sharedInstance = null;


    public static SharedPrefencesEngine sharedInstance(Activity context) {

        sharedInstance = new SharedPrefencesEngine(context);
        return sharedInstance;
    }

    public static SharedPrefencesEngine sharedInstance(Context context) {

        sharedInstance = new SharedPrefencesEngine(context);
        return sharedInstance;
    }

    public static void clearSharedInstance() {
        sharedInstance = null;
    }

    private final SharedPreferences preferences;

    public SharedPrefencesEngine(Activity context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public SharedPrefencesEngine(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public String getAppLanguage(String key) {
        return preferences.getString(key, "en");
    }
    public void setAppLanguage(String key, String languageCode) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, languageCode);
        editor.commit();
    }

    public String getUserImageProfile(String key) {
        return preferences.getString(key, null);
    }
    public void setUserImageProfile(String key, String encodedImage) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, encodedImage);
        editor.commit();
    }

    public String getStringPreferences(String key) {
        return preferences.getString(key, "empty");
    }

    public void savePreferences(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void savePreferences(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void savePreferences(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public <T> void savePreferencesObject(String key, T object){
        SharedPreferences.Editor editor 	= preferences.edit();
        Gson gson 		= new Gson();
        String json 	= gson.toJson(object);
        editor.putString(key, json);
        editor.commit();
    }
    public int getBugReportCount() {
        return preferences.getInt(BUG_REPORT_COUNT_KEY, 0);
    }

    public void incrementBugReportCount() {
        int currentCount = getBugReportCount();
        preferences.edit().putInt(BUG_REPORT_COUNT_KEY, currentCount + 1).apply();
    }

    public int getLastReportDay() {
        return preferences.getInt(LAST_REPORT_DATE_KEY, 0);
    }

    public void updateLastReportDay() {
        int currentDayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        preferences.edit().putInt(LAST_REPORT_DATE_KEY, currentDayOfYear).apply();
    }
    public String getPatientId() {
        return preferences.getString(PATIENT_ID_KEY, "");
    }

    public void setPatientId(String patientId) {
        preferences.edit().putString(PATIENT_ID_KEY, patientId).apply();
    }
    public int getUserBugReportCount(String userKey) {
        return preferences.getInt(userKey, 0);
    }

    public void incrementUserBugReportCount(String userKey) {
        int currentCount = getUserBugReportCount(userKey);
        preferences.edit().putInt(userKey, currentCount + 1).apply();
    }

    public int getUserLastReportDay(String userKey) {
        return preferences.getInt(userKey + LAST_REPORT_DATE_KEY, 0);
    }

    public void updateUserLastReportDay(String userKey) {
        int currentDayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        preferences.edit().putInt(userKey + LAST_REPORT_DATE_KEY, currentDayOfYear).apply();
    }
    public boolean getBooleanPreferences(String key) {
        return preferences.getBoolean(key, false);
    }

    public void saveBackgroundColor(String key, int color) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, color);
        editor.commit();
    }

    public int getBackgroundColor(String key) {
        return preferences.getInt(key, Color.LTGRAY); // Varsayılan beyaz renk
    }

}

