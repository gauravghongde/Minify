package com.rstack.dephone;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsHelper {
    private static final String DAILY_LIMIT_PREF = "dailyLimit";
    private static final String CONT_LIMIT_PREF = "contLimit";

    private static final String DATA_FOR_DAY_PREF = "dataForDay";
    private static final String DATA_FOR_WEEK_PREF = "dataForWeek";
    private static final String PAUSE_SWITCH_PREF = "pauseSwitch";
    private static final String IS_DISMISSED_PREF = "isDismissed";
    private static SharedPrefsHelper instance;
    private SharedPreferences dailyLimit;
    private SharedPreferences contLimit;
    private SharedPreferences dataForDay;
    private SharedPreferences dataForWeek;
    private SharedPreferences pauseSwitch;
    private SharedPreferences isAppAlertDismissed;

    private SharedPrefsHelper(Context context) {
        dataForDay = context.getSharedPreferences(DATA_FOR_DAY_PREF, Context.MODE_PRIVATE);
        dataForWeek = context.getSharedPreferences(DATA_FOR_WEEK_PREF, Context.MODE_PRIVATE);
        dailyLimit = context.getSharedPreferences(DAILY_LIMIT_PREF, Context.MODE_PRIVATE);
        contLimit = context.getSharedPreferences(CONT_LIMIT_PREF, Context.MODE_PRIVATE);
        pauseSwitch = context.getSharedPreferences(PAUSE_SWITCH_PREF, Context.MODE_PRIVATE);
        isAppAlertDismissed = context.getSharedPreferences(IS_DISMISSED_PREF, Context.MODE_PRIVATE);
    }

    public static SharedPrefsHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefsHelper(context);
        }
        return instance;
    }

    public void setDailyLimit(String packageName, int limit) {
        SharedPreferences.Editor edit = dailyLimit.edit();
        edit.putInt(packageName, limit);
        edit.apply();
    }

    public void setContLimit(String packageName, int limit) {
        SharedPreferences.Editor edit = contLimit.edit();
        edit.putInt(packageName, limit);
        edit.apply();
    }

    public int getDailyLimit(String packageName) {
        return dailyLimit.getInt(packageName, Integer.MAX_VALUE);
    }

    public int getContLimit(String packageName) {
        return contLimit.getInt(packageName, Integer.MAX_VALUE);
    }

    public void setDataForDay(String packageName, int limit) {
        SharedPreferences.Editor edit = dataForDay.edit();
        edit.putInt(packageName, limit);
        edit.apply();
    }

    public void setDataForWeek(String packageName, int limit) {
        SharedPreferences.Editor edit = dataForWeek.edit();
        edit.putInt(packageName, limit);
        edit.apply();
    }

    public int getPauseSwitch() {
        return pauseSwitch.getInt("chkStatus", -1);
    }
    public void setPauseSwitch(int value) {
        SharedPreferences.Editor edit = pauseSwitch.edit();
        edit.putInt("chkStatus", value);
        edit.apply();
    }

    public SharedPreferences getPauseSwitchPrefs() {
        return pauseSwitch;
    }

    public void setAlertDismissed(String packageName, boolean dismissed) {
        SharedPreferences.Editor editor = isAppAlertDismissed.edit();
        editor.putBoolean("alert_dismissed_" + packageName, dismissed);
        editor.apply();
    }

    public boolean isAlertDismissed(String packageName) {
        return isAppAlertDismissed.getBoolean("alert_dismissed_" + packageName, false);
    }

}