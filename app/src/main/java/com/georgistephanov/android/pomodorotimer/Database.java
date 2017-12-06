package com.georgistephanov.android.pomodorotimer;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

/**
 * Created by Georgi on 04-Dec-17.
 */

public class Database {
	private static DatabaseHelper databaseHelper;

	/**
	 * Creates a database helper instance the first time it is requested with an
	 * application context so that it could be used from any activity. Used to get an
	 * instance of the database helper class.
	 * @param context any context from which the application context is taken
	 * @return the only database helper instance
	 */
	public static DatabaseHelper getInstance(Context context) {
		if (databaseHelper == null) {
			databaseHelper = new DatabaseHelper(context.getApplicationContext());
		}

		return databaseHelper;
	}

	private Database(Context context) {
		databaseHelper = new DatabaseHelper(context.getApplicationContext());
	}

	/**
	 * Inserts a row in the task table with the task name, length and whether it has
	 * finished or has been terminated early passed as a ContentValues parameter.
	 * @param cv the content values to be inserted in the task table
	 */
	public static void insert(ContentValues cv) {
		new InsertTask().doInBackground(cv);
	}

	public static void updateSettings(ContentValues cv) {
		new UpdateSettings().doInBackground(cv);
	}

	/**
	 * Gets and returns the settings from the settings table of the database.
	 * @return Cursor containing the result set of the query
	 */
	public static Cursor getSettings() {
		return new GetSettings().doInBackground();
	}

	public static Cursor getTasks() {
		return new GetTasks().doInBackground();
	}


	/**
	 * Class that extends AsyncTask to get the settings from the database on a background
	 * thread and return them to the main activity. It is used on activity creation and
	 * the doInBackground method returns a cursor with the two values for the task and break
	 * lengths respectively.
	 */
	private final static class GetSettings extends AsyncTask<Void, Void, Cursor> {
		@Override
		protected Cursor doInBackground(Void... voids) {
			return databaseHelper
					.getReadableDatabase()
					.rawQuery("SELECT * FROM settings", null);
		}
	}

	private final static class GetTasks extends AsyncTask<Void, Void, Cursor> {
		@Override
		protected Cursor doInBackground(Void... voids) {
			return databaseHelper
					.getReadableDatabase()
					.rawQuery("SELECT * FROM task", null);
		}
	}

	private final static class UpdateSettings extends AsyncTask<ContentValues, Void, Void> {
		@Override
		protected Void doInBackground(ContentValues... contentValues) {
			// Delete the current settings
			databaseHelper.getWritableDatabase()
					.execSQL("DELETE FROM settings");

			// Insert the new settings
			databaseHelper.getWritableDatabase()
					.insert(databaseHelper.getSettingsTable(), null, contentValues[0]);

			return null;
		}
	}

	private final static class InsertTask extends AsyncTask<ContentValues, Void, Void> {
		@Override
		protected Void doInBackground(ContentValues... contentValues) {
			databaseHelper.getWritableDatabase()
					.insert(databaseHelper.getTaskTable(), null, contentValues[0]);

			return null;
		}
	}
}
