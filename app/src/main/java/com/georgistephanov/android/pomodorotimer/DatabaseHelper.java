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
	private static final int SCHEMA = 1;

	// The task table name and its columns
	private static final String TASK_TABLE = "task";
	private static final String TASK_NAME = "name";
	private static final String TASK_LENGTH = "length";
	private static final String TASK_COMPLETED = "completed";

	// The settings table name and its columns
	private static final String SETTINGS_TABLE = "settings";
	private static final String SETTINGS_TASK_LEGTH = "task_length";
	private static final String SETTINGS_SHORT_BREAK_LENGTH = "short_break_length";
	private static final String SETTINGS_LONG_BREAK_LENGTH = "long_break_length";
	private static final String SETTINGS_LONG_BREAK_AFTER = "long_break_after";

	// Default settings
	private static final int DEFAULT_TASK_LENGTH = 1500000;
	private static final int DEFAULT_BREAK_LENGTH = 300000;
	private static final int DEFAULT_LONG_BREAK_LENGTH = 600000;
	private static final int DEFAULT_LONG_BREAK_AFTER = 4;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		sqLiteDatabase.execSQL("CREATE TABLE task (name TEXT, length REAL NOT NULL, completed BYTE DEFAULT 0)");
		sqLiteDatabase.execSQL("CREATE TABLE settings (" +
				"task_length INTEGER NOT NULL," +
				"short_break_length INTEGER NOT NULL," +
				"long_break_length INTEGER NOT NULL," +
				"long_break_after INTEGER NOT NULL)");


		// Set the default values
		ContentValues contentValues = new ContentValues();
		contentValues.put(SETTINGS_TASK_LEGTH, DEFAULT_TASK_LENGTH);
		contentValues.put(SETTINGS_SHORT_BREAK_LENGTH, DEFAULT_BREAK_LENGTH);
		contentValues.put(SETTINGS_LONG_BREAK_LENGTH, DEFAULT_LONG_BREAK_LENGTH);
		contentValues.put(SETTINGS_LONG_BREAK_AFTER, DEFAULT_LONG_BREAK_AFTER);
		sqLiteDatabase.insert(SETTINGS_TABLE, null, contentValues);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
		// Drop the old settings table
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS settings");

		// Create a new settings table
		sqLiteDatabase.execSQL("CREATE TABLE settings (" +
				"task_length INTEGER NOT NULL," +
				"short_break_length INTEGER NOT NULL," +
				"long_break_length INTEGER NOT NULL," +
				"long_break_after INTEGER NOT NULL)");

		// Set the default values
		ContentValues contentValues = new ContentValues();
		contentValues.put(SETTINGS_TASK_LEGTH, DEFAULT_TASK_LENGTH);
		contentValues.put(SETTINGS_SHORT_BREAK_LENGTH, DEFAULT_BREAK_LENGTH);
		contentValues.put(SETTINGS_LONG_BREAK_LENGTH, DEFAULT_LONG_BREAK_LENGTH);
		contentValues.put(SETTINGS_LONG_BREAK_AFTER, DEFAULT_LONG_BREAK_AFTER);
		sqLiteDatabase.insert(SETTINGS_TABLE, null, contentValues);
	}


	public String getTaskTable() {
		return TASK_TABLE;
	}
	public String getTaskName() {
		return TASK_NAME;
	}
	public String getTaskLength() {
		return TASK_LENGTH;
	}
	public String getTaskCompleted() {
		return TASK_COMPLETED;
	}

	public String getSettingsTable() {
		return SETTINGS_TABLE;
	}
	public String getSettingsTaskLength() {
		return SETTINGS_TASK_LEGTH;
	}
	public String getSettingsBreakLength() {
		return SETTINGS_SHORT_BREAK_LENGTH;
	}

	public int getDefaultTaskLength() {
		return DEFAULT_TASK_LENGTH;
	}
	public int getDefaultBreakLength() {
		return DEFAULT_BREAK_LENGTH;
	}
}
