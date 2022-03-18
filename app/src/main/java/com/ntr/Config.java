package com.ntr;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by charleston on 13/03/14.
 */
public class Config {
	public static int getGapDays(Context c){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(c);
		return Integer.valueOf(sharedPrefs.getString("gap_days", "1"));
	}
	public static int getCheckTimes(Context c){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(c);
		return Integer.valueOf(sharedPrefs.getString("review_times","1"));
	}
    public static boolean getResizeText(Context c)
    {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(c);
        return sharedPrefs.getBoolean("resize_text",true);
    }
}
