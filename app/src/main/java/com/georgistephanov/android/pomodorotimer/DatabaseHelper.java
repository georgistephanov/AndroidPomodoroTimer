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

	static final String TASK_TABLE = "task";
	static final String TASK_NAME = "name";
	static final String TASK_LENGTH = "length";
	static final String TASK_COMPLETED = "completed";

	static final String SETTINGS_TABLE = "settings";
	static final String SETTINGS_TASK_LEGTH = "task_length";
	static final String SETTINGS_BREAK_LENGTH = "break_length";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		sqLiteDatabase.execSQL("CREATE TABLE task (name TEXT, length REAL NOT NULL, completed BYTE DEFAULT 0)");
		sqLiteDatabase.execSQL("CREATE TABLE settings (task_length INTEGER NOT NULL, break_length INTEGER NOT NULL)");

		ContentValues contentValues = new ContentValues();

		contentValues.put(SETTINGS_TASK_LEGTH, 15000);
		contentValues.put(SETTINGS_BREAK_LENGTH, 3000);
		sqLiteDatabase.insert(SETTINGS_TABLE, null, contentValues);
	}

	// TODO: This is to be removed as it stands for testing purposes
	@Override
	public void onOpen(SQLiteDatabase db) {
		db.delete(SETTINGS_TABLE, "? > 0", new String[] {"SETTINGS_TASK_LENGTH"});

		ContentValues contentValues = new ContentValues();
		contentValues.put(SETTINGS_TASK_LEGTH, 15000);
		contentValues.put(SETTINGS_BREAK_LENGTH, 3000);

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
