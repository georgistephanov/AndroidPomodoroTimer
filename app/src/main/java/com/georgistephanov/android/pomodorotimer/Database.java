package com.georgistephanov.android.pomodorotimer;

import android.app.Application;
import android.content.Context;

/**
 * Created by Georgi on 04-Dec-17.
 */

public class Database {
	private static DatabaseHelper databaseHelper;

	public static DatabaseHelper getInstance(Context context) {
		if (databaseHelper == null) {
			databaseHelper = new DatabaseHelper(context);
		}

		return databaseHelper;
	}

	private Database(Context context) {
		databaseHelper = new DatabaseHelper(context.getApplicationContext());
	}
}
