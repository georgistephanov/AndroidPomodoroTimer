package com.georgistephanov.android.pomodorotimer;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Georgi on 04-Dec-17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "pomodoro";
	private static final int SCHEMA = 6;

	// The task table name and its columns
	private static final String TASK_TABLE = "task";
	private static final String TASK_NAME = "name";
	private static final String TASK_LENGTH = "length";
	private static final String TASK_DATE = "date";

	// The settings table name and its columns
	private static final String SETTINGS_TABLE = "settings";
	private static final String SETTINGS_TASK_LEGTH = "task_length";
	private static final String SETTINGS_SHORT_BREAK_LENGTH = "short_break_length";
	private static final String SETTINGS_LONG_BREAK_LENGTH = "long_break_length";
	private static final String SETTINGS_LONG_BREAK_AFTER = "long_break_after";
	private static final String SETTINGS_NOTIFICATIONS_VIBRATE = "vibrate";
	private static final String SETTINGS_NOTIFICATIONS_PLAY_SOUND = "play_sound";
	private static final String SETTINGS_KEEP_SCREEN = "keep_screen";
	private static final String SETTINGS_DISABLE_VIBR_SOUND = "dis_vibration_sound";
	private static final String SETTINGS_DISABLE_WIFI = "dis_wifi";
	private static final String SETTINGS_DARK_THEME = "dark_theme";

	// Default settings
	private static final int DEFAULT_TASK_LENGTH = 1500000;
	private static final int DEFAULT_SHORT_BREAK_LENGTH = 300000;
	private static final int DEFAULT_LONG_BREAK_LENGTH = 1200000;
	private static final int DEFAULT_LONG_BREAK_AFTER = 4;
	private static final int DEFAULT_NOTIFICATION_VIBRATE = 1;
	private static final int DEFAULT_NOTIFICATION_PLAY_SOUND = 1;
	private static final int DEFAULT_KEEP_SCREEN = 1;
	private static final int DEFAULT_DISABLE_VIBR_SOUND = 0;
	private static final int DEFAULT_DISABLE_WIFI = 0;
	private static final int DEFAULT_DARK_THEME = 0;

	DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		sqLiteDatabase.execSQL("CREATE TABLE task (" +
				"name 					TEXT," +
				"length 				INTEGER NOT NULL," +
				"date					LONG NOT NULL)");

		sqLiteDatabase.execSQL("CREATE TABLE settings (" +
				"task_length 			INTEGER NOT NULL," +
				"short_break_length 	INTEGER NOT NULL," +
				"long_break_length 		INTEGER NOT NULL," +
				"long_break_after 		INTEGER NOT NULL," +
				"vibrate 				INTEGER NOT NULL," +
				"play_sound 			INTEGER NOT NULL," +
				"keep_screen 			INTEGER NOT NULL," +
				"dis_vibration_sound 	INTEGER NOT NULL," +
				"dis_wifi 				INTEGER NOT NULL," +
				"dark_theme				INTEGER NOT NULL)");

		// Set the default values
		ContentValues contentValues = new ContentValues();
		contentValues.put(SETTINGS_TASK_LEGTH, DEFAULT_TASK_LENGTH);
		contentValues.put(SETTINGS_SHORT_BREAK_LENGTH, DEFAULT_SHORT_BREAK_LENGTH);
		contentValues.put(SETTINGS_LONG_BREAK_LENGTH, DEFAULT_LONG_BREAK_LENGTH);
		contentValues.put(SETTINGS_LONG_BREAK_AFTER, DEFAULT_LONG_BREAK_AFTER);
		contentValues.put(SETTINGS_NOTIFICATIONS_VIBRATE, DEFAULT_NOTIFICATION_VIBRATE);
		contentValues.put(SETTINGS_NOTIFICATIONS_PLAY_SOUND, DEFAULT_NOTIFICATION_PLAY_SOUND);
		contentValues.put(SETTINGS_KEEP_SCREEN, DEFAULT_KEEP_SCREEN);
		contentValues.put(SETTINGS_DISABLE_VIBR_SOUND, DEFAULT_DISABLE_VIBR_SOUND);
		contentValues.put(SETTINGS_DISABLE_WIFI, DEFAULT_DISABLE_WIFI);
		contentValues.put(SETTINGS_DARK_THEME, DEFAULT_DARK_THEME);
		sqLiteDatabase.insert(SETTINGS_TABLE, null, contentValues);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
		// Drop the old settings table
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS settings");

		// Create a new settings table
		sqLiteDatabase.execSQL("CREATE TABLE settings (" +
				"task_length 			INTEGER NOT NULL," +
				"short_break_length 	INTEGER NOT NULL," +
				"long_break_length 		INTEGER NOT NULL," +
				"long_break_after 		INTEGER NOT NULL," +
				"vibrate 				INTEGER NOT NULL," +
				"play_sound 			INTEGER NOT NULL," +
				"keep_screen 			INTEGER NOT NULL," +
				"dis_vibration_sound 	INTEGER NOT NULL," +
				"dis_wifi 				INTEGER NOT NULL," +
				"dark_theme				INTEGER NOT NULL)");

		// Set the default values
		ContentValues contentValues = new ContentValues();
		contentValues.put(SETTINGS_TASK_LEGTH, DEFAULT_TASK_LENGTH);
		contentValues.put(SETTINGS_SHORT_BREAK_LENGTH, DEFAULT_SHORT_BREAK_LENGTH);
		contentValues.put(SETTINGS_LONG_BREAK_LENGTH, DEFAULT_LONG_BREAK_LENGTH);
		contentValues.put(SETTINGS_LONG_BREAK_AFTER, DEFAULT_LONG_BREAK_AFTER);
		contentValues.put(SETTINGS_NOTIFICATIONS_VIBRATE, DEFAULT_NOTIFICATION_VIBRATE);
		contentValues.put(SETTINGS_NOTIFICATIONS_PLAY_SOUND, DEFAULT_NOTIFICATION_PLAY_SOUND);
		contentValues.put(SETTINGS_KEEP_SCREEN, DEFAULT_KEEP_SCREEN);
		contentValues.put(SETTINGS_DISABLE_VIBR_SOUND, DEFAULT_DISABLE_VIBR_SOUND);
		contentValues.put(SETTINGS_DISABLE_WIFI, DEFAULT_DISABLE_WIFI);
		contentValues.put(SETTINGS_DARK_THEME, DEFAULT_DARK_THEME);
		sqLiteDatabase.insert(SETTINGS_TABLE, null, contentValues);
	}


	String getTaskTableName() {
		return TASK_TABLE;
	}
	String getTaskNameColumnName() {
		return TASK_NAME;
	}
	String getTaskLengthColumnName() {
		return TASK_LENGTH;
	}
	String getTaskDateColumnName() {
		return TASK_DATE;
	}

	String getSettingsTableName() {
		return SETTINGS_TABLE;
	}
	String getSettingsTaskColumnName() {
		return SETTINGS_TASK_LEGTH;
	}
	String getSettingsShortBreakLengthColumnName() {
		return SETTINGS_SHORT_BREAK_LENGTH;
	}
	String getSettingsLongBreakLengthColumnName() {
		return SETTINGS_LONG_BREAK_LENGTH;
	}
	String getSettingsLongBreakAfterColumnName() {
		return SETTINGS_LONG_BREAK_AFTER;
	}
	String getSettingsNotificationsVibrate() {
		return SETTINGS_NOTIFICATIONS_VIBRATE;
	}
	String getSettingsNotificationsPlaySound() {
		return SETTINGS_NOTIFICATIONS_PLAY_SOUND;
	}
	String getSettingsKeepScreen() {
		return SETTINGS_KEEP_SCREEN;
	}
	String getSettingsDisableVibrationSound() {
		return SETTINGS_DISABLE_VIBR_SOUND;
	}
	String getSettingsDisableWifi() {
		return SETTINGS_DISABLE_WIFI;
	}
	String getSettingsDarkTheme() {
		return SETTINGS_DARK_THEME;
	}

	int getDefaultTaskLength() {
		return DEFAULT_TASK_LENGTH;
	}
	int getDefaultShortBreakLength() {
		return DEFAULT_SHORT_BREAK_LENGTH;
	}
	int getDefaultLongBreakLength() {
		return DEFAULT_LONG_BREAK_LENGTH;
	}
	int getDefaultLongBreakAfter() {
		return DEFAULT_LONG_BREAK_AFTER;
	}
}
