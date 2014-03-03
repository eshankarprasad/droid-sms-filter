package com.devdroid.smsfilter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Class contains methods for handling shared preference access
 * 
 * @author qainfotech
 * 
 */
public class SharedPreferencesUtil {

	/**
	 * Method saves string in shared preference
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveStringPreferences(Context context, String key,
			String value) {
		SharedPreferences sPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sPrefs.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * Method saves integer in shared preference
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveIntegerPreferences(Context context, String key,
			Integer value) {
		SharedPreferences sPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sPrefs.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	/**
	 * Method saves boolean in shared preference
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveBooleanPreferences(Context context, String key,
			Boolean value) {
		SharedPreferences sPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sPrefs.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * Method deletes entry of key from shared preference
	 * 
	 * @param context
	 * @param key
	 */
	public static void deletePreferences(Context context, String key) {
		SharedPreferences sPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sPrefs.edit();
		editor.remove(key);
		editor.commit();
	}

	/**
	 * Method fetches boolean value from key in shared preference
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public static Boolean getBooleanPreferences(Context context, String key) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		Boolean savedPref = sharedPreferences.getBoolean(key, false);
		return savedPref;
	}

	/**
	 * Method fetches String value from key in shared preference
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public static String getStringPreferences(Context context, String key) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String savedPref = sharedPreferences.getString(key, "");
		return savedPref;
	}

	/**
	 * Method fetches Integer value from key in shared preference
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public static Integer getIntegerPreferences(Context context, String key) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		Integer savedPref = sharedPreferences.getInt(key, 0);
		return savedPref;
	}

	/**
	 * Clear all shared preference entries
	 * 
	 * @param context
	 */
	public static void clearAllSharedPreferencesEntries(Context context) {
		SharedPreferences sPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor sEdit = sPrefs.edit();
		sEdit.clear();
		sEdit.commit();
	}
}
