package com.arkada38.eventlogger.Model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.arkada38.eventlogger.R;

public class Settings {
    static public Activity activity;
    static SharedPreferences sPref;
    static public int periodIndex;
    static public boolean waitingPayment = false;

    static public void setPeriod(int period) {
        sPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt("period", period);
        ed.apply();
    }

    static public void setAccess(boolean access) {
        sPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean("access", access);
        ed.apply();
    }

    static public int getPeriod() {
        sPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sPref.getInt("period", 0);
    }

    static public CharSequence[] getItems() {
        CharSequence[] items = {
                Settings.activity.getString(R.string.for_all_time),
                Settings.activity.getString(R.string.for_7_days),
                Settings.activity.getString(R.string.for_week),
                Settings.activity.getString(R.string.for_24_hours),
                Settings.activity.getString(R.string.for_day),
                Settings.activity.getString(R.string.for_hour)
        };
        return items;
    }

    static public boolean getAccess() {
        sPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sPref.getBoolean("access", false);
    }
}