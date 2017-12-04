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
	private static final String SETTINGS_BREAK_LENGTH = "break_length";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		sqLiteDatabase.execSQL("CREATE TABLE task (name TEXT, length REAL NOT NULL, completed BYTE DEFAULT 0)");
		sqLiteDatabase.execSQL("CREATE TABLE settings (task_length INTEGER NOT NULL, break_length INTEGER NOT NULL)");

		ContentValues contentValues = new ContentValues();

		contentValues.put(SETTINGS_TASK_LEGTH, 25000);
		contentValues.put(SETTINGS_BREAK_LENGTH, 5000);
		sqLiteDatabase.insert(SETTINGS_TABLE, null, contentValues);
	}

	// TODO: This is to be removed as it stands for testing purposes
	@Override
	public void onOpen(SQLiteDatabase db) {
		db.delete(SETTINGS_TABLE, "? > 0", new String[] {"SETTINGS_TASK_LENGTH"});

		ContentValues contentValues = new ContentValues();
		contentValues.put(SETTINGS_TASK_LEGTH, 12000);
		contentValues.put(SETTINGS_BREAK_LENGTH, 4000);

		db.insert(SETTINGS_TABLE, null, contentValues);

		super.onOpen(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
		throw new RuntimeException("No upgrades have been done to the database.");
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
		return SETTINGS_BREAK_LENGTH;
	}
}
