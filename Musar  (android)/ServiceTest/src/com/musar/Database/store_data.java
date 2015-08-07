package com.musar.Database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
/**
 * 
 * store the important data in shared preference
 *
 */
public class store_data {
	@SuppressWarnings("unused")
	public String getSavedData(String key,Context context) {
		SharedPreferences settings = context.getApplicationContext()
				.getSharedPreferences("save", 0);
		return settings.getString(key, null);
	}
	public boolean getSavedData_bool(String key,Context context) {
		SharedPreferences settings = context.getApplicationContext()
				.getSharedPreferences("save", 0);
		return settings.getBoolean(key, false);
	}
	public int getSavedData_int(String key,Context context) {
		SharedPreferences settings = context.getApplicationContext()
				.getSharedPreferences("save", 0);
		return settings.getInt(key, 0);
	}
	public float getSavedData_float(String key,Context context) {
		SharedPreferences settings = context.getApplicationContext()
				.getSharedPreferences("save", 0);
		return settings.getFloat(key, 0);
	}
	
	public void saveData(String key, float value,Context context) {
		SharedPreferences settings = context.getApplicationContext()
				.getSharedPreferences("save", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putFloat(key, value);
		editor.commit();
	}
	public void saveData(String key, int value,Context context) {
		SharedPreferences settings = context.getApplicationContext()
				.getSharedPreferences("save", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	public void saveData(String key, boolean value,Context context) {
		SharedPreferences settings = context.getApplicationContext()
				.getSharedPreferences("save", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public void saveData(String key, String value,Context context) {
		SharedPreferences settings = context.getApplicationContext()
				.getSharedPreferences("save", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, value);
		editor.commit();
	}
	public boolean getPrefSaveData(String key,Context context)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		// then you use
		return prefs.getBoolean(key, false);	
	}
	public void setPrefSaveData(String key,boolean value,Context context)
	{
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		// then you use
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
}
